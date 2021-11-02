package editor.windows.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class NumberField extends TextField {

    final private static TextFieldFilter numberFilter = new DigitsPlusNegativeFilter();

    public NumberField(String text, Skin skin) {
        super(text, skin);
        setTextFieldFilter(numberFilter);
    }
}
