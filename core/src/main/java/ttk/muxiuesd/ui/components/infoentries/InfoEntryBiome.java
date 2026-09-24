package ttk.muxiuesd.ui.components.infoentries;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.FightCore;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.components.InfoEntry;
import ttk.muxiuesd.ui.text.Text;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.biome.Biome;
import ttk.muxiuesd.world.entity.player.Player;

/**
 * 显示玩家当前所在坐标的区块的群系
 * <p>
 * 实时刷新，格式："当前群系：{群系id}"。
 */
public class InfoEntryBiome extends InfoEntry {
    public InfoEntryBiome () {
        super(Text.ofText(Fight.ID("info_entry_biome")));
    }

    @Override
    public void update (float delta) {
        World world = FightCore.getInstance().getWorld();
        if (world == null) return;

        PlayerSystem ps = world.getSystem(PlayerSystem.class);
        if (ps == null) return;
        Player player = ps.getPlayer();
        if (player == null) return;

        ChunkSystem cs = world.getSystem(ChunkSystem.class);
        if (cs == null) return;

        // 玩家当前坐标所属区块的群系
        Biome biome = cs.getBiomeAt(player.getX(), player.getY());
        String biomeId = biome != null ? biome.getId().getID() : "unknown";

        getText().set(0, biomeId);
    }
}