package ttk.muxiuesd.ui.components;

import game.muxiuesd.bedrockcore.app.interfaces.Updateable;
import ttk.muxiuesd.ui.text.Text;

/**
 * 信息面板的信息条目
 * <p>
 * 每一信息占用一行，持有标签与值的提供者。
 * */
public class InfoEntry implements Updateable {
    private Text text;

    public InfoEntry (Text text) {
        this.text = text;
    }

    @Override
    public void update (float delta) {

    }

    /**
     *
     * */
    public String getTextString () {
        return this.getText().getString();
    }

    public Text getText () {
        return this.text;
    }

    public InfoEntry setText (Text text) {
        this.text = text;
        return this;
    }
}
