package ttk.muxiuesd.interfaces.world.loottable;

import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.math.Vec2;
import ttk.muxiuesd.world.World;

/**
 * 战利品表接口
 * */
public interface LootTable<C extends LootTable.Conditions<C>> {

    /**
     * 待实现方法：生成战利品
     * @param conditions 传入的条件，可根据条件调整战利品的生成
     * */
    void generate (World world, C conditions);


    /**
     * 条件类
     * */
    class Conditions<C extends LootTable.Conditions<C>> {
        private Vec2 pos;    //触发这个战利品表的世界坐标

        public Vec2 getPos () {
            return this.pos;
        }

        public C setPos (Vec2 pos) {
            this.pos = pos;
            return (C) this;
        }

        public C setPos (Vector2 pos) {
            this.pos = new Vec2(pos);
            return (C) this;
        }
    }
}
