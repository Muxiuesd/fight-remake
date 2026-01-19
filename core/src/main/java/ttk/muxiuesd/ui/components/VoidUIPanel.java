package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.interfaces.util.Voidable;

/**
 * 空的UI面板
 * */
public class VoidUIPanel extends UIPanel implements Voidable {
    public VoidUIPanel() {
        super(0, 0, 0, 0, new GridPoint2());
    }
}
