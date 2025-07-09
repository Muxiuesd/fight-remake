package ttk.muxiuesd.system.abs;

import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.world.World;

/**
 * 实体渲染抽象类
 * */
public abstract class EntityRenderSystem extends WorldSystem{
    private EntitySystem curEntitySystem;

    public EntityRenderSystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {
        this.curEntitySystem = (EntitySystem) getManager().getSystem("EntitySystem");
    }

    public EntitySystem getCurEntitySystem () {
        return this.curEntitySystem;
    }

    public EntityRenderSystem setCurEntitySystem (EntitySystem curEntitySystem) {
        this.curEntitySystem = curEntitySystem;
        return this;
    }
}
