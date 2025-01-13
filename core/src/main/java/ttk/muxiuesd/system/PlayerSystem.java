package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Player;

/**
 * 玩家系统
 * */
public class PlayerSystem extends GameSystem{
    public final String TAG = this.getClass().getName();

    private Player player;

    public PlayerSystem(World world) {
        super(world);
        this.player = new Player(1000, 1000);
        Log.print(TAG, "PlayerSystem初始化完成！");
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void draw(Batch batch) {

    }

    @Override
    public void renderShape(ShapeRenderer batch) {

    }

    @Override
    public void dispose() {
    }

    public Player getPlayer() {
        return this.player;
    }
}
