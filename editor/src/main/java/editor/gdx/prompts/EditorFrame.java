package editor.gdx.prompts;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//A JFrame which notifies an object when it closes
public class EditorFrame extends JFrame {
    public EditorFrame(Object syncObject) {
        super();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                synchronized (syncObject) {
                    syncObject.notifyAll();
                }
            }
        });
    }
}
