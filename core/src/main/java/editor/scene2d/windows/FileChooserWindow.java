package editor.scene2d.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import core.game.logic.GameLogic;
import core.wad.funcs.WadFuncs;
import editor.launch.EditorScreen;
import net.mtrop.doom.WadFile;
import org.lwjgl.system.CallbackI;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class FileChooserWindow extends Window {

    private EditorScreen editor;
    private String currentPath;
    private File currentDir;
    private File chosenFile = null;
    private Label currentPathLabel;
    private ScrollPane filePane;
    private List<String> fileList;
    private ScrollPane resourcePane;
    private List<String> resourceList;
    private Table buttonTable;
    private CheckBox hideFiles;
    private TextButton addFileButton;
    private TextButton addResourceButton;
    private TextButton removeResourceButton;
    private TextButton okButton;
    private TextButton cancelButton;
    private Array<String> resources;

    //A dialog that pops up to crate new files
    private class FileNamerWindow extends Window {

        private File currentDir;
        private FileChooserWindow parent;
        private TextField name;
        private TextButton okButton;
        private TextButton cancelButton;
        private SelectBox<String> type;

        public FileNamerWindow(Skin skin, File currentDir, FileChooserWindow parent) {
            super("Name new file:", skin);
            this.currentDir = currentDir;
            this.parent = parent;
            setModal(true);

            type = new SelectBox<>(skin);
            type.setItems(".wad", ".lmp");

            name = new TextField("newwad", skin);
            add(name);
            add(type);
            row();
            okButton = new TextButton("OK", skin);

            okButton.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);

                    File newFile = new File(currentDir.getAbsolutePath() + "/" + name.getText() + type.getSelected());

                    if (newFile.exists()) {
                        System.out.println("That file already exists.");
                        return;
                    }

                    try {
                        newFile.createNewFile();

                        if (type.getSelected().equals(".wad")) {
                            WadFile.createWadFile(newFile);
                        }
                    } catch (IOException e) {
                        System.out.println("Could not create file " + newFile.getAbsolutePath());
                    }

                    parent.updateList();
                    remove();
                }

            });

            add(okButton);
            cancelButton = new TextButton("Cancel", skin);

            cancelButton.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    remove();
                }
            });

            add(cancelButton);
            pack();
        }
    }

    public FileChooserWindow(String title, Skin skin, EditorScreen editor) {
        super(title, skin);
        this.editor = editor;
        currentPath = Gdx.files.internal("assets").file().getAbsolutePath();
        currentDir = new File(currentPath);
        resources = new Array<>();

        setModal(true);
        setResizable(true);

        currentPathLabel = new Label(currentDir.getName(), skin);
        add(currentPathLabel);
        add(new Label("resources", skin));
        row();
        fileList = new List<>(skin);
        filePane = new ScrollPane(fileList, skin);
        filePane.setFadeScrollBars(false);

        fileList.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                selectFile();
            }
        });

        add(filePane).height(240f).width(240f);

        resourceList = new List<>(skin);
        resourcePane = new ScrollPane(resourceList, skin);
        resourcePane.setFadeScrollBars(false);

        add(resourcePane).height(240f).width(240f);
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
        addResourceButton = new TextButton("Add current File as resource", skin);
        addResourceButton.setDisabled(true);

        addResourceButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                addResource();
            }
        });

        add(addResourceButton);


        removeResourceButton = new TextButton("Remove resource", skin);
        removeResourceButton.setDisabled(true);

        removeResourceButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                removeResource();
            }
        });

        add(removeResourceButton);

        row();
        buttonTable = new Table(skin);

        addFileButton = new TextButton("+", skin);

        addFileButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                addNewFile();
            }
        });

        buttonTable.add(addFileButton);
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
        updateResources();

        fileList.setSelected("resource.wad");
        if (fileList.getSelected().equals("resource.wad")) {
            selectFile();
            addResource();
        }
    }

    private void updateResources() {
        resourceList.setItems(resources);
    }

    private void addResource() {
        if (!resources.contains(chosenFile.getAbsolutePath(), true)) {
            resources.add(chosenFile.getAbsolutePath());
        }
        updateResources();
    }

    private void removeResource() {
        resources.removeIndex(resourceList.getSelectedIndex());
        updateResources();
    }

    private void updateList() {
        Array<String> listMembers = new Array<>();
        Array<String> directoryNames = new Array<>();
        Array<String> fileNames = new Array<>();

        currentPathLabel.setText(currentDir.getName());

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
            if (f.getName().toLowerCase().endsWith(".wad") || f.getName().toLowerCase().endsWith(".lmp")
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
                    addResourceButton.setDisabled(true);
                    updateList();
                } else {
                    chosenFile = f;
                    okButton.setDisabled(false);
                    addResourceButton.setDisabled(false);
                }
                break;
            }
        }
    }

    private void addNewFile() {
        editor.stage.addActor(new FileNamerWindow(getSkin(), currentDir, this));
    }


    private void chooseFile() {

        if (chosenFile.getName().toLowerCase().endsWith(".wad")) {
            try {
                editor.file = new WadFile(chosenFile);
                editor.resources.clear();

                for (String s : resources) {
                    editor.resources.add(new WadFile(s));
                }

                editor.resources.add(editor.file);
                GameLogic.entityList.clear();
                WadFuncs.loadTextures(editor.resources);
                close();
            } catch (IOException e) {
                System.out.println("Invalid file...");
            }
        } else {

            if (resources.isEmpty()) {
                System.out.println("Must have at least once resource to load a standalone level.");
                return;
            }

            editor.soloFile = chosenFile;

            for (String s : resources) {
                try {
                    editor.resources.add(new WadFile(s));
                } catch (IOException e) {
                    System.out.println("Invalid file...");
                }
            }

            GameLogic.entityList.clear();
            WadFuncs.loadTextures(editor.resources);
            close();
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
        if (editor.file != null && editor.soloFile == null) {
            editor.openLevelPrompt();
        } else {
            editor.loadLevel();
        }
    }
}
