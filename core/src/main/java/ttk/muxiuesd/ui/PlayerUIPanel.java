package ttk.muxiuesd.ui;

import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.ui.abs.UIPanel;

/**
 * 玩家的面板
 * */
public class PlayerUIPanel extends UIPanel {
    public PlayerUIPanel () {
        addComponent(new Button(0, 0,10, 10, new GridPoint2(10, 10)));
    }
}
