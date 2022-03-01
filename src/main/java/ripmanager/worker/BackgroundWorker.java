package ripmanager.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ripmanager.common.CommonUtils;
import ripmanager.engine.Eac3toParser;
import ripmanager.engine.dto.*;
import ripmanager.engine.enums.AudioCodec;
import ripmanager.engine.enums.Encoder;
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

import static ripmanager.common.CommonUtils.*;

@Log4j2
@RequiredArgsConstructor
public class BackgroundWorker extends SwingWorker<WorkerOutcome, Void> {

    private static final Pattern EAC3TO_PROGRESS_PATTERN = Pattern.compile("(analyze|process): (?<progress>\\d+)%");
    private static final Pattern MKVINFO_CHAPTERS_PATTERN = Pattern.compile("^\\|\\+ Chapters$", Pattern.MULTILINE);
    private static final Pattern FFMPEG_DURATION_PATTERN = Pattern.compile("\\s*Duration:\\s+(?<duration>(\\d{2}:)+?(\\d{2}:)+?(\\d{2}.)+?\\d{2}),.*");
    private static final Pattern FFMPEG_TIME_PATTERN = Pattern.compile("frame=.+?fps=.+?q=.+?size=.+?time=(?<time>(\\d{2}:)+?(\\d{2}:)+?(\\d{2}.)+?\\d{2}).+?bitrate=.+");
    private static final Pattern FFMSINDEX_PROGRESS_PATTERN = Pattern.compile("Indexing, please wait\\.{3}\\s*(?<progress>\\d{1,3})%\\s*");
    private static final Pattern ENCODE_TOTALFRAMES_PATTERN = Pattern.compile("Detected length: (?<frames>\\d+) frames");
    private static final Pattern ENCODE_PROGRESS_PATTERN = Pattern.compile("(?<frames>\\d+) frames:.*");

    // class init params
    private final WorkerCommand command;
    private final String source;
    private final List<Track> tracks;
    private final EncodingOptions encodingOptions;
    private final RipManagerImpl frame;

    // local variables
    private final StringBuilder output = new StringBuilder();

    @Override
    protected WorkerOutcome doInBackground() throws Exception {
        switch (command) {
            case ANALYZE:
                return doAnalyze();
            case PRINT_COMMANDS:
                return doPrintCommands();
            case DEMUX:
                return doDemux();
            case ENCODE:
                return doEncode();
            case DEMUX_ENCODE:
                return doDemuxEncode();
            default:
                throw new UnsupportedOperationException("Unsupported command: " + command);
        }
    }

    @Override
    protected void done() {
        try {
            WorkerOutcome ret = get();
            switch (command) {
                case ANALYZE:
                    frame.analyzeTaskCallback(ret);
                    break;
                case PRINT_COMMANDS:
                    frame.printCommandsTaskCallback(ret);
                    break;
                case DEMUX:
                    frame.demuxTaskCallback(ret);
                    break;
                case ENCODE:
                    frame.encodeTaskCallback(ret);
                    break;
                case DEMUX_ENCODE:
                    frame.demuxEncodeTaskCallback(ret);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported command: " + command);
            }
        } catch (CancellationException | InterruptedException ex) {
            frame.endBackgroundTask();
        } catch (ExecutionException ex) {
            frame.exceptionTaskCallback(ex.getCause());
        } catch (Exception ex) {
            frame.exceptionTaskCallback(ex);
        }
    }

