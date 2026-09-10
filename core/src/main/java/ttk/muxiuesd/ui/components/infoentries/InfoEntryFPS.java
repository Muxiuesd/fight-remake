package ttk.muxiuesd.ui.components.infoentries;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.ui.components.InfoEntry;
import ttk.muxiuesd.ui.text.Text;

/**
 * 显示FPS信息
 * <p>
 * 每帧累计帧数与耗时，每秒结算一次，显示"上一秒的平均帧率"（而非瞬时 FPS）。
 * */
public class InfoEntryFPS extends InfoEntry {
    /// FPS 累计（每秒结算一次，显示上一秒平均帧率）
    private int frameCount = 0;      //本秒已累计的帧数
    private float elapsedTime = 0f;  //本秒已累计的时长
    private int lastSecondAvgFps = 0;//上一秒的平均帧率

    public InfoEntryFPS () {
        super(Text.ofText(Fight.ID("info_entry_fps")));
    }

    @Override
    public void update (float delta) {
        //FPS 累计：每帧累计帧数与时长，每秒结算一次
        this.frameCount++;
        this.elapsedTime += delta;
        if (this.elapsedTime >= 1f) {
            this.lastSecondAvgFps = (int) (this.frameCount / this.elapsedTime);
            this.frameCount = 0;
            this.elapsedTime = 0f;
        }

        getText().set(0, this.getLastSecondAvgFps());
    }

    public int getLastSecondAvgFps () {
        return this.lastSecondAvgFps;
    }
}
