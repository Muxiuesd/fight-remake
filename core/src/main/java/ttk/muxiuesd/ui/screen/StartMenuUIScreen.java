package ttk.muxiuesd.ui.screen;

import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.FightCore;
import ttk.muxiuesd.ui.abs.UIScreen;
import ttk.muxiuesd.ui.components.UIButton;
import ttk.muxiuesd.ui.components.UIPanel;
import ttk.muxiuesd.ui.components.UITextButton;
import ttk.muxiuesd.ui.text.Text;

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
        addComponent(this.buttonsPanel);

        this.buttonsPanel.addComponent(new UITextButton(
            Text.ofText(Fight.ID("button_start_game")),
            (button, interactPos) -> {
                FightCore.getInstance().changeScreen(FightCore.getInstance().mainGameScreen);
                return false;
            }).setPosition(0, 10)
        );
        this.buttonsPanel.addComponent(new UITextButton(
            Text.ofText(Fight.ID("button_game_settings")),
            (button, interactPos) -> {
                return false;
            }).setPosition(0, -10)
        );
    }

    @Override
    public void hide () {
        this.getComponents().forEach(uiComponent -> {
            uiComponent.setMouseOver(false);
            uiComponent.setClicked(false);
        });
    }
}
