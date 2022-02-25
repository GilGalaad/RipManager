package ripmanager.gui;

import lombok.extern.log4j.Log4j2;
import ripmanager.common.ExceptionUtils;
import ripmanager.engine.dto.Track;
import ripmanager.engine.dto.TrackType;
import ripmanager.engine.dto.WorkerCommand;
import ripmanager.worker.BackgroundWorker;
import ripmanager.worker.WorkerOutcome;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static ripmanager.common.CommonUtils.formatInterval;
import static ripmanager.common.CommonUtils.isEmpty;

@Log4j2
public class RipManagerImpl extends RipManager {

    private static final String VIDEO_CATEGORY_LABEL = "Video streams";
    private static final String AUDIO_CATEGORY_LABEL = "Audio streams";
    private static final String SUBTITLES_CATEGORY_LABEL = "Subtitles";
    private static final String CHAPTERS_CATEGORY_LABEL = "Chapters";
    public static final String ETA_DEFAULT = "ETA: 00:00:00";

    private boolean running = false;
    private BackgroundWorker worker;

    public RipManagerImpl() {
        super();
        initComponents();
        sourceButton.addActionListener(e -> sourceButtonClicked());
        analyzeButton.addActionListener(e -> analyzeButtonClicked());

        trackTree.setModel(null);
        trackTree.setRootVisible(false);
        trackTree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) {
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                throw new ExpandVetoException(event);
            }
        });

        trackTree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();
                if (Track.class.isAssignableFrom(node.getUserObject().getClass())) {
                    this.setText(((Track) userObject).getLabel());
                }
                return this;
            }
        });
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

    private void sourceButtonClicked() {
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
            return;
        }
        populateTree(outcome.getTracks());
    }

    public void analyzeTaskCallback(Exception ex) {
        endBackgroundTask();
        outputTextArea.setText(ExceptionUtils.getCanonicalFormWithStackTrace(ex));
        JOptionPane.showMessageDialog(this, "Exception while running process", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void populateTree(List<Track> tracks) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(Paths.get(sourceTextField.getText()).getFileName(), true);
        if (tracks.stream().anyMatch(i -> i.getType() == TrackType.VIDEO)) {
            DefaultMutableTreeNode videoCategory = new DefaultMutableTreeNode(VIDEO_CATEGORY_LABEL, true);
            rootNode.add(videoCategory);
            tracks.stream().filter(i -> i.getType() == TrackType.VIDEO).forEachOrdered(i -> videoCategory.add(new DefaultMutableTreeNode(i, false)));
        }
        if (tracks.stream().anyMatch(i -> i.getType() == TrackType.AUDIO)) {
            DefaultMutableTreeNode audioCategory = new DefaultMutableTreeNode(AUDIO_CATEGORY_LABEL, true);
            rootNode.add(audioCategory);
            tracks.stream().filter(i -> i.getType() == TrackType.AUDIO).forEachOrdered(i -> audioCategory.add(new DefaultMutableTreeNode(i, false)));
        }
        if (tracks.stream().anyMatch(i -> i.getType() == TrackType.SUBTITLES)) {
            DefaultMutableTreeNode subtitlesCategory = new DefaultMutableTreeNode(SUBTITLES_CATEGORY_LABEL, true);
            rootNode.add(subtitlesCategory);
            tracks.stream().filter(i -> i.getType() == TrackType.SUBTITLES).forEachOrdered(i -> subtitlesCategory.add(new DefaultMutableTreeNode(i, false)));
        }
        if (tracks.stream().anyMatch(i -> i.getType() == TrackType.CHAPTERS)) {
            DefaultMutableTreeNode chaptersCategory = new DefaultMutableTreeNode(CHAPTERS_CATEGORY_LABEL, true);
            rootNode.add(chaptersCategory);
            tracks.stream().filter(i -> i.getType() == TrackType.CHAPTERS).forEachOrdered(i -> chaptersCategory.add(new DefaultMutableTreeNode(i, false)));
        }
        trackTree.setModel(new DefaultTreeModel(rootNode, true));
        trackTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        // expand all
        for (int i = 0; i < trackTree.getRowCount(); i++) {
            trackTree.expandRow(i);
        }
    }

}
