package ttk.muxiuesd.resource;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * 点九贴图的资源包装类
 * */
public class NinePatchResource {
    private Resource<TextureRegion> resource;
    private NinePatch ninePatch;

    public NinePatchResource(Resource<TextureRegion> resource, int left, int right, int top, int bottom) {
        this.resource = resource;
        if (resource.get() != null) this.ninePatch = new NinePatch(resource.get(), left, right, top, bottom);
    }

    public Resource<TextureRegion> getResource () {
        return this.resource;
    }

    public NinePatch getNinePatch () {
        return this.ninePatch;
    }
}
