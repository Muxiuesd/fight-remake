package ttk.muxiuesd.world.hitbox;

import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.app.interfaces.Voidable;

/**
 * 空的Hitbox
 * */
public class VoidHitbox extends Hitbox implements Voidable {
    public static final Vector2 POS = new Vector2(0, 0);

    @Override
    public boolean checkCollision (Hitbox otherHitbox) {
        return false;
    }

    @Override
    public float getSizeRadius () {
        return 0;
    }

    @Override
    public Hitbox setCenterPos (float cx, float cy) {
        return this;
    }

    @Override
    public Vector2 getCenterPos () {
        return POS;
    }
}
