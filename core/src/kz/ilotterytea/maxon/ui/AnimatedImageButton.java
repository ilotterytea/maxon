package kz.ilotterytea.maxon.ui;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class AnimatedImageButton extends ImageButton {
    public AnimatedImageButton(AnimatedImage image) {
        super(image.getDrawable());
        ImageButtonStyle style = new ImageButtonStyle();

        style.up = image.getDrawable();
        super.setStyle(style);
    }

    public void setDrawable(Drawable drawable) {
        ImageButtonStyle style = new ImageButtonStyle();

        style.up = drawable;
        super.setStyle(style);
    }

    @Override public void act(float delta) {
    }
}