    private WorkerOutcome doAnalyze() throws Exception {
        // analyzing file with eac3to
        List<String> args = Arrays.asList("eac3to", source, "-log=NUL", "-progressnumbers");
        ProcessOutcome outcome = runEac3to(args);
        if (outcome.getExitCode() != 0) {
            return new WorkerOutcome(WorkerOutcome.Status.KO, output.toString(), null);
        }

        // parsing output
        List<Track> parsedTracks = Eac3toParser.parse(outcome.getStdout());
        if (parsedTracks.isEmpty()) {
            return new WorkerOutcome(WorkerOutcome.Status.KO, output.toString(), parsedTracks);
        }

        // if chapters are found, we are done
        if (parsedTracks.stream().anyMatch(i -> i.getType() == TrackType.CHAPTERS)) {
            return new WorkerOutcome(WorkerOutcome.Status.OK, output.toString(), parsedTracks);
        }
        // otherwise, looking for chapters with mkvinfo
        output.append(System.lineSeparator());
        args = Arrays.asList("mkvinfo", source, "--ui-language", "en");
        outcome = runGenericProcess(args);
        if (outcome.getExitCode() != 0) {
            return new WorkerOutcome(WorkerOutcome.Status.KO, output.toString(), null);
        }
        if (MKVINFO_CHAPTERS_PATTERN.matcher(outcome.getStdout()).find()) {
            ChaptersTrack track = new ChaptersTrack(parsedTracks.stream().mapToInt(Track::getIndex).max().orElse(0) + 1, "Chapters");
            track.setProperties(new ChaptersProperties(true));
            track.setDemuxOptions(new DemuxOptions(true));
            parsedTracks.add(track);
        }

        return new WorkerOutcome(WorkerOutcome.Status.OK, output.toString(), parsedTracks);
    }

    private WorkerOutcome doPrintCommands() {
        List<List<String>> commands = generateCommands();
        List<String> lines = commands.stream().map(i -> String.join(" ", i)).collect(Collectors.toList());
        for (var line : lines) {
            output.append(line).append(System.lineSeparator());
        }
        return new WorkerOutcome(WorkerOutcome.Status.OK, output.toString(), null);
    }

    private WorkerOutcome doDemux() throws Exception {
        List<List<String>> commands = generateCommands();
        for (var command : commands) {
            ProcessOutcome outcome;
            switch (command.get(0)) {
                case "mkvextract":
                    outcome = runGenericProcess(command);
                    break;
                case "eac3to":
                    outcome = runEac3to(command);
                    //outcome = runGenericProcess(Arrays.asList("ping", "localhost", "-n", "1"));
                    break;
                case "ffmpeg":
                    outcome = runFFmpeg(command);
                    //outcome = runGenericProcess(Arrays.asList("ping", "localhost", "-n", "1"));
                    break;
                case "ffmsindex":
                    outcome = runFFMSindex(command);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported executable: " + command.get(0));
            }
            if (outcome.getExitCode() != 0) {
                return new WorkerOutcome(WorkerOutcome.Status.KO, output.toString(), null);
            }
            output.append(System.lineSeparator());
        }
        return new WorkerOutcome(WorkerOutcome.Status.OK, output.toString(), null);
    }

    private WorkerOutcome doEncode() throws Exception {
        List<String> args = new ArrayList<>();
        args.add("python");
        args.add("c:\\dvd-rip\\python\\encode.py");
        if (encodingOptions.getCrf() != 18) {
            args.add("--crf");
            args.add(Integer.toString(encodingOptions.getCrf()));
        }
        if (!isEmpty(encodingOptions.getSuffix())) {
            args.add("--suffix");
            args.add(encodingOptions.getSuffix());
        }
        if (encodingOptions.getEncoder() != Encoder.AUTODETECT) {
            args.add(encodingOptions.getEncoder().toString());
        }
        ProcessOutcome outcome = runEncode(args);
        if (outcome.getExitCode() != 0) {
            return new WorkerOutcome(WorkerOutcome.Status.KO, output.toString(), null);
        }
        return new WorkerOutcome(WorkerOutcome.Status.OK, output.toString(), null);
    }

    private WorkerOutcome doDemuxEncode() throws Exception {
        WorkerOutcome outcome = doDemux();
        if (outcome.getStatus() == WorkerOutcome.Status.KO) {
            return outcome;
        }
        output.append(System.lineSeparator());
        return doEncode();
    }

