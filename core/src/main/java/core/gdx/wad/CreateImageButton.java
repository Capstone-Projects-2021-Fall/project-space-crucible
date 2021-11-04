package core.gdx.wad;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class CreateImageButton {

    String overImage;
    String upImage;
    Button button;

    public CreateImageButton(String overImage, String upImage) {
        this.overImage = overImage;
        this.upImage = upImage;
        ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
        buttonStyle.over = new TextureRegionDrawable(new TextureRegion( new Texture(overImage)));
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion( new Texture(upImage)));
        button = new ImageButton(buttonStyle);
    }

}
