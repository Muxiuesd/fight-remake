package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.world.World;

import java.util.HashMap;
import java.util.LinkedHashMap;


/**
 * 世界的系统
 * */
public class WorldSystemsManager extends GameSystem{

    private final HashMap<String, GameSystem> systems;

    public WorldSystemsManager(World world) {
        super(world);
        this.systems = new LinkedHashMap<>();
    }

    public WorldSystemsManager addSystem(String name, GameSystem system) {
        if (!this.systems.containsKey(name)) {
            this.systems.put(name, system);
            return this;
        }
        //TODO
        return this;
    }


    public GameSystem getSystem(String name) {
        if (systems.containsKey(name)) {
            return systems.get(name);
        }
        return null;
    }

    @Override
    public void draw(Batch batch) {
        this.systems.values().forEach(system -> system.draw(batch));
    }

    @Override
    public void renderShape(ShapeRenderer batch) {
        this.systems.values().forEach(system -> system.renderShape(batch));
    }

    @Override
    public void update(float delta) {
        this.systems.values().forEach(system -> system.update(delta));
    }

    @Override
    public void dispose() {
        this.systems.values().forEach(Disposable::dispose);
    }
}
