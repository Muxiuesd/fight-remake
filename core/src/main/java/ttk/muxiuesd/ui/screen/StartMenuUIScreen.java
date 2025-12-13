package ttk.muxiuesd.ui.screen;

import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.FightGameMain;
import ttk.muxiuesd.ui.abs.UIScreen;
import ttk.muxiuesd.ui.components.UIButton;
import ttk.muxiuesd.ui.components.UIPanel;

/**
 * 主菜单的UIScreen
 * */
public class StartMenuUIScreen extends UIScreen {

    private UIPanel buttonsPanel;

    public StartMenuUIScreen () {
        this.buttonsPanel = new UIPanel(
            - (UIButton.DEFAULT_WIDTH/ 2), 0,
            UIButton.DEFAULT_WIDTH, 100,
            new GridPoint2((int) UIButton.DEFAULT_WIDTH, 100)
        );

        this.buttonsPanel.addComponent(new UIButton((button, interactPos) -> {
            FightGameMain.getInstance().changeScreen(FightGameMain.getInstance().mainGameScreen);
            return false;
        }));
        addComponent(buttonsPanel);
    }
}
