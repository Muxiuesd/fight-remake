package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.render.RenderLayer;

import java.util.function.Supplier;

/**
 * 所有的渲染层级
 * */
public final class RenderLayers {
    public static void init () {
    }

    public static final RenderLayer ENTITY_GROUND = register("entity_ground", RenderLayer::new);
    public static final RenderLayer ENTITY_UNDERGROUND = register("entity_underground", RenderLayer::new);


    public static RenderLayer register(String name, Supplier<RenderLayer> supplier) {
        String id = Fight.getId(name);
        return Registries.RENDER_LAYER.register(new Identifier(id), supplier.get().setId(id));
    }
}
