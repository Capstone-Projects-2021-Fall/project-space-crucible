package editor.scene2d.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class MIDINameField extends TextField {

    final private static TextFieldFilter lumpFilter = new LumpFilter();

    public MIDINameField(String text, Skin skin) {
        super(text, skin);
        setTextFieldFilter(lumpFilter);
    }

    private static class LumpFilter extends TextField.TextFieldFilter.DigitsOnlyFilter {

        @Override
        public boolean acceptChar(TextField field, char c) {

            return field.getText().length() + 1 <= 8;
        }
    }
}
