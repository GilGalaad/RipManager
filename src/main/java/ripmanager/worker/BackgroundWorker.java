package ripmanager.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ripmanager.common.CommonUtils;
import ripmanager.engine.Eac3toParser;
import ripmanager.engine.dto.*;
import ripmanager.engine.enums.AudioCodec;
import ripmanager.engine.enums.Language;
import ripmanager.gui.RipManagerImpl;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ripmanager.common.CommonUtils.calcEta;

@Log4j2
@RequiredArgsConstructor
public class BackgroundWorker extends SwingWorker<WorkerOutcome, Void> {

    private static final Pattern EAC3TOPROGRESS_PATTERN = Pattern.compile("(analyze|process): (?<progress>\\d+)%");
    private static final Pattern MKVINFO_CHAPTERS_PATTERN = Pattern.compile("^\\|\\+ Chapters$", Pattern.MULTILINE);

    // class init params
    private final WorkerCommand command;
    private final String source;
    private final List<Track> tracks;
    private final RipManagerImpl frame;

    // local variables
    private final StringBuilder output = new StringBuilder();

    @Override
    protected WorkerOutcome doInBackground() throws Exception {
        switch (command) {
            case ANALYZE:
                return doAnalyze();
            case PRINT_COMMANDS:
                List<List<String>> commands = doPrintCommands();
                List<String> lines = commands.stream().map(i -> String.join(" ", i)).collect(Collectors.toList());
                String output = String.join(System.lineSeparator(), lines);
                return new WorkerOutcome(WorkerOutcome.Status.OK, output, null);
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    protected void done() {
        try {
            WorkerOutcome ret = get();
            if (command == WorkerCommand.ANALYZE) {
                frame.analyzeTaskCallback(ret);
            } else if (command == WorkerCommand.PRINT_COMMANDS) {
                frame.printCommandsTaskCallback(ret);
            }
        } catch (CancellationException | InterruptedException ex) {
            frame.endBackgroundTask();
        } catch (ExecutionException ex) {
            frame.exceptionTaskCallback(ex);
        }
    }

    private WorkerOutcome doAnalyze() throws Exception {
        // analyzing file with eac3to
        List<String> args = new ArrayList<>();
        if (command == WorkerCommand.DEMUX) {
            args.add("python.exe");
            args.add("c:\\dvd-rip\\python\\run_cmd.py");
        }
        args.add("eac3to.exe");
        args.add(source);
        args.add("-log=NUL");
        args.add("-progressnumbers");

        ProcessOutcome eac3toOutcome = runEac3to(args);
        if (eac3toOutcome.getExitCode() != 0) {
            return new WorkerOutcome(WorkerOutcome.Status.KO, output.toString(), null);
        }

        // parsing output
        List<Track> parsedTracks = Eac3toParser.parse(eac3toOutcome.getStdout());
        if (parsedTracks.isEmpty()) {
            return new WorkerOutcome(WorkerOutcome.Status.KO, output.toString(), parsedTracks);
        }

        // if chapters are found, we are done
        if (parsedTracks.stream().anyMatch(i -> i.getType() == TrackType.CHAPTERS)) {
            return new WorkerOutcome(WorkerOutcome.Status.OK, output.toString(), parsedTracks);
        }
        // otherwise, looking for chapters with mkvinfo
        output.append(System.lineSeparator());
        ProcessOutcome mkvinfoOutcome = runMkvinfo();
        if (mkvinfoOutcome.getExitCode() != 0) {
            return new WorkerOutcome(WorkerOutcome.Status.KO, output.toString(), null);
        }
        if (MKVINFO_CHAPTERS_PATTERN.matcher(mkvinfoOutcome.getStdout()).find()) {
            ChaptersTrack track = new ChaptersTrack(parsedTracks.stream().mapToInt(Track::getIndex).max().orElse(0) + 1, "Chapters");
            track.setProperties(new ChaptersProperties(true));
            track.setDemuxOptions(new DemuxOptions(true));
            parsedTracks.add(track);
        }

        parsedTracks.forEach(log::info);
        return new WorkerOutcome(WorkerOutcome.Status.OK, output.toString(), parsedTracks);
    }

    private List<List<String>> doPrintCommands() {
        List<List<String>> ret = new ArrayList<>();
        List<String> eac3to = new ArrayList<>();
        eac3to.add("eac3to.exe");
        eac3to.add(source);

        // check if chapters must be extracted with mkvextract
        ChaptersTrack chaps = tracks.stream().filter(i -> i.getType() == TrackType.CHAPTERS).map(i -> (ChaptersTrack) i).filter(i -> i.getDemuxOptions().isSelected()).findFirst().orElse(null);
        if (chaps != null) {
            if (chaps.getProperties().isUseMkvExtract()) {
                ret.add(Arrays.asList("mkvextract.exe", source, "chapters", "-s", "chaps.txt"));
            } else {
                eac3to.add(String.format("%s:%s", chaps.getIndex(), "chaps.txt"));
            }
        }

        // video tracks
        List<List<String>> ffmmpeg = new ArrayList<>();
        VideoTrack video = tracks.stream().filter(i -> i.getType() == TrackType.VIDEO).map(i -> (VideoTrack) i).filter(i -> i.getDemuxOptions().isSelected()).findFirst().orElse(null);
        if (video != null) {
            if (video.getDemuxOptions().isConvertToHuff()) {
                // if we are demuxing, make it more verbose to collect eta information
                if (command == WorkerCommand.PRINT_COMMANDS) {
                    // if we are just printing commands for manual execution, make the output quieter
                    ffmmpeg.add(Arrays.asList("ffmpeg.exe", "-hide_banner", "-loglevel", "warning", "-stats", "-y", "-i", source, "-map", "0:v:0", "-c:v", "ffvhuff", "video_huff.mkv", "&&", "ffmsindex.exe", "video_huff.mkv"));
                } else {
                    // if we are demuxing, make it more verbose to collect eta information
                    ffmmpeg.add(Arrays.asList("ffmpeg.exe", "-hide_banner", "-y", "-i", source, "-map", "0:v:0", "-c:v", "ffvhuff", "video_huff.mkv"));
                    ffmmpeg.add(Arrays.asList("ffmsindex.exe", "video_huff.mkv"));
                }
            }
        }

        // audio tracks
        List<AudioTrack> audioTracks = tracks.stream().filter(i -> i.getType() == TrackType.AUDIO).map(i -> (AudioTrack) i).filter(i -> i.getDemuxOptions().isSelected()).collect(Collectors.toList());
        for (var audioTrack : audioTracks) {
            int index = audioTrack.getIndex();
            AudioCodec codec = audioTrack.getProperties().getCodec();
            Language lang = audioTrack.getProperties().getLanguage();
            LosslessDemuxStrategy strategy = audioTrack.getDemuxOptions().getDemuxStrategy();
            if (codec.isLossless()) {
                if (strategy == LosslessDemuxStrategy.KEEP_BOTH || strategy == LosslessDemuxStrategy.KEEP_LOSSLESS) {
                    eac3to.add(String.format("%s:audio.%s.%s.orig.%s", index, lang.getCode(), index, codec.getOriginalExtension()));
                }
                if (audioTrack.getDemuxOptions().getExtractCore()) {
                    eac3to.add(String.format("%s:audio.%s.%s.core.%s", index, lang.getCode(), index, codec.getCoreExtension()));
                }
                if (strategy == LosslessDemuxStrategy.KEEP_BOTH || strategy == LosslessDemuxStrategy.KEEP_LOSSY) {
                    String format = audioTrack.getProperties().getChannels() <= 2 ? "2ch.256kbps" : "6ch.640kbps";
                    eac3to.add(String.format("%s:audio.%s.%s.compressed.%s.%s", index, lang.getCode(), index, format, "ac3"));
                    if (audioTrack.getProperties().getChannels() > 6) {
                        eac3to.add("-down6");
                    }
                    if (audioTrack.getDemuxOptions().getNormalize()) {
                        eac3to.add("-normalize");
                    }
                }
            } else {
                eac3to.add(String.format("%s:audio.%s.%s.orig.%s", index, lang.getCode(), index, codec.getOriginalExtension()));
            }
        }

        eac3to.add("-log=NUL");
        if (command != WorkerCommand.PRINT_COMMANDS) {
            eac3to.add("-progressnumbers");
        }
        ret.add(eac3to);

        // adding ffmpeg at the end
        ret.addAll(ffmmpeg);

        // finishing with an empty line
        ret.add(Arrays.asList("", System.lineSeparator()));
        return ret;
    }

    private ProcessOutcome runEac3to(List<String> args) throws Exception {
        log.info("Running: {}", String.join(" ", args));
        output.append("Running: ").append(String.join(" ", args)).append(System.lineSeparator());
        firePropertyChange("output", null, output.toString());

        StringBuilder stdout = new StringBuilder();
        ProcessBuilder pb = new ProcessBuilder(args.toArray(new String[0]));
        pb.directory(new File("d:\\iso"));
        pb.redirectErrorStream(true);
        long startTime = System.nanoTime();
        Process p = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (isCancelled()) {
                    log.info("Cancelling execution and killing children processes");
                    p.descendants().forEachOrdered(ProcessHandle::destroyForcibly);
                    throw new RuntimeException();
                }
                Matcher m = EAC3TOPROGRESS_PATTERN.matcher(line);
                if (m.matches()) {
                    if (command == WorkerCommand.ANALYZE || line.contains("process")) {
                        int progress = Integer.parseInt(m.group("progress"));
                        setProgress(Math.min(progress, 100));
                    }
                } else {
                    output.append(CommonUtils.rtrim(line)).append(System.lineSeparator());
                    stdout.append(CommonUtils.rtrim(line)).append(System.lineSeparator());
                    firePropertyChange("output", null, output.toString());
                }
                firePropertyChange("eta", null, calcEta(startTime, getProgress()));
            }
        }
        p.waitFor();

        log.info("Process finished with exit code: {}", p.exitValue());
        output.append("Process finished with exit code: ").append(p.exitValue()).append(System.lineSeparator());
        firePropertyChange("output", null, output.toString());

        return new ProcessOutcome(p.exitValue(), stdout.toString());
    }

