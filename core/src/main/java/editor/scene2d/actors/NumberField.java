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
        try {
            return Integer.parseInt(getText());
        } catch(NumberFormatException nfe) {return 0;}
    }

    public Float getFloat() {
        try {
            return Float.parseFloat(getText());
        } catch(NumberFormatException nfe) {return 0f;}
    }

    private static class DigitsPlusNegativeFilter extends TextField.TextFieldFilter.DigitsOnlyFilter {

        @Override
        public boolean acceptChar(TextField field, char c) {

            return super.acceptChar(field, c) || c == '-' || c == '.';
        }
    }
}