    private List<List<String>> generateCommands() {
        List<List<String>> ret = new ArrayList<>();
        List<String> eac3to = new ArrayList<>();
        eac3to.add("eac3to");
        eac3to.add(source);

        // check if chapters must be extracted with mkvextract
        ChaptersTrack chaps = tracks.stream().filter(i -> i.getType() == TrackType.CHAPTERS).map(i -> (ChaptersTrack) i).filter(i -> i.getDemuxOptions().isSelected()).findFirst().orElse(null);
        if (chaps != null) {
            if (chaps.getProperties().isUseMkvExtract()) {
                ret.add(Arrays.asList("mkvextract", source, "chapters", "-s", "chaps.txt"));
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
                    ffmmpeg.add(Arrays.asList("ffmpeg", "-hide_banner", "-loglevel", "warning", "-stats", "-y", "-i", source, "-map", "0:v:0", "-c:v", "ffvhuff", "video_huff.mkv", "&&", "ffmsindex.exe", "video_huff.mkv"));
                } else {
                    // if we are demuxing, make it more verbose to collect eta information
                    ffmmpeg.add(Arrays.asList("ffmpeg", "-hide_banner", "-y", "-i", source, "-map", "0:v:0", "-c:v", "ffvhuff", "video_huff.mkv"));
                    ffmmpeg.add(Arrays.asList("ffmsindex", "-f", "video_huff.mkv"));
                }
            }
        }

        // audio tracks
        List<AudioTrack> audioTracks = tracks.stream().filter(i -> i.getType() == TrackType.AUDIO).map(i -> (AudioTrack) i).filter(i -> i.getDemuxOptions().isSelected()).collect(Collectors.toList());
        for (var audioTrack : audioTracks) {
            AudioCodec codec = audioTrack.getProperties().getCodec();
            LosslessDemuxStrategy strategy = audioTrack.getDemuxOptions().getDemuxStrategy();
            if (codec.isLossless()) {
                if (strategy == LosslessDemuxStrategy.KEEP_BOTH || strategy == LosslessDemuxStrategy.KEEP_LOSSLESS) {
                    eac3to.add(String.format("%s:audio.%s.%s.orig.%s", audioTrack.getIndex(), audioTrack.getProperties().getLanguage().getCode(), audioTrack.getIndex(), codec.getOriginalExtension()));
                }
                if (audioTrack.getDemuxOptions().getExtractCore()) {
                    eac3to.add(String.format("%s:audio.%s.%s.core.%s", audioTrack.getIndex(), audioTrack.getProperties().getLanguage().getCode(), audioTrack.getIndex(), codec.getCoreExtension()));
                }
                if (strategy == LosslessDemuxStrategy.KEEP_BOTH || strategy == LosslessDemuxStrategy.KEEP_LOSSY) {
                    String format = audioTrack.getProperties().getChannels() <= 2 ? "2ch.256kbps" : "6ch.640kbps";
                    eac3to.add(String.format("%s:audio.%s.%s.compressed.%s.%s", audioTrack.getIndex(), audioTrack.getProperties().getLanguage().getCode(), audioTrack.getIndex(), format, "ac3"));
                    if (audioTrack.getProperties().getChannels() > 6) {
                        eac3to.add("-down6");
                    }
                    if (audioTrack.getDemuxOptions().getNormalize()) {
                        eac3to.add("-normalize");
                    }
                }
            } else {
                eac3to.add(String.format("%s:audio.%s.%s.orig.%s", audioTrack.getIndex(), audioTrack.getProperties().getLanguage().getCode(), audioTrack.getIndex(), codec.getOriginalExtension()));
            }
        }

        // subtitles
        List<SubtitlesTrack> subtitlesTracks = tracks.stream().filter(i -> i.getType() == TrackType.SUBTITLES).map(i -> (SubtitlesTrack) i).filter(i -> i.getDemuxOptions().isSelected()).collect(Collectors.toList());
        for (var subtitlesTrack : subtitlesTracks) {
            eac3to.add(String.format("%s:sub.%s.%s.sup", subtitlesTrack.getIndex(), subtitlesTrack.getProperties().getLanguage().getCode(), subtitlesTrack.getIndex()));
        }

        eac3to.add("-log=NUL");
        ret.add(eac3to);
        
        // adding ffmpeg at the end
        ret.addAll(ffmmpeg);

