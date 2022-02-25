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
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (command == WorkerCommand.ANALYZE) {
            // analyzing file with eac3to
            List<String> args = new ArrayList<>();
            args.add(source);
            ProcessOutcome eac3toOutcome = runEac3to(args);
            if (eac3toOutcome.getExitCode() != 0) {
                return new WorkerOutcome(WorkerOutcome.Status.KO, output.toString(), null);
            }

            // parsing output
            List<Track> parsedTracks = Eac3toParser.parse(eac3toOutcome.getStdout());
            if (parsedTracks.stream().anyMatch(i -> i.getType() == TrackType.CHAPTERS)) {
                return new WorkerOutcome(WorkerOutcome.Status.OK, output.toString(), parsedTracks);
            }

            // looking for chapters with mkvextract
            output.append(System.lineSeparator());
            ProcessOutcome mkvInfoOutcome = runMkvInfo();
            if (mkvInfoOutcome.getExitCode() != 0) {
                return new WorkerOutcome(WorkerOutcome.Status.KO, output.toString(), null);
            }
            if (mkvInfoOutcome.getStdout().contains("+ Chapters")) {
                ChaptersTrack track = new ChaptersTrack(parsedTracks.stream().mapToInt(Track::getIndex).max().orElse(0) + 1);
                track.setProperties(new ChaptersProperties(false));
                parsedTracks.add(track);
            }

            parsedTracks.sort(Comparator.comparing(Track::getIndex));
            parsedTracks.forEach(log::info);
            return new WorkerOutcome(WorkerOutcome.Status.OK, output.toString(), parsedTracks);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    protected void done() {
        try {
            WorkerOutcome ret = get();
            frame.analyzeTaskCallback(ret);
        } catch (CancellationException | InterruptedException ex) {
            frame.endBackgroundTask();
        } catch (ExecutionException ex) {
            frame.analyzeTaskCallback(ex);
        }
    }

    private ProcessOutcome runEac3to(List<String> args) throws Exception {
        args.add(0, "python.exe");
        args.add(1, "c:\\dvd-rip\\python\\run_cmd.py");
        args.add(2, "eac3to.exe");
        //args.add("6:subs.sup");
        args.add("-log=NUL");
        args.add("-progressnumbers");

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

    private ProcessOutcome runMkvInfo() throws Exception {
        List<String> args = new ArrayList<>();
        args.add("python.exe");
        args.add("c:\\dvd-rip\\python\\run_cmd.py");
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
