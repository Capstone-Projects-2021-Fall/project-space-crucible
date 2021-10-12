package editor.gdx.windows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import core.level.info.LevelTile;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;

public class EditTileWindow extends Window {

    WadFile file;
    LevelTile tile;
    Image tileTexture;
    SelectBox<String> textureList;
    CheckBox solidCheckBox;
    Slider lightSlider;
    TextField effectField;
    TextField arg1Field;
    TextField arg2Field;
    CheckBox repeatCheckBox;
    TextField tagField;
    TextButton okButton;
    TextButton cancelButton;

    public EditTileWindow(String title, Skin skin, LevelTile tile, WadFile file) {
        super(title, skin);
        this.file = file;
        this.tile = tile;

        setModal(true);

        tileTexture = new Image(tile.graphic);
        add(tileTexture);
        textureList = new SelectBox<>(skin);
        textureList.setItems(getTextureList());
        textureList.setSelected(tile.graphicname);
        add(textureList);
        row();
        solidCheckBox = new CheckBox("Solid", skin);
        solidCheckBox.setChecked(tile.solid);
        add(solidCheckBox);
        row();
        add(new Label("Light:", skin));
        lightSlider = new Slider(0, 255, 1, false, skin);
        lightSlider.setValue(tile.light);
        add(lightSlider);
        row();
        add(new Label("Effect:", skin));
        effectField = new TextField("Effect:", skin);
        effectField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        effectField.setText(Integer.toString(tile.effect));
        add(effectField);
        row();
        add(new Label("Arg 1:", skin));
        arg1Field = new TextField("Arg 1:", skin);
        arg1Field.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        arg1Field.setText(Integer.toString(tile.arg1));
        add(arg1Field);
        row();
        add(new Label("Arg 2:", skin));
        arg2Field = new TextField("Arg 2:", skin);
        arg2Field.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        arg2Field.setText(Integer.toString(tile.arg2));
        add(arg2Field);
        row();
        repeatCheckBox = new CheckBox("Repeat", skin);
        repeatCheckBox.setChecked(tile.repeat);
        add(repeatCheckBox);
        row();
        add(new Label("Tag:", skin));
        tagField = new TextField("Tag:", skin);
        tagField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        tagField.setText(Integer.toString(tile.tag));
        add(tagField);
        row();
        okButton = new TextButton("OK", skin);

        okButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                changeTile();
            }
        });

        add(okButton);
        cancelButton = new TextButton("Cancel", skin);

        cancelButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                close();
            }
        });

        add(cancelButton);
        pack();


    }

    private String[] getTextureList() {

        int start = file.lastIndexOf("G_START") + 1;
        int end = file.lastIndexOf("G_END");

        String[] list = new String[end - start];

        for (int i = start; i < end; i++) {
            list[i-start] = file.getEntry(i).getName();
        }

        return list;
    }

    private void close() {
        remove();
    }

    private void changeTile() {
        tile.graphicname = textureList.getSelected();
        tile.graphic = WadFuncs.getTexture(file, tile.graphicname);
        tile.solid = solidCheckBox.isChecked();
        tile.light = (int) lightSlider.getValue();
        tile.effect = Integer.parseInt(effectField.getText());
        tile.arg1 = Integer.parseInt(arg1Field.getText());
        tile.arg2 = Integer.parseInt(arg2Field.getText());
        tile.repeat = repeatCheckBox.isChecked();
        tile.tag = Integer.parseInt(tagField.getText());

        close();
    }
}
