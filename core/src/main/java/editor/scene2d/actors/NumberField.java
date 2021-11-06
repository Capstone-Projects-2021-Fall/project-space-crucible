package editor.scene2d.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class NumberField extends TextField {

    final private static TextFieldFilter numberFilter = new DigitsPlusNegativeFilter();

    public NumberField(String text, Skin skin) {
        super(text, skin);
        setTextFieldFilter(numberFilter);
    }

    public Integer getInteger() {
        return Integer.parseInt(getText());
    }

    private static class DigitsPlusNegativeFilter extends TextField.TextFieldFilter.DigitsOnlyFilter {

        @Override
        public boolean acceptChar(TextField field, char c) {

            return super.acceptChar(field, c) || c == '-' || c == '.';
        }
    }
}
