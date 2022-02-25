package ripmanager.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ripmanager.common.CommonUtils;
import ripmanager.engine.Eac3toParser;
import ripmanager.engine.dto.Track;
import ripmanager.engine.dto.WorkerCommand;
import ripmanager.gui.RipManagerImpl;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ripmanager.common.CommonUtils.calcEta;

@Log4j2
@RequiredArgsConstructor
public class BackgroundWorker extends SwingWorker<ProcessOutcome, Void> {

    private static final Pattern EAC3TOPROGRESS_PATTERN = Pattern.compile("(analyze|process): (?<progress>\\d+)%");

    // class init params
    private final WorkerCommand command;
    private final String source;
    private final List<Track> tracks;
    private final RipManagerImpl frame;

    // local variables
    private Integer exitCode;
    private StringBuilder output;

    @Override
    protected ProcessOutcome doInBackground() throws Exception {
        if (command == WorkerCommand.ANALYZE) {
            List<String> args = new ArrayList<>();
            args.add(source);
            runEac3to(args);
            if (exitCode != 0) {
                return new ProcessOutcome(exitCode, output.toString());
            }
            List<Track> tracks = Eac3toParser.parse(output.toString());
            return new ProcessOutcome(exitCode, output.toString(), tracks);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    protected void done() {
        try {
            ProcessOutcome ret = get();
            frame.analyzeTaskCallback(ret);
        } catch (CancellationException | InterruptedException ex) {
            frame.endBackgroundTask();
        } catch (ExecutionException ex) {
            frame.analyzeTaskCallback(ex);
        }
    }

    private void runEac3to(List<String> args) throws Exception {
        args.add(0, "python.exe");
        args.add(1, "c:\\dvd-rip\\python\\run_cmd.py");
        args.add(2, "eac3to.exe");
        //args.add("6:subs.sup");
        args.add("-log=NUL");
        args.add("-progressnumbers");
        log.info("Running: {}", String.join(" ", args));
        output = new StringBuilder();
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
                    output.append(CommonUtils.rtrim(line));
                    output.append(System.lineSeparator());
                    firePropertyChange("output", null, output.toString());
                }
                firePropertyChange("eta", null, calcEta(startTime, getProgress()));
            }
        }
        p.waitFor();
        log.info("Process finished with exit code: {}", p.exitValue());
        exitCode = p.exitValue();
    }

}
