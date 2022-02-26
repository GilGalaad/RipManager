package ripmanager.gui;

import lombok.extern.log4j.Log4j2;
import ripmanager.common.ExceptionUtils;
import ripmanager.engine.dto.*;
import ripmanager.worker.BackgroundWorker;
import ripmanager.worker.WorkerOutcome;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
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
        trackTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        trackTree.setRootVisible(false);
        trackTree.setRowHeight(18);
        trackTree.addTreeWillExpandListener(createCustomTreeWillExpandListener());
        trackTree.setCellRenderer(createCustomCellRenderer());
        trackTree.addMouseListener(createCustomMouseListener());
        trackTree.addTreeSelectionListener(this::nodeSelected);

        selectedCheckBox.addActionListener(this::demuxOptionChanged);
        convertToHuffCheckBox.addActionListener(this::demuxOptionChanged);
    }

    public void startBackgroundTask() {
        running = true;
        sourceButton.setEnabled(false);
        analyzeButton.setText("Abort");
        trackTree.setEnabled(false);
        progressBar.setValue(0);
        etaLabel.setText(ETA_DEFAULT);
        disableDemuxOptions();
    }

    public void endBackgroundTask() {
        running = false;
        sourceButton.setEnabled(true);
        analyzeButton.setText("Analyze");
        trackTree.setEnabled(true);
        progressBar.setValue(0);
        etaLabel.setText(ETA_DEFAULT);
        configureDemuxOptions();
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
            // clearing ui
            trackTree.setModel(null);
            outputTextArea.setText(null);
            clearDemuxOptions();
            // starting worker
            worker = new BackgroundWorker(WorkerCommand.ANALYZE, sourceTextField.getText(), null, this);
            worker.addPropertyChangeListener(this::workerPropertyChanged);
            worker.execute();
        } else {
            worker.cancel(true);
        }
    }

    private void workerPropertyChanged(PropertyChangeEvent evt) {
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
        // expand all
        for (int i = 0; i < trackTree.getRowCount(); i++) {
            trackTree.expandRow(i);
        }
    }

    // expand listener that prevents the collapse of category nodes
    private TreeWillExpandListener createCustomTreeWillExpandListener() {
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
    private DefaultTreeCellRenderer createCustomCellRenderer() {
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

    // clear selection if we click outside any row
    private MouseListener createCustomMouseListener() {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (trackTree.getRowForLocation(e.getX(), e.getY()) == -1) {
                    trackTree.clearSelection();
                    clearDemuxOptions();
                    disableDemuxOptions();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        };
    }

    private void nodeSelected(TreeSelectionEvent evt) {
        // if no node selected, nothing to do
        if (trackTree.getModel() == null || trackTree.getLastSelectedPathComponent() == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) trackTree.getLastSelectedPathComponent();
        // prevent selection of categories (non leaf nodes)
        if (!node.isLeaf()) {
            trackTree.setSelectionPath(evt.getOldLeadSelectionPath());
            return;
        }
        configureDemuxOptions();
    }

    private void clearDemuxOptions() {
        selectedCheckBox.setSelected(false);
        convertToHuffCheckBox.setSelected(false);
    }

    private void disableDemuxOptions() {
        selectedCheckBox.setEnabled(false);
        convertToHuffCheckBox.setEnabled(false);
    }

    // called when worker ends to re-enable demux options based on eventual selected node
    // or after a node selection to update demux options
    private void configureDemuxOptions() {
        // if no node selected, nothing to do
        if (trackTree.getModel() == null || trackTree.getLastSelectedPathComponent() == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) trackTree.getLastSelectedPathComponent();
        Object userObject = node.getUserObject();
        if (userObject instanceof VideoTrack) {
            VideoTrack track = (VideoTrack) userObject;
            selectedCheckBox.setEnabled(true);
            selectedCheckBox.setSelected(track.getDemuxOptions().isSelected());
            convertToHuffCheckBox.setEnabled(true);
            convertToHuffCheckBox.setSelected(track.getDemuxOptions().isConvertToHuff());
        } else if (userObject instanceof AudioTrack) {
            AudioTrack track = (AudioTrack) userObject;
            selectedCheckBox.setEnabled(true);
            selectedCheckBox.setSelected(track.getDemuxOptions().isSelected());
            convertToHuffCheckBox.setEnabled(false);
            convertToHuffCheckBox.setSelected(false);
        } else if (userObject instanceof SubtitlesTrack) {
            SubtitlesTrack track = (SubtitlesTrack) userObject;
            selectedCheckBox.setEnabled(true);
            selectedCheckBox.setSelected(track.getDemuxOptions().isSelected());
            convertToHuffCheckBox.setEnabled(false);
            convertToHuffCheckBox.setSelected(false);
        } else if (userObject instanceof ChaptersTrack) {
            ChaptersTrack track = (ChaptersTrack) userObject;
            selectedCheckBox.setEnabled(true);
            selectedCheckBox.setSelected(track.getDemuxOptions().isSelected());
            convertToHuffCheckBox.setEnabled(false);
            convertToHuffCheckBox.setSelected(false);
        }
    }

    private void demuxOptionChanged(ActionEvent evt) {
        // if no node selected, nothing to do
        if (trackTree.getModel() == null || trackTree.getLastSelectedPathComponent() == null) {
            log.warn("ActionEvent called with empty tree or no node selected, the checkbox should be disabled");
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) trackTree.getLastSelectedPathComponent();
        Object userObject = node.getUserObject();
        if (evt.getSource() == selectedCheckBox) {
            if (userObject instanceof VideoTrack) {
                ((VideoTrack) userObject).getDemuxOptions().setSelected(((JCheckBox) evt.getSource()).isSelected());
            } else if (userObject instanceof AudioTrack) {
                ((AudioTrack) userObject).getDemuxOptions().setSelected(((JCheckBox) evt.getSource()).isSelected());
            } else if (userObject instanceof SubtitlesTrack) {
                ((SubtitlesTrack) userObject).getDemuxOptions().setSelected(((JCheckBox) evt.getSource()).isSelected());
            } else if (userObject instanceof ChaptersTrack) {
                ((ChaptersTrack) userObject).getDemuxOptions().setSelected(((JCheckBox) evt.getSource()).isSelected());
            }
            ((DefaultTreeModel) trackTree.getModel()).nodeChanged(node);
        } else if (evt.getSource() == convertToHuffCheckBox) {
            ((VideoTrack) userObject).getDemuxOptions().setConvertToHuff(((JCheckBox) evt.getSource()).isSelected());
        }
        log.info(userObject);
    }

}
