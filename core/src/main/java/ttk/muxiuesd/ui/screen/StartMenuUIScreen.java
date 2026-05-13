package ttk.muxiuesd.ui.screen;

import com.badlogic.gdx.Gdx;
import game.muxiuesd.bedrockcore.app.ui.abs.UIScreen;
import game.muxiuesd.bedrockcore.app.ui.components.UIButton;
import game.muxiuesd.bedrockcore.app.ui.components.UIPanel;
import game.muxiuesd.bedrockcore.app.ui.components.UITextButton;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.FightCore;
import ttk.muxiuesd.ui.components.FightUITextButton;
import ttk.muxiuesd.ui.text.Text;

/**
 * 主菜单的UIScreen
 * */
public class StartMenuUIScreen extends UIScreen {

    private UIPanel buttonsPanel;
    private UITextButton startButton;
    private UITextButton settingsButton;
    private UITextButton exitButton;


    public StartMenuUIScreen () {
        this.buttonsPanel = new UIPanel(-(UIButton.DEFAULT_WIDTH / 2), 0);
        addComponent(this.buttonsPanel);

        //开始游戏按钮
        this.startButton = new FightUITextButton(
            Text.ofText(Fight.ID("button_start_game")),
            (button, interactPos) -> {
                FightCore.getInstance().setScreen(FightCore.getInstance().worldsMenuScreen);
                return false;
            }
        );
        this.startButton.setPosition(0, UIButton.DEFAULT_HEIGHT * 2 + 20);

        //游戏设置按钮
        this.settingsButton = new FightUITextButton(
            Text.ofText(Fight.ID("button_game_settings")),
            (button, interactPos) -> {
                return false;
            }
        );
        this.settingsButton.setPosition(0, UIButton.DEFAULT_HEIGHT + 10);

        //退出游戏的按钮
        this.exitButton = new FightUITextButton(
            Text.ofText(Fight.ID("button_exit_game")),
            (button, interactPos) -> {
                Gdx.app.exit();
                return false;
            }
        );
        this.exitButton.setPosition(0, 0);

        this.buttonsPanel.addComponent(this.startButton);
        this.buttonsPanel.addComponent(this.settingsButton);
        this.buttonsPanel.addComponent(this.exitButton);
        this.buttonsPanel.auto();
    }

    @Override
    public void hide () {
        this.getComponents().forEach(uiComponent -> {
            uiComponent.setMouseOver(false);
            uiComponent.setClicked(false);
        });
    }
}
