package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.audio.AudioManager;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.Block;
import ttk.muxiuesd.world.entity.Player;

/**
 * 游戏内的音效系统* */
public class SoundEffectSystem extends WorldSystem {
    private ChunkSystem cs;
    private PlayerSystem ps;

    public float walkSoundEffectPlaySpan = 0.0f;
    public float walkSoundEffectPlayCycle = 0.65f;

    public SoundEffectSystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {
        this.cs = (ChunkSystem) getManager().getSystem("ChunkSystem");
        this.ps = (PlayerSystem) getManager().getSystem("PlayerSystem");
    }

    @Override
    public void update (float delta) {
        //玩家走路音效
        if (walkSoundEffectPlaySpan == walkSoundEffectPlayCycle && this.ps.playerMoved()) {
            Player player = this.ps.getPlayer();
            Vector2 playerCenter = player.getPlayerCenter();
            Block underfootBlock = cs.getBlock(playerCenter.x, playerCenter.y);
            String walkSoundId = underfootBlock.getProperty().getWalkSoundId();
            AudioManager.getInstance().playSound(walkSoundId, 1, 2f, 0);
            walkSoundEffectPlaySpan = 0.0f;
        }else {
            walkSoundEffectPlaySpan += delta;
            if (walkSoundEffectPlaySpan > walkSoundEffectPlayCycle) {
                walkSoundEffectPlaySpan = walkSoundEffectPlayCycle;
            }
        }
    }

    @Override
    public void draw (Batch batch) {

    }

    @Override
    public void renderShape (ShapeRenderer batch) {

    }

    @Override
    public void dispose () {

    }
}
