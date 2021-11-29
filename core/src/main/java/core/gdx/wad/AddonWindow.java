package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

public class AddonWindow extends Window {

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
    private TextButton addResourceButton;
    private TextButton removeResourceButton;
    private TextButton okButton;
    private Array<String> files;
    private Array<String> hashes;

    public AddonWindow(String title, Skin skin) {
        super(title, skin);
        currentPath = Gdx.files.internal("assets").file().getAbsolutePath();
        currentDir = new File(currentPath);

        files = new Array<>();
        hashes = new Array<>();

        setModal(true);
        setResizable(true);

        currentPathLabel = new Label(currentDir.getName(), skin);
        add(currentPathLabel);
        add(new Label("Add-ons", skin));
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
        addResourceButton = new TextButton("Add .WAD", skin);
        addResourceButton.setDisabled(true);

        addResourceButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                addResource();
            }
        });


        removeResourceButton = new TextButton("Remove .WAD", skin);
        removeResourceButton.setDisabled(true);

        removeResourceButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                removeResource();
            }
        });

        row();
        buttonTable = new Table(skin);

        buttonTable.add(addResourceButton);
        buttonTable.add(removeResourceButton);
        okButton = new TextButton("OK", skin);
        okButton.setDisabled(true);

        okButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                TitleScreen.mainMenuTable.setVisible(true);
                close();
            }
        });

        buttonTable.add(okButton);
        add(buttonTable);
        pack();
        updateList();
//        updateResources();
    }

    private void updateResources() {
        resourceList.setItems(files);
        System.out.println("Loading wads");
        MyGDxTest.loadWADS();
    }

    private void addResource() {
        if (!files.contains(chosenFile.getAbsolutePath(), true)) {
            try {
                MyGDxTest.addons.add(chosenFile);
                String hash = Files.asByteSource(chosenFile).hash(Hashing.sha256()).toString();
                MyGDxTest.addonHashes.add(hash);

                files.add(chosenFile.getAbsolutePath());
                hashes.add(hash);

                System.out.println("Hash:\t" + hash);
            } catch (IOException e) {
                System.out.println("Couldn't add resource.");
            }
        }
        updateResources();
    }

    private void removeResource() {
        int index = resourceList.getSelectedIndex();
        files.removeIndex(index);
        hashes.removeIndex(index);

        MyGDxTest.addons.remove(index);
        MyGDxTest.addonHashes.remove(index);
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

    private void close() {
        remove();
    }
}
