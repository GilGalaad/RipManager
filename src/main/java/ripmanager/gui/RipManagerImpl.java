package ripmanager.gui;

import lombok.extern.log4j.Log4j2;
import ripmanager.common.ExceptionUtils;
import ripmanager.engine.dto.WorkerCommand;
import ripmanager.worker.BackgroundWorker;
import ripmanager.worker.WorkerOutcome;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static ripmanager.common.CommonUtils.formatInterval;
import static ripmanager.common.CommonUtils.isEmpty;

@Log4j2
public class RipManagerImpl extends RipManager {

    public static final String ETA_DEFAULT = "ETA: 00:00:00";

    private boolean running = false;
    private BackgroundWorker worker;

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

    private PropertyChangeListener generatePropertyChangeListener() {
        return evt -> {
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
        };
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
            outputTextArea.setText(null);
            worker = new BackgroundWorker(WorkerCommand.ANALYZE, sourceTextField.getText(), null, this);
            worker.addPropertyChangeListener(generatePropertyChangeListener());
            worker.execute();
        } else {
            worker.cancel(true);
        }
    }

    public void analyzeTaskCallback(WorkerOutcome outcome) {
        endBackgroundTask();
        outputTextArea.setText(outcome.getOutput());
        if (outcome.getStatus() != WorkerOutcome.Status.OK) {
            JOptionPane.showMessageDialog(this, "Process finished with errors", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void analyzeTaskCallback(Exception ex) {
        endBackgroundTask();
        outputTextArea.setText(ExceptionUtils.getCanonicalFormWithStackTrace(ex));
        JOptionPane.showMessageDialog(this, "Exception while running process", "Error", JOptionPane.ERROR_MESSAGE);
    }

}
