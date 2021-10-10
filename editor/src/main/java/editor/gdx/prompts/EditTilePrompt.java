package editor.gdx.prompts;

import javax.swing.*;
import java.awt.*;

public class EditTilePrompt extends JPanel {

    private JLabel arg1Label;
    private JSpinner arg1Spinner;
    private JLabel arg2Label;
    private JSpinner arg2Spinner;
    private JPanel buttonPanel;
    private JButton cancelButton;
    private JLabel effectLabel;
    private JSpinner effectSpinner;
    private JLabel lightLabel;
    private JSpinner lightSpinner;
    private JPanel mainPanel;
    private JButton newTagButton;
    private JButton okButton;
    private JPanel previewPanel;
    private JCheckBox repeatCheckBox;
    private JLabel repeatLabel;
    private JPanel settingsPanel;
    private JCheckBox solidCheckBox;
    private JLabel solidLabel;
    private JLabel tagLabel;
    private JSpinner tagSpinner;
    private JComboBox<String> textureComboBox;
    private JLabel texturePreviewLabel;

    public EditTilePrompt() {

        mainPanel = new JPanel();
        previewPanel = new JPanel();
        texturePreviewLabel = new JLabel();
        textureComboBox = new JComboBox<>();
        settingsPanel = new JPanel();
        solidCheckBox = new JCheckBox();
        solidLabel = new JLabel();
        lightSpinner = new JSpinner();
        lightLabel = new JLabel();
        effectSpinner = new JSpinner();
        effectLabel = new JLabel();
        arg1Spinner = new JSpinner();
        arg1Label = new JLabel();
        arg2Spinner = new JSpinner();
        arg2Label = new JLabel();
        repeatCheckBox = new JCheckBox();
        repeatLabel = new JLabel();
        tagSpinner = new JSpinner();
        tagLabel = new JLabel();
        newTagButton = new JButton();
        buttonPanel = new JPanel();
        cancelButton = new JButton();
        okButton = new JButton();

        setPreferredSize(new Dimension(420, 350));

        mainPanel.setBorder(BorderFactory.createEtchedBorder());

        previewPanel.setBorder(BorderFactory.createEtchedBorder());

        texturePreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        texturePreviewLabel.setAlignmentX(0.5F);
        texturePreviewLabel.setBorder(BorderFactory.createEtchedBorder());
        texturePreviewLabel.setPreferredSize(new Dimension(64, 64));

        GroupLayout previewPanelLayout = new GroupLayout(previewPanel);
        previewPanel.setLayout(previewPanelLayout);
        previewPanelLayout.setHorizontalGroup(
                previewPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, previewPanelLayout.createSequentialGroup()
                                .addContainerGap(32, Short.MAX_VALUE)
                                .addComponent(texturePreviewLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(32, 32, 32))
                        .addGroup(previewPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(textureComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        previewPanelLayout.setVerticalGroup(
                previewPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(previewPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(texturePreviewLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(textureComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        settingsPanel.setBorder(BorderFactory.createEtchedBorder());

        solidLabel.setText("Solid:");

        lightLabel.setText("Light:");

        effectLabel.setText("Effect:");

        arg1Label.setText("Arg1:");

        arg2Label.setText("Arg2:");

        repeatLabel.setText("Repeat:");

        tagLabel.setText("Tag:");

        newTagButton.setText("New Tag");

        GroupLayout settingsPanelLayout = new GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsPanelLayout);
        settingsPanelLayout.setHorizontalGroup(
                settingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(settingsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(settingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(GroupLayout.Alignment.TRAILING, settingsPanelLayout.createSequentialGroup()
                                                .addComponent(solidLabel)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(solidCheckBox))
                                        .addGroup(GroupLayout.Alignment.TRAILING, settingsPanelLayout.createSequentialGroup()
                                                .addComponent(repeatLabel)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(repeatCheckBox))
                                        .addGroup(GroupLayout.Alignment.TRAILING, settingsPanelLayout.createSequentialGroup()
                                                .addGroup(settingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(lightLabel)
                                                        .addComponent(effectLabel)
                                                        .addComponent(arg1Label)
                                                        .addComponent(arg2Label)
                                                        .addComponent(tagLabel))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(settingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(tagSpinner, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                                                        .addComponent(arg2Spinner, GroupLayout.Alignment.TRAILING)
                                                        .addComponent(arg1Spinner, GroupLayout.Alignment.TRAILING)
                                                        .addComponent(effectSpinner, GroupLayout.Alignment.TRAILING)
                                                        .addComponent(lightSpinner, GroupLayout.Alignment.TRAILING)))
                                        .addGroup(GroupLayout.Alignment.TRAILING, settingsPanelLayout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(newTagButton)))
                                .addContainerGap())
        );
        settingsPanelLayout.setVerticalGroup(
                settingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(settingsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(settingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(solidCheckBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(solidLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(settingsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lightSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lightLabel))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(settingsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(effectSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(effectLabel))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(settingsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(arg1Spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(arg1Label))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(settingsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(arg2Spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(arg2Label))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(settingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(repeatCheckBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(repeatLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(settingsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(tagSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(tagLabel))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(newTagButton)
                                .addContainerGap(28, Short.MAX_VALUE))
        );

        GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(previewPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(settingsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(settingsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(previewPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );

        buttonPanel.setBorder(BorderFactory.createEtchedBorder());

        cancelButton.setText("Cancel");

        okButton.setText("OK");

        GroupLayout buttonPanelLayout = new GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
                buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
                                .addGap(0, 285, Short.MAX_VALUE)
                                .addComponent(okButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelButton))
        );
        buttonPanelLayout.setVerticalGroup(
                buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(cancelButton)
                                .addComponent(okButton))
        );

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(buttonPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(mainPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
    }
}