    private ProcessOutcome runMkvinfo() throws Exception {
        List<String> args = new ArrayList<>();
        args.add("mkvinfo.exe");
        args.add(source);
        args.add("--ui-language");
        args.add("en");

        log.info("Running: {}", String.join(" ", args));
        output.append("Running: ").append(String.join(" ", args)).append(System.lineSeparator());
        firePropertyChange("output", null, output.toString());

        StringBuilder stdout = new StringBuilder();
        ProcessBuilder pb = new ProcessBuilder(args.toArray(new String[0]));
        pb.directory(new File("d:\\iso"));
        pb.redirectErrorStream(true);
        Process p = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (isCancelled()) {
                    log.info("Cancelling execution and killing children processes");
                    p.descendants().forEachOrdered(ProcessHandle::destroyForcibly);
                    throw new RuntimeException();
                }
                // we capture stdout but don't send anything to output window
                stdout.append(CommonUtils.rtrim(line)).append(System.lineSeparator());
            }
        }
        p.waitFor();

        log.info("Process finished with exit code: {}", p.exitValue());
        output.append("Process finished with exit code: ").append(p.exitValue()).append(System.lineSeparator());
        firePropertyChange("output", null, output.toString());

        return new ProcessOutcome(p.exitValue(), stdout.toString());
    }

}
