package ttk.muxiuesd.render;

/**
 * 渲染层级
 * */
public class RenderLayer {
    private String id;

    public RenderLayer () {}
    public RenderLayer(String id) {
        this.id = id;
    }

    public String getId () {
        return this.id;
    }

    public RenderLayer setId (String id) {
        this.id = id;
        return this;
    }
}
