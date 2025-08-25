package ttk.muxiuesd.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.util.Util;

public class Button extends UIComponent {
    private TextureRegion textureRegion;
    public Button (float x, float y, float width, float height, GridPoint2 interactPos) {
        super(x, y, width, height, interactPos);
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

    @Override
    public void mouseOver (GridPoint2 interactPos) {
        System.out.println(interactPos.toString());
    }

    @Override
    public boolean click (GridPoint2 interactPos) {
        System.out.println("点击" + interactPos.toString());
        return super.click(interactPos);
    }
}