        // ip printing commands, finishing with an empty line
        if (command == WorkerCommand.PRINT_COMMANDS) {
            ret.add(Arrays.asList("", System.lineSeparator()));
        }
        return ret;
    }

    private ProcessOutcome runEac3to(List<String> args) throws Exception {
        log.info("Running: {}", String.join(" ", args));
        output.append("Running: ").append(String.join(" ", args)).append(System.lineSeparator());
        firePropertyChange("output", null, output.toString());

        args = wrapCommand(args);
        args.add("-progressnumbers");
        log.info("Actual command line: {}", String.join(" ", args));

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
                Matcher m = EAC3TO_PROGRESS_PATTERN.matcher(line);
                if (m.matches()) {
                    if (command == WorkerCommand.ANALYZE || line.contains("process")) {
                        int progress = Integer.parseInt(m.group("progress"));
                        setProgress(Math.min(progress, 100));
                        firePropertyChange("eta", null, calcEta(startTime, progress));
                    }
                    continue;
                }
                output.append(CommonUtils.rtrim(line)).append(System.lineSeparator());
                stdout.append(CommonUtils.rtrim(line)).append(System.lineSeparator());
                firePropertyChange("output", null, output.toString());
            }
        }
        p.waitFor();
        long endTime = System.nanoTime();

        log.info("Process completed with exit code: {} after {}", p.exitValue(), smartElapsed(endTime - startTime));
        output.append(String.format("Process completed with exit code: %s after %s", p.exitValue(), smartElapsed(endTime - startTime))).append(System.lineSeparator());
        firePropertyChange("output", null, output.toString());

        return new ProcessOutcome(p.exitValue(), stdout.toString());
    }

    private ProcessOutcome runFFmpeg(List<String> args) throws Exception {
        log.info("Running: {}", String.join(" ", args));
        output.append("Running: ").append(String.join(" ", args)).append(System.lineSeparator());
        firePropertyChange("output", null, output.toString());

        args = wrapCommand(args);
        log.info("Actual command line: {}", String.join(" ", args));

        StringBuilder stdout = new StringBuilder();
        long duration = 0;
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
                Matcher m = FFMPEG_DURATION_PATTERN.matcher(line);
                if (m.matches()) {
                    duration = parseInterval(m.group("duration"));
                    output.append(line.trim()).append(System.lineSeparator());
                    stdout.append(line.trim()).append(System.lineSeparator());
                    firePropertyChange("output", null, output.toString());
                    continue;
                }
                m = FFMPEG_TIME_PATTERN.matcher(line);
                if (m.matches()) {
                    long currentTime = parseInterval(m.group("time"));
                    if (duration != 0) {
                        int progress = calcPercent(currentTime, duration);
                        if (progress != getProgress()) {
                            setProgress(Math.min(progress, 100));
                            firePropertyChange("eta", null, calcEta(startTime, getProgress()));
                        }
                    }
                    if (getLastOutputLine().startsWith("frame=")) {
                        removeLastOuputLine();
                    }
                    output.append(line.trim()).append(System.lineSeparator());
                    stdout.append(line.trim()).append(System.lineSeparator());
                    firePropertyChange("output", null, output.toString());
                    continue;
                }
                if (line.contains("muxing overhead:")) {
                    output.append(line.trim()).append(System.lineSeparator());
                    stdout.append(line.trim()).append(System.lineSeparator());
                    firePropertyChange("output", null, output.toString());
                }
            }
        }
        p.waitFor();
        long endTime = System.nanoTime();

        log.info("Process completed with exit code: {} after {}", p.exitValue(), smartElapsed(endTime - startTime));
        output.append(String.format("Process completed with exit code: %s after %s", p.exitValue(), smartElapsed(endTime - startTime))).append(System.lineSeparator());
        firePropertyChange("output", null, output.toString());

        return new ProcessOutcome(p.exitValue(), stdout.toString());
    }

    private ProcessOutcome runFFMSindex(List<String> args) throws Exception {
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
                if (isEmpty(line)) {
                    continue;
                }
                Matcher m = FFMSINDEX_PROGRESS_PATTERN.matcher(line);
                if (m.matches()) {
                    int progress = Integer.parseInt(m.group("progress"));
                    setProgress(Math.min(progress, 100));
                    firePropertyChange("eta", null, calcEta(startTime, progress));
                }
                if (getLastOutputLine().startsWith("Indexing")) {
                    removeLastOuputLine();
                }
                output.append(line.trim()).append(System.lineSeparator());
                stdout.append(line.trim()).append(System.lineSeparator());
                firePropertyChange("output", null, output.toString());
            }
        }
        p.waitFor();
        long endTime = System.nanoTime();

        log.info("Process completed with exit code: {} after {}", p.exitValue(), smartElapsed(endTime - startTime));
        output.append(String.format("Process completed with exit code: %s after %s", p.exitValue(), smartElapsed(endTime - startTime))).append(System.lineSeparator());
        firePropertyChange("output", null, output.toString());

        return new ProcessOutcome(p.exitValue(), stdout.toString());
    }

    private ProcessOutcome runEncode(List<String> args) throws Exception {
        log.info("Running: {}", String.join(" ", args));
        output.append("Running: ").append(String.join(" ", args)).append(System.lineSeparator());
        firePropertyChange("output", null, output.toString());

        StringBuilder stdout = new StringBuilder();
        long totalFrames = 0;
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
                Matcher m = ENCODE_TOTALFRAMES_PATTERN.matcher(line);
                if (m.matches()) {
                    totalFrames = Long.parseLong(m.group("frames"));
                }
                m = ENCODE_PROGRESS_PATTERN.matcher(line);
                if (m.matches()) {
                    long currentFrames = Long.parseLong(m.group("frames"));
                    if (totalFrames != 0) {
                        int progress = calcPercent(currentFrames, totalFrames);
                        if (progress != getProgress()) {
                            setProgress(Math.min(progress, 100));
                            firePropertyChange("eta", null, calcEta(startTime, getProgress()));
                        }
                    }
                }
                if (getLastOutputLine().contains("frames") && getLastOutputLine().contains("fps") && getLastOutputLine().contains("kb/s")) {
                    // there is an unwanted empty line at the end of the encoding, when we actually want to delete the last progress line
                    if (isEmpty(line)) {
                        continue;
                    }
                    removeLastOuputLine();
                }
                output.append(line.trim()).append(System.lineSeparator());
                stdout.append(line.trim()).append(System.lineSeparator());
                firePropertyChange("output", null, output.toString());
            }
        }
        p.waitFor();
        long endTime = System.nanoTime();

        log.info("Process completed with exit code: {} after {}", p.exitValue(), smartElapsed(endTime - startTime));
        output.append(String.format("Process completed with exit code: %s after %s", p.exitValue(), smartElapsed(endTime - startTime))).append(System.lineSeparator());
        firePropertyChange("output", null, output.toString());

        return new ProcessOutcome(p.exitValue(), stdout.toString());
    }

    private ProcessOutcome runGenericProcess(List<String> args) throws Exception {
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
                // we capture stdout but don't send anything to output window
                stdout.append(CommonUtils.rtrim(line)).append(System.lineSeparator());
            }
        }
        p.waitFor();
        long endTime = System.nanoTime();

        log.info("Process completed with exit code: {} after {}", p.exitValue(), smartElapsed(endTime - startTime));
        output.append(String.format("Process completed with exit code: %s after %s", p.exitValue(), smartElapsed(endTime - startTime))).append(System.lineSeparator());
        firePropertyChange("output", null, output.toString());

        return new ProcessOutcome(p.exitValue(), stdout.toString());
    }

    private List<String> wrapCommand(List<String> args) {
        List<String> wrappedArgs = new ArrayList<>(args.size() + 2);
        wrappedArgs.add("python");
        wrappedArgs.add("C:\\dvd-rip\\python\\run_cmd.py");
        wrappedArgs.addAll(args);
        return wrappedArgs;
    }

    private String getLastOutputLine() {
        if (output.toString().endsWith(System.lineSeparator())) {
            return output.substring(output.substring(0, output.length() - System.lineSeparator().length()).lastIndexOf(System.lineSeparator()) + System.lineSeparator().length());
        }
        return output.substring(output.lastIndexOf(System.lineSeparator() + System.lineSeparator().length()));
    }

    private void removeLastOuputLine() {
        if (output.toString().endsWith(System.lineSeparator())) {
            output.setLength(output.lastIndexOf(System.lineSeparator()));
        }
        output.setLength(output.lastIndexOf(System.lineSeparator()) + System.lineSeparator().length());
    }

}
