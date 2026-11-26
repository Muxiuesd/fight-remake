package ttk.muxiuesd.ui.components.infoentries;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.ui.components.InfoEntry;
import ttk.muxiuesd.ui.text.Text;

/**
 * 显示内存使用信息
 * <p>
 * 每帧实时刷新，显示当前游戏程序已使用的内存与 JVM 分配的内存（单位 MB）。
 */
public class InfoEntryMemory extends InfoEntry {
    private long lastUsedMB = 0;     //已使用的内存（MB）
    private long lastTotalMB = 0;    //JVM 分配的内存（MB）

    public InfoEntryMemory () {
        super(Text.ofText(Fight.ID("info_entry_memory")));
    }

    @Override
    public void update (float delta) {
        //实时刷新内存值（Runtime.getRuntime() 为普通 Java 调用，开销可忽略）
        Runtime rt = Runtime.getRuntime();
        //已使用的内存 = 已分配 - 空闲；分配的内存 = JVM 当前已向系统申请
        this.lastUsedMB  = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        this.lastTotalMB =  rt.totalMemory() / (1024 * 1024);

        getText().set(0, this.getLastUsedMB()).set(1, this.getLastTotalMB());
    }

    public long getLastUsedMB () {
        return lastUsedMB;
    }

    public long getLastTotalMB () {
        return lastTotalMB;
    }
}