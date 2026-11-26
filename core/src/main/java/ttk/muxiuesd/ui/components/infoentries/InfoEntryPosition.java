package ttk.muxiuesd.ui.components.infoentries;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.FightCore;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.components.InfoEntry;
import ttk.muxiuesd.ui.text.Text;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.player.Player;

/**
 * 显示玩家当前坐标与所在高度
 * <p>
 * 实时刷新，格式："x:{x}, y:{y}, 高度：{height} m"。
 */
public class InfoEntryPosition extends InfoEntry {
    public InfoEntryPosition () {
        super(Text.ofText(Fight.ID("info_entry_position")));
    }

    @Override
    public void update (float delta) {
        World world = FightCore.getInstance().getWorld();
        if (world == null) return;

        PlayerSystem ps = world.getSystem(PlayerSystem.class);
        if (ps == null) return;
        Player player = ps.getPlayer();
        if (player == null) return;

        float x = player.getX();
        float y = player.getY();

        // 当前坐标对应的高度（地形高度值，m）
        ChunkSystem cs = world.getSystem(ChunkSystem.class);
        int height = cs != null ? cs.landHeight(x, y) : 0;

        //坐标默认显示到小数点后四位
        getText().set(0, String.format("%.4f", x)).set(1, String.format("%.4f", y)).set(2, height);
    }
}