package editor.gdx.prompts;

import editor.gdx.launch.LevelEditor;
import net.mtrop.doom.WadFile;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class FilePrompt extends JPanel {

    private WadFile file;
    private final EditorFrame host;
    private final LevelEditor editor;

    private JButton chooseButton;
    private JFileChooser fileChooser;
    private JTextField filePathField;
    private JLabel label;
    private JButton openButton;
    private JLabel levelLabel;
    private JSpinner levelSpinner;

    public FilePrompt(WadFile file, EditorFrame host, LevelEditor editor) {

        this.file = file;
        this.host = host;
        this.editor = editor;

        fileChooser = new JFileChooser();
        label = new JLabel();
        filePathField = new JTextField();
        chooseButton = new JButton();
        openButton = new JButton();
        levelLabel = new JLabel();
        levelSpinner = new JSpinner();

        label.setText("Choose a file:");

        filePathField.setEditable(false);

        chooseButton.setText("Choose");
        chooseButton.addActionListener(evt1 -> chooseButtonActionPerformed(evt1));

        openButton.setText("Open");
        openButton.setEnabled(false);
        openButton.addActionListener(evt -> openButtonActionPerformed(evt));

        levelLabel.setText("Level:");

        levelSpinner.setModel(new SpinnerNumberModel(1, 1, 999, 1));
        levelSpinner.setEnabled(false);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(filePathField, GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(chooseButton))
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(openButton))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(label)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(levelLabel)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(levelSpinner, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(label)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(filePathField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(chooseButton))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(levelLabel)
                                        .addComponent(levelSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(openButton)
                                .addContainerGap(171, Short.MAX_VALUE))
        );
    }

    private void chooseButtonActionPerformed(java.awt.event.ActionEvent evt) {
        fileChooser.showOpenDialog(this);
        File rawFile = fileChooser.getSelectedFile();
        try {
            file = new WadFile(rawFile);
            openButton.setEnabled(true);
            levelSpinner.setEnabled(true);
            filePathField.setText(file.getFileAbsolutePath());
        } catch (IOException e) {
            System.out.println("Not a valid wad file!");
            openButton.setEnabled(false);
            levelSpinner.setEnabled(false);
        }
    }

    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {
        host.dispose();
    }
}
