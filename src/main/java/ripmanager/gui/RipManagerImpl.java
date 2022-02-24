package ripmanager.gui;

import lombok.extern.log4j.Log4j2;
import ripmanager.common.ExceptionUtils;
import ripmanager.worker.Eac3toWorker;
import ripmanager.worker.ProcessOutcome;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static ripmanager.common.CommonUtils.formatInterval;
import static ripmanager.common.CommonUtils.isEmpty;

@Log4j2
public class RipManagerImpl extends RipManager {

    public static final String ETA_DEFAULT = "ETA: 00:00:00";

    private boolean running = false;
    private SwingWorker<ProcessOutcome, Void> worker;

    public RipManagerImpl() {
        super();
        initComponents();
        sourceButton.addActionListener(e -> sourceButtonClicked());
        analyzeButton.addActionListener(e -> analyzeButtonClicked());
    }

    private void initComponents() {
        sourceTextField.setText("D:\\iso\\video.mkv");
        etaLabel.setText(ETA_DEFAULT);
    }

    public void startBackgroundTask() {
        running = true;
        sourceButton.setEnabled(false);
        //analyzeButton.setEnabled(false);
        analyzeButton.setText("Abort");
        progressBar.setValue(0);
        etaLabel.setText(ETA_DEFAULT);
    }

    public void endBackgroundTask() {
        running = false;
        sourceButton.setEnabled(true);
        //analyzeButton.setEnabled(true);
        analyzeButton.setText("Analyze");
        progressBar.setValue(0);
        etaLabel.setText(ETA_DEFAULT);
    }

    public void sourceButtonClicked() {
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        JFileChooser fc;
        if (!isEmpty(sourceTextField.getText()) && Files.exists(Paths.get(sourceTextField.getText()).getParent())) {
            fc = new JFileChooser(Paths.get(sourceTextField.getText()).getParent().toAbsolutePath().toFile());
        } else {
            fc = new JFileChooser(Paths.get("").toAbsolutePath().toFile());
        }
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        int ret = fc.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File selectedDir = fc.getSelectedFile();
            sourceTextField.setText(selectedDir.toString());
        }
    }

    public void analyzeButtonClicked() {
        if (!running) {
            startBackgroundTask();
            List<String> args = new ArrayList<>();
            args.add(sourceTextField.getText());
            worker = new Eac3toWorker(args, this);
            worker.addPropertyChangeListener(evt -> {
                if (!worker.isDone()) {
                    switch (evt.getPropertyName()) {
                        case "progress":
                            progressBar.setValue((Integer) evt.getNewValue());
                            break;
                        case "output":
                            outputTextArea.setText((String) evt.getNewValue());
                            break;
                        case "eta":
                            etaLabel.setText(evt.getNewValue() == null ? ETA_DEFAULT : "ETA: " + formatInterval((Long) evt.getNewValue()));
                            break;
                    }
                }
            });
            worker.execute();
        } else {
            worker.cancel(true);
        }
    }

    public void analyzeTaskCallback(ProcessOutcome outcome) {
        endBackgroundTask();
        outputTextArea.setText(outcome.getOutput());
        if (outcome.getExitCode() != null && outcome.getExitCode() != 0) {
            JOptionPane.showMessageDialog(this, String.format("Process finished with exit code: %s", outcome.getExitCode()), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void analyzeTaskCallback(Exception ex) {
        endBackgroundTask();
        outputTextArea.setText(ExceptionUtils.getCanonicalFormWithStackTrace(ex));
        JOptionPane.showMessageDialog(this, "Exception while running process", "Error", JOptionPane.ERROR_MESSAGE);
    }

}
