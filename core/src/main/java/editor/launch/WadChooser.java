package editor.launch;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class WadChooser extends JFileChooser {

    public WadChooser() {
        super();
        JButton newFileButton = new JButton("New File");
        newFileButton.setSize(64, 64);
        newFileButton.addActionListener(e -> newFile());
        setAccessory(newFileButton);
    }

    private void newFile() {
        try {
            String path = WadChooser.this.getCurrentDirectory().toString() + "/unamed.wad";
            File newFile = new File(path);
            int i = 0;

            while (!newFile.createNewFile()) {
                i++;
                path = WadChooser.this.getCurrentDirectory().toString() + "/unamed" + i + ".wad";
                newFile = new File(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
