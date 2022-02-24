package ripmanager.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ripmanager.common.StringUtils;
import ripmanager.gui.RipManagerImpl;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@RequiredArgsConstructor
public class Eac3toWorker extends SwingWorker<ProcessOutcome, Void> {

    private static final String PROGRESS_REGEX = "(analyze|process): (?<progress>\\d+)%";
    private static final Pattern PROGRESS_PATTERN = Pattern.compile(PROGRESS_REGEX);

    private final List<String> args;
    private final RipManagerImpl frame;

    private final StringBuilder output = new StringBuilder();

    @Override
    protected ProcessOutcome doInBackground() throws Exception {
        args.add(0, "python.exe");
        args.add(1, "c:\\dvd-rip\\python\\run_cmd.py");
        args.add(2, "eac3to.exe");
        args.add("4:asd.sup");
        args.add("-log=NUL");
        args.add("-progressnumbers");
        log.info("Running: {}", String.join(" ", args));
        ProcessBuilder pb = new ProcessBuilder(args.toArray(new String[0]));
        pb.directory(new File("d:\\iso"));
        pb.redirectErrorStream(true);
        Process p = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (isCancelled()) {
                    p.descendants().forEachOrdered(ProcessHandle::destroyForcibly);
                    return null;
                }
                Matcher m = PROGRESS_PATTERN.matcher(line);
                if (m.matches()) {
                    int progress = Integer.parseInt(m.group("progress"));
                    setProgress(Math.min(progress, 100));
                } else {
                    output.append(StringUtils.rtrim(line));
                    output.append(System.lineSeparator());
                    firePropertyChange("output", null, output.toString());
                }
            }
        }
        p.waitFor();
        log.info("Process finished with exit code: {}", p.exitValue());
        return new ProcessOutcome(p.exitValue(), output.toString());
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
}
