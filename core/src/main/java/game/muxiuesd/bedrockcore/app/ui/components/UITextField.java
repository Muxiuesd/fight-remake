package game.muxiuesd.bedrockcore.app.ui.components;

import com.badlogic.gdx.math.GridPoint2;
import game.muxiuesd.bedrockcore.app.ui.abs.UIComponent;

/**
 * 文本框
 * */
public class UITextField extends UIComponent {

    @Override
    public boolean click (GridPoint2 interactPos) {
        getScreen().setFocusComponent(this);
        return false;
    }
}
