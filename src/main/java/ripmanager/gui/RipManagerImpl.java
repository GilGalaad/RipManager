package ripmanager.gui;

import lombok.extern.log4j.Log4j2;
import ripmanager.common.ExceptionUtils;
import ripmanager.engine.dto.*;
import ripmanager.worker.BackgroundWorker;
import ripmanager.worker.WorkerOutcome;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionListener;
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
    private static final Icon VIDEO_CATEGORY_ICON = new ImageIcon(RipManagerImpl.class.getResource("/icons/video.png"));
    private static final Icon AUDIO_CATEGORY_ICON = new ImageIcon(RipManagerImpl.class.getResource("/icons/audio.png"));
    private static final Icon SUBTITLES_CATEGORY_ICON = new ImageIcon(RipManagerImpl.class.getResource("/icons/subtitles.png"));
    private static final Icon CHAPTERS_CATEGORY_ICON = new ImageIcon(RipManagerImpl.class.getResource("/icons/chapters.png"));
    private static final Icon SELECTED_YES_ICON = new ImageIcon(RipManagerImpl.class.getResource("/icons/yes.png"));
    private static final Icon SELECTED_NO_ICON = new ImageIcon(RipManagerImpl.class.getResource("/icons/no.png"));
    public static final String ETA_DEFAULT = "ETA: 00:00:00";

    private boolean running = false;
    private BackgroundWorker worker;

    public RipManagerImpl() {
        super();

        sourceTextField.setText("D:\\iso\\video.mkv");
        etaLabel.setText(ETA_DEFAULT);

        sourceButton.addActionListener(evt -> sourceButtonClicked());
        analyzeButton.addActionListener(evt -> analyzeButtonClicked());

        trackTree.setModel(null);
        trackTree.setRootVisible(false);
        trackTree.setRowHeight(18);
        trackTree.addTreeWillExpandListener(customTreeWillExpandListener());
        trackTree.setCellRenderer(customCellRenderer());
        trackTree.addTreeSelectionListener(nodeSelected());
    }

    public void startBackgroundTask() {
        running = true;
        sourceButton.setEnabled(false);
        //analyzeButton.setEnabled(false);
        analyzeButton.setText("Abort");
        progressBar.setValue(0);
        etaLabel.setText(ETA_DEFAULT);
        trackTree.setModel(null);
    }

    public void endBackgroundTask() {
        running = false;
        sourceButton.setEnabled(true);
        //analyzeButton.setEnabled(true);
        analyzeButton.setText("Analyze");
        progressBar.setValue(0);
        etaLabel.setText(ETA_DEFAULT);
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
            worker.addPropertyChangeListener(propertyChanged());
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

    private PropertyChangeListener propertyChanged() {
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

    // expand listener that prevents the collapse of category nodes
    private TreeWillExpandListener customTreeWillExpandListener() {
        return new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) {
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                throw new ExpandVetoException(event);
            }
        };
    }

    // customization of tree rendering, with icon and calculated labels
    private DefaultTreeCellRenderer customCellRenderer() {
        return new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();
                // customizing categories
                if (VIDEO_CATEGORY_LABEL.equals(node.getUserObject().toString())) {
                    this.setIcon(VIDEO_CATEGORY_ICON);
                } else if (AUDIO_CATEGORY_LABEL.equals(node.getUserObject().toString())) {
                    this.setIcon(AUDIO_CATEGORY_ICON);
                } else if (SUBTITLES_CATEGORY_LABEL.equals(node.getUserObject().toString())) {
                    this.setIcon(SUBTITLES_CATEGORY_ICON);
                } else if (CHAPTERS_CATEGORY_LABEL.equals(node.getUserObject().toString())) {
                    this.setIcon(CHAPTERS_CATEGORY_ICON);
                }
                // customizing tracks
                if (Track.class.isAssignableFrom(node.getUserObject().getClass())) {
                    this.setText(((Track) userObject).getLabel());
                }
                if (userObject instanceof VideoTrack) {
                    this.setIcon(((VideoTrack) userObject).getDemuxOptions().isSelected() ? SELECTED_YES_ICON : SELECTED_NO_ICON);
                } else if (userObject instanceof AudioTrack) {
                    this.setIcon(((AudioTrack) userObject).getDemuxOptions().isSelected() ? SELECTED_YES_ICON : SELECTED_NO_ICON);
                } else if (userObject instanceof SubtitlesTrack) {
                    this.setIcon(((SubtitlesTrack) userObject).getDemuxOptions().isSelected() ? SELECTED_YES_ICON : SELECTED_NO_ICON);
                } else if (userObject instanceof ChaptersTrack) {
                    this.setIcon(((ChaptersTrack) userObject).getDemuxOptions().isSelected() ? SELECTED_YES_ICON : SELECTED_NO_ICON);
                }
                return this;
            }
        };
    }

    private TreeSelectionListener nodeSelected() {
        return evt -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) trackTree.getLastSelectedPathComponent();
            if (node == null) {
                return;
            }
            // prevent selection of categories
            if (!node.isLeaf()) {
                trackTree.setSelectionPath(evt.getOldLeadSelectionPath());
                return;
            }
            Object userObject = node.getUserObject();
            if (userObject instanceof VideoTrack) {
                VideoTrack track = (VideoTrack) userObject;
                selectedCheckBox.setEnabled(true);
                selectedCheckBox.setSelected(track.getDemuxOptions().isSelected());
            } else if (userObject instanceof AudioTrack) {
                AudioTrack track = (AudioTrack) userObject;
                selectedCheckBox.setEnabled(true);
                selectedCheckBox.setSelected(track.getDemuxOptions().isSelected());
            } else if (userObject instanceof SubtitlesTrack) {
                SubtitlesTrack track = (SubtitlesTrack) userObject;
                selectedCheckBox.setEnabled(true);
                selectedCheckBox.setSelected(track.getDemuxOptions().isSelected());
            } else if (userObject instanceof ChaptersTrack) {
                ChaptersTrack track = (ChaptersTrack) userObject;
                selectedCheckBox.setEnabled(true);
                selectedCheckBox.setSelected(track.getDemuxOptions().isSelected());
            }
        };
    }

}
