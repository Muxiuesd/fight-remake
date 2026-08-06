package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.math.Vector3;
import game.muxiuesd.bedrockcore.app.ui.components.UIButton;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.game.SpatialAudioSystem;
import ttk.muxiuesd.util.Util;

/**
 * 游戏用的默认按钮UI组件
 * */
public class FightUIButton extends UIButton {
    public FightUIButton (ClickEvent clickEvent) {
        this(clickEvent, VOID_MOUSE_OVER_EVENT);
    }

    public FightUIButton (ClickEvent clickEvent, MouseOverEvent mouseOverEvent) {
        super(
            Util.loadTextureRegion(
                Fight.ID("button"),
                Fight.UITexturePath("button.png")
            ),
            Util.loadTextureRegion(
                Fight.ID("button_clicked"),
                Fight.UITexturePath("button_clicked.png")
            ),
            Util.loadTextureRegion(
                Fight.ID("button_mouse_over"),
                Fight.UITexturePath("button_mouse_over.png")
            ),
            clickEvent,
            mouseOverEvent
        );
    }

    @Override
    public void playClickSound () {
        Vector3 pos = new Vector3(getX() + getWidth() / 2f, getY() + getHeight() / 2f, 0);
        SpatialAudioSystem.getInstance().playUIAudio(
            Sounds.ITEM_CLICK,
            () -> pos
        );
    }
}
