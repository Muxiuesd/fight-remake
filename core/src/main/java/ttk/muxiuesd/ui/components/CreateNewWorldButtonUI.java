package ttk.muxiuesd.ui.components;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.ui.screen.WorldsMenuUIScreen;
import ttk.muxiuesd.ui.text.Text;

/**
 * 新建世界的按钮UI组件
 * */
public class CreateNewWorldButtonUI extends FightUITextButton {
    private WorldsMenuUIScreen worldsMenuUIScreen;

    public CreateNewWorldButtonUI (WorldsMenuUIScreen worldsMenuUIScreen, ClickEvent clickEvent) {
        super(Text.ofText(Fight.ID("button_create_new_world")), clickEvent);
        this.worldsMenuUIScreen = worldsMenuUIScreen;
    }
}
