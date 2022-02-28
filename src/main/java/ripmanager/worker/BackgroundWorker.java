package ripmanager.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ripmanager.common.CommonUtils;
import ripmanager.engine.Eac3toParser;
import ripmanager.engine.dto.*;
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
        args.add("python.exe");
        args.add("c:\\dvd-rip\\python\\run_cmd.py");
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
        if (mkvinfoOutcome.getStdout().contains("+ Chapters")) {
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
        // creating eac3to command
        List<String> eac3to = new ArrayList<>();
        eac3to.add("python.exe");
        eac3to.add("c:\\dvd-rip\\python\\run_cmd.py");
        eac3to.add("eac3to.exe");
        eac3to.add(source);

        // check if chapters must be extracted with mkvextract
        boolean eac3toUsed = false;
        ChaptersTrack chaps = tracks.stream().filter(i -> i.getType() == TrackType.CHAPTERS).map(i -> (ChaptersTrack) i).filter(i -> i.getDemuxOptions().isSelected()).findFirst().orElse(null);
        if (chaps != null) {
            if (chaps.getProperties().isUseMkvExtract()) {
                // prepend mkvextract command
                ret.add(Arrays.asList("mkvextract.exe", source, "chapters", "-s", "chaps.txt"));
            } else {
                eac3toUsed = true;
                eac3to.add(String.format("%s:%s", chaps.getIndex(), "chaps.txt"));
            }
        }

        // video tracks
        VideoTrack video = tracks.stream().filter(i -> i.getType() == TrackType.VIDEO).map(i -> (VideoTrack) i).filter(i -> i.getDemuxOptions().isSelected()).findFirst().orElse(null);
        if (video != null) {
            if (video.getDemuxOptions().isConvertToHuff()) {
                // prepend ffmpeg command
                ret.add(Arrays.asList("python.exe", "c:\\dvd-rip\\python\\run_cmd.py", "ffmpeg", "-hide_banner", "-loglevel", "warning", "-stats", "-i", source, "-map", "0:v:0", "-c:v", "ffvhuff", "video_huff.mkv"));
            }
        }

        eac3to.add("-log=NUL");
        eac3to.add("-progressnumbers");
        if (eac3toUsed) {
            ret.add(eac3to);
        }

        // finishing with an empty line
        ret.add(Arrays.asList(""));
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
                // we don't capture output here
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
