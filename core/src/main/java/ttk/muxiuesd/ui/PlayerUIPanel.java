package ttk.muxiuesd.ui;

import ttk.muxiuesd.ui.abs.UIPanel;

/**
 * 玩家的面板
 * */
public class PlayerUIPanel extends UIPanel {
    public PlayerUIPanel () {
        addComponent(new Button(0, 0,100, 100));
    }
}
