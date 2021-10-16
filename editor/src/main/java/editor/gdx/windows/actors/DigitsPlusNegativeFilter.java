package editor.gdx.windows.actors;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class DigitsPlusNegativeFilter extends TextField.TextFieldFilter.DigitsOnlyFilter {

    @Override
    public boolean acceptChar(TextField field, char c) {

        return super.acceptChar(field, c) || c == '-';
    }
}
