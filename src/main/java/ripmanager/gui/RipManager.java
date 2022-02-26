package ripmanager.gui;

public class RipManager extends javax.swing.JFrame {

    public RipManager() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sourceTextField = new javax.swing.JTextField();
        sourceButton = new javax.swing.JButton();
        analyzeButton = new javax.swing.JButton();
        outputScrollPane = new javax.swing.JScrollPane();
        outputTextArea = new javax.swing.JTextArea();
        progressBar = new javax.swing.JProgressBar();
        etaLabel = new javax.swing.JLabel();
        treeScrollPane = new javax.swing.JScrollPane();
        trackTree = new javax.swing.JTree();
        demuxOptionsPanel = new javax.swing.JPanel();
        selectedCheckBox = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        convertToHuffCheckBox = new javax.swing.JCheckBox();

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

        demuxOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("General demux options"));

        selectedCheckBox.setText("Select track");
        selectedCheckBox.setEnabled(false);

        javax.swing.GroupLayout demuxOptionsPanelLayout = new javax.swing.GroupLayout(demuxOptionsPanel);
        demuxOptionsPanel.setLayout(demuxOptionsPanelLayout);
        demuxOptionsPanelLayout.setHorizontalGroup(
                demuxOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(demuxOptionsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(selectedCheckBox)
                                .addContainerGap(151, Short.MAX_VALUE))
        );
        demuxOptionsPanelLayout.setVerticalGroup(
                demuxOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(demuxOptionsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(selectedCheckBox)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Video demux options"));

        convertToHuffCheckBox.setText("Convert to FFMpeg HuffYUV");
        convertToHuffCheckBox.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(convertToHuffCheckBox)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(convertToHuffCheckBox)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
                                        .addComponent(analyzeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(etaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(treeScrollPane)
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(demuxOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
                                                .addComponent(analyzeButton)
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(treeScrollPane)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(demuxOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
    protected javax.swing.JCheckBox convertToHuffCheckBox;
    private javax.swing.JPanel demuxOptionsPanel;
    protected javax.swing.JLabel etaLabel;
    private javax.swing.JPanel jPanel1;
    protected javax.swing.JScrollPane outputScrollPane;
    protected javax.swing.JTextArea outputTextArea;
    protected javax.swing.JProgressBar progressBar;
    protected javax.swing.JCheckBox selectedCheckBox;
    protected javax.swing.JButton sourceButton;
    protected javax.swing.JTextField sourceTextField;
    protected javax.swing.JTree trackTree;
    private javax.swing.JScrollPane treeScrollPane;
    // End of variables declaration//GEN-END:variables
}
