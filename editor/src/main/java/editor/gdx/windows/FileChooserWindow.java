package editor.gdx.windows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import editor.gdx.launch.EditorScreen;
import net.mtrop.doom.WadFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class FileChooserWindow extends Window {

    private EditorScreen editor;
    private String currentPath;
    private File currentDir;
    private File chosenFile = null;
    private Label currentPathLabel;
    private ScrollPane scrollPane;
    private List<String> fileList;
    private Table buttonTable;
    private CheckBox hideFiles;
    private TextButton okButton;
    private TextButton cancelButton;

    public FileChooserWindow(String title, Skin skin, EditorScreen editor) {
        super(title, skin);
        this.editor = editor;
        currentPath = System.getProperty("user.home");
        currentDir = new File(currentPath);
        setModal(true);
        setResizable(true);
        currentPathLabel = new Label(currentPath, skin);
        add(currentPathLabel);
        row();
        fileList = new List<>(skin);
        scrollPane = new ScrollPane(fileList, skin);

        fileList.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                selectFile();
            }
        });

        add(scrollPane).height(360f);
        row();
        hideFiles = new CheckBox("Hide Hidden Files?", skin);
        hideFiles.setChecked(true);

        hideFiles.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                updateList();
            }
        });

        add(hideFiles);
        row();
        buttonTable = new Table(skin);
        okButton = new TextButton("OK", skin);
        okButton.setDisabled(true);

        okButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                chooseFile();
            }
        });

        buttonTable.add(okButton);
        cancelButton = new TextButton("Cancel", skin);

        cancelButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                cancelFile();
            }
        });

        buttonTable.add(cancelButton);
        add(buttonTable);
        pack();
        updateList();
    }

    private void updateList() {
        Array<String> listMembers = new Array<>();
        Array<String> directoryNames = new Array<>();
        Array<String> fileNames = new Array<>();

        listMembers.add("..");

        for (File f : Objects.requireNonNull(currentDir.listFiles())) {
            if (f.isDirectory()
                && !(f.getName().charAt(0) == '.' && hideFiles.isChecked())) {
                directoryNames.add(f.getName());
            }
        }

        directoryNames.sort();
        listMembers.addAll(directoryNames);

        for (File f : Objects.requireNonNull(currentDir.listFiles())) {
            if (f.getName().toLowerCase().endsWith(".wad")
                    && !(f.getName().charAt(0) == '.' && hideFiles.isChecked())) {
                fileNames.add(f.getName());
            }
        }

        fileNames.sort();
        listMembers.addAll(fileNames);

        fileList.setItems(listMembers);
    }

    private void selectFile() {
        String fileName = fileList.getSelected();

        if (fileName.equals("..")) {
            currentDir = currentDir.getParentFile();
            currentPath = currentDir.getAbsolutePath();
            updateList();
            return;
        }

        for (File f : Objects.requireNonNull(currentDir.listFiles())) {
            if (f.getName().equals(fileName)) {

                if (f.isDirectory()) {
                    currentDir = f;
                    currentPath = f.getAbsolutePath();
                    okButton.setDisabled(true);
                    updateList();
                } else {
                    chosenFile = f;
                    okButton.setDisabled(false);
                }
                break;
            }
        }
    }

    private void chooseFile() {

        try {
            editor.file = new WadFile(chosenFile);
            close();
        } catch (IOException e) {
            System.out.println("Invalid file...");
        }
    }

    private void cancelFile() {

        if (editor.file == null) {
            System.exit(0);
        }
        close();
    }

    private void close() {
        remove();
        editor.windowOpen = false;
        editor.openLevelPrompt();
    }
}
