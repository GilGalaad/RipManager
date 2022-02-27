package ripmanager.gui;

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
        outputScrollPane = new javax.swing.JScrollPane();
        outputTextArea = new javax.swing.JTextArea();
        progressBar = new javax.swing.JProgressBar();
        etaLabel = new javax.swing.JLabel();
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
        printCommandsButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RipManager");

        sourceTextField.setEditable(false);

        sourceButton.setText("...");

        analyzeButton.setText("Analyze");

        outputTextArea.setEditable(false);
        outputTextArea.setColumns(20);
        outputTextArea.setRows(5);
        outputScrollPane.setViewportView(outputTextArea);

        progressBar.setStringPainted(true);

        etaLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

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
                                .addContainerGap(151, Short.MAX_VALUE))
        );
        generalDemuxOptionsPanelLayout.setVerticalGroup(
                generalDemuxOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, generalDemuxOptionsPanelLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(selectedCheckBox)
                                .addContainerGap())
        );

        videoDemuxOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Video demux options"));

        convertToHuffCheckBox.setText("Convert to FFMpeg HuffYUV");
        convertToHuffCheckBox.setEnabled(false);

        javax.swing.GroupLayout videoDemuxOptionsPanelLayout = new javax.swing.GroupLayout(videoDemuxOptionsPanel);
        videoDemuxOptionsPanel.setLayout(videoDemuxOptionsPanelLayout);
        videoDemuxOptionsPanelLayout.setHorizontalGroup(
                videoDemuxOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(videoDemuxOptionsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(convertToHuffCheckBox)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        videoDemuxOptionsPanelLayout.setVerticalGroup(
                videoDemuxOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(videoDemuxOptionsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(convertToHuffCheckBox)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        audioDemuxOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Audio demux options"));

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
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        printCommandsButton.setText("Print Commands");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(sourceTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 930, Short.MAX_VALUE)
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
                                                        .addComponent(generalDemuxOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(videoDemuxOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(audioDemuxOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(analyzeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(printCommandsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addGap(18, 18, 18)
                                .addComponent(outputScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 981, Short.MAX_VALUE)
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
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(analyzeButton)
                                                        .addComponent(printCommandsButton))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(treeScrollPane)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(generalDemuxOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(videoDemuxOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(audioDemuxOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(0, 0, Short.MAX_VALUE)))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(etaLabel)))
                                        .addComponent(outputScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 978, Short.MAX_VALUE))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton analyzeButton;
    protected javax.swing.ButtonGroup audioDemuxButtonGroup;
    protected javax.swing.JPanel audioDemuxOptionsPanel;
    protected javax.swing.JCheckBox convertToHuffCheckBox;
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
    protected javax.swing.JTree trackTree;
    protected javax.swing.JScrollPane treeScrollPane;
    protected javax.swing.JPanel videoDemuxOptionsPanel;
    // End of variables declaration//GEN-END:variables
}
