package ttk.muxiuesd.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.util.Util;

public class Button extends UIComponent {
    private TextureRegion textureRegion;
    public Button (float x, float y, float width, float height) {
        super(x, y, width, height);
        this.textureRegion = Util.loadTextureRegion(
            Fight.getId("button"),
            Fight.UITexturePath("button.png")
        );
    }

    @Override
    public void draw (Batch batch) {
        if (isVisible()) {
            batch.draw(this.textureRegion, getX(), getY(), getWidth(), getHeight());
        }
    }
}
