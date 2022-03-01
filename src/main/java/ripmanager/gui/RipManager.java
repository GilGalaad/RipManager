package ripmanager.gui;

import ripmanager.engine.enums.Encoder;

public class RipManager extends javax.swing.JFrame {

    public RipManager() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        audioDemuxButtonGroup = new javax.swing.ButtonGroup();
        sourceTextField = new javax.swing.JTextField();
        sourceButton = new javax.swing.JButton();
        analyzeButton = new javax.swing.JButton();
        printCommandsButton = new javax.swing.JButton();
        demuxButton = new javax.swing.JButton();
        encodeButton = new javax.swing.JButton();
        demuxEncodeButton = new javax.swing.JButton();
        treeScrollPane = new javax.swing.JScrollPane();
        trackTree = new javax.swing.JTree();
        generalDemuxOptionsPanel = new javax.swing.JPanel();
        selectedCheckBox = new javax.swing.JCheckBox();
        videoDemuxOptionsPanel = new javax.swing.JPanel();
        convertToHuffCheckBox = new javax.swing.JCheckBox();
        audioDemuxOptionsPanel = new javax.swing.JPanel();
        losslessAndLossyRadioButton = new javax.swing.JRadioButton();
        losslessRadioButton = new javax.swing.JRadioButton();
        lossyRadioButton = new javax.swing.JRadioButton();
        extractCoreCheckBox = new javax.swing.JCheckBox();
        normalizeCheckBox = new javax.swing.JCheckBox();
        encodingOptionsPanel = new javax.swing.JPanel();
        encoderComboBox = new javax.swing.JComboBox<>();
        encoderLabel = new javax.swing.JLabel();
        crfLabel = new javax.swing.JLabel();
        crfSlider = new javax.swing.JSlider();
        suffixTextField = new javax.swing.JTextField();
        suffixLabel = new javax.swing.JLabel();
        y4mCheckBox = new javax.swing.JCheckBox();
        y4mLabel = new javax.swing.JLabel();
        outputScrollPane = new javax.swing.JScrollPane();
        outputTextArea = new javax.swing.JTextArea();
        progressBar = new javax.swing.JProgressBar();
        etaLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RipManager");

        sourceTextField.setEditable(false);

        sourceButton.setText("...");

        analyzeButton.setText("Analyze");

        printCommandsButton.setText("Print Commands");
        printCommandsButton.setEnabled(false);

        demuxButton.setText("Demux");
        demuxButton.setEnabled(false);

        encodeButton.setText("Encode");

        demuxEncodeButton.setText("Demux & Encode");
        demuxEncodeButton.setEnabled(false);

        trackTree.setRootVisible(false);
        trackTree.setRowHeight(18);
        treeScrollPane.setViewportView(trackTree);

        generalDemuxOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("General demux options"));

        selectedCheckBox.setText("Select track");
        selectedCheckBox.setEnabled(false);

        javax.swing.GroupLayout generalDemuxOptionsPanelLayout = new javax.swing.GroupLayout(generalDemuxOptionsPanel);
        generalDemuxOptionsPanel.setLayout(generalDemuxOptionsPanelLayout);
        generalDemuxOptionsPanelLayout.setHorizontalGroup(
                generalDemuxOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(generalDemuxOptionsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(selectedCheckBox)
                                .addContainerGap(192, Short.MAX_VALUE))
        );
        generalDemuxOptionsPanelLayout.setVerticalGroup(
                generalDemuxOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, generalDemuxOptionsPanelLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(selectedCheckBox)
                                .addContainerGap())
        );

        videoDemuxOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Video demux options"));
        videoDemuxOptionsPanel.setPreferredSize(new java.awt.Dimension(291, 59));

        convertToHuffCheckBox.setText("Convert to FFMpeg HuffYUV");
        convertToHuffCheckBox.setEnabled(false);

        javax.swing.GroupLayout videoDemuxOptionsPanelLayout = new javax.swing.GroupLayout(videoDemuxOptionsPanel);
        videoDemuxOptionsPanel.setLayout(videoDemuxOptionsPanelLayout);
        videoDemuxOptionsPanelLayout.setHorizontalGroup(
                videoDemuxOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(videoDemuxOptionsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(convertToHuffCheckBox)
                                .addContainerGap(112, Short.MAX_VALUE))
        );
        videoDemuxOptionsPanelLayout.setVerticalGroup(
                videoDemuxOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(videoDemuxOptionsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(convertToHuffCheckBox)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        audioDemuxOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Audio demux options"));
        audioDemuxOptionsPanel.setPreferredSize(new java.awt.Dimension(291, 163));

        audioDemuxButtonGroup.add(losslessAndLossyRadioButton);
        losslessAndLossyRadioButton.setText("Keep both lossless and lossy");
        losslessAndLossyRadioButton.setEnabled(false);

        audioDemuxButtonGroup.add(losslessRadioButton);
        losslessRadioButton.setText("Keep lossless");
        losslessRadioButton.setEnabled(false);

        audioDemuxButtonGroup.add(lossyRadioButton);
        lossyRadioButton.setText("Keep lossy");
        lossyRadioButton.setEnabled(false);

        extractCoreCheckBox.setText("Extract core");
        extractCoreCheckBox.setEnabled(false);

        normalizeCheckBox.setText("Normalize");
        normalizeCheckBox.setEnabled(false);

        javax.swing.GroupLayout audioDemuxOptionsPanelLayout = new javax.swing.GroupLayout(audioDemuxOptionsPanel);
        audioDemuxOptionsPanel.setLayout(audioDemuxOptionsPanelLayout);
        audioDemuxOptionsPanelLayout.setHorizontalGroup(
                audioDemuxOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(audioDemuxOptionsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(audioDemuxOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(losslessAndLossyRadioButton)
                                        .addComponent(losslessRadioButton)
                                        .addComponent(lossyRadioButton)
                                        .addComponent(extractCoreCheckBox)
                                        .addComponent(normalizeCheckBox))
                                .addContainerGap(112, Short.MAX_VALUE))
        );
        audioDemuxOptionsPanelLayout.setVerticalGroup(
                audioDemuxOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(audioDemuxOptionsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(losslessAndLossyRadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(losslessRadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lossyRadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(normalizeCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(extractCoreCheckBox)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        encodingOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Encoding options"));

        encoderLabel.setText("Encoder");

        crfLabel.setText("CRF");

        crfSlider.setMajorTickSpacing(1);
        crfSlider.setMaximum(24);
        crfSlider.setMinimum(16);
        crfSlider.setPaintLabels(true);
        crfSlider.setPaintTicks(true);
        crfSlider.setSnapToTicks(true);
        crfSlider.setValue(18);

        suffixLabel.setText("Suffix");

        y4mLabel.setText("--y4m");

        javax.swing.GroupLayout encodingOptionsPanelLayout = new javax.swing.GroupLayout(encodingOptionsPanel);
        encodingOptionsPanel.setLayout(encodingOptionsPanelLayout);
        encodingOptionsPanelLayout.setHorizontalGroup(
                encodingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(encodingOptionsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(encodingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(encoderLabel)
                                        .addComponent(crfLabel)
                                        .addComponent(suffixLabel)
                                        .addComponent(y4mLabel))
                                .addGap(20, 20, 20)
                                .addGroup(encodingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(encodingOptionsPanelLayout.createSequentialGroup()
                                                .addComponent(y4mCheckBox)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addComponent(suffixTextField)
                                        .addComponent(crfSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(encoderComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        encodingOptionsPanelLayout.setVerticalGroup(
                encodingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(encodingOptionsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(encodingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(encoderComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(encoderLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(encodingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                        .addComponent(crfSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(crfLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(encodingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(suffixTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(suffixLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(encodingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(y4mLabel)
                                        .addComponent(y4mCheckBox))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        outputTextArea.setEditable(false);
        outputTextArea.setColumns(20);
        outputTextArea.setLineWrap(true);
        outputTextArea.setRows(5);
        outputScrollPane.setViewportView(outputTextArea);

        progressBar.setStringPainted(true);

        etaLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        etaLabel.setText("ETA: 00:00:00");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(sourceTextField)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(sourceButton))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(etaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(treeScrollPane)
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(videoDemuxOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(audioDemuxOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(encodingOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(generalDemuxOptionsPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(analyzeButton, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(printCommandsButton, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                                                .addComponent(demuxButton, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                                                .addComponent(encodeButton, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                                                .addComponent(demuxEncodeButton, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)))
                                .addGap(18, 18, 18)
                                .addComponent(outputScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1047, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(sourceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(sourceButton))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(analyzeButton)
                                                        .addComponent(printCommandsButton)
                                                        .addComponent(demuxButton)
                                                        .addComponent(encodeButton)
                                                        .addComponent(demuxEncodeButton))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(generalDemuxOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(videoDemuxOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(audioDemuxOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(encodingOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(0, 594, Short.MAX_VALUE))
                                                        .addComponent(treeScrollPane))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(etaLabel)))
                                        .addComponent(outputScrollPane))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton analyzeButton;
    protected javax.swing.ButtonGroup audioDemuxButtonGroup;
    protected javax.swing.JPanel audioDemuxOptionsPanel;
    protected javax.swing.JCheckBox convertToHuffCheckBox;
    protected javax.swing.JLabel crfLabel;
    protected javax.swing.JSlider crfSlider;
    protected javax.swing.JButton demuxButton;
    protected javax.swing.JButton demuxEncodeButton;
    protected javax.swing.JButton encodeButton;
    protected javax.swing.JComboBox<Encoder> encoderComboBox;
    protected javax.swing.JLabel encoderLabel;
    protected javax.swing.JPanel encodingOptionsPanel;
    protected javax.swing.JLabel etaLabel;
    protected javax.swing.JCheckBox extractCoreCheckBox;
    protected javax.swing.JPanel generalDemuxOptionsPanel;
    protected javax.swing.JRadioButton losslessAndLossyRadioButton;
    protected javax.swing.JRadioButton losslessRadioButton;
    protected javax.swing.JRadioButton lossyRadioButton;
    protected javax.swing.JCheckBox normalizeCheckBox;
    protected javax.swing.JScrollPane outputScrollPane;
    protected javax.swing.JTextArea outputTextArea;
    protected javax.swing.JButton printCommandsButton;
    protected javax.swing.JProgressBar progressBar;
    protected javax.swing.JCheckBox selectedCheckBox;
    protected javax.swing.JButton sourceButton;
    protected javax.swing.JTextField sourceTextField;
    protected javax.swing.JLabel suffixLabel;
    protected javax.swing.JTextField suffixTextField;
    protected javax.swing.JTree trackTree;
    protected javax.swing.JScrollPane treeScrollPane;
    protected javax.swing.JPanel videoDemuxOptionsPanel;
    protected javax.swing.JCheckBox y4mCheckBox;
    protected javax.swing.JLabel y4mLabel;
    // End of variables declaration//GEN-END:variables
}
