package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.math.Vector3;
import game.muxiuesd.bedrockcore.app.ui.components.UITextButton;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.game.SpatialAudioSystem;
import ttk.muxiuesd.ui.text.Text;
import ttk.muxiuesd.util.Util;

/**
 * 游戏用的默认带文本的按钮UI组件（使用游戏默认的按钮贴图）
 * */
public class FightUITextButton extends UITextButton {
    public FightUITextButton() {
        this(Text.NULL_TEXT, VOID_CLICK_EVENT);
    }
    /**
     * @param text 要显示的文本组件
     * @param clickEvent 点击事件
     * */
    public FightUITextButton (Text text, ClickEvent clickEvent) {
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
            text, clickEvent, VOID_MOUSE_OVER_EVENT);
    }

    @Override
    public void playClickSound () {
        SpatialAudioSystem.getInstance().playUIAudio(
            Sounds.ITEM_CLICK,
            () -> new Vector3(getX() + getWidth() / 2f, getY() + getHeight() / 2f, 0)
        );
    }
}
