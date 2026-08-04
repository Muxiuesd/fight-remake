package ttk.muxiuesd.render;

import ttk.muxiuesd.id.Identifier;

/**
 * 渲染层级
 * */
public class RenderLayer {
    private Identifier identifier;

    public RenderLayer () {}
    public RenderLayer(Identifier identifier) {
        this.identifier = identifier;
    }

    public String getId () {
        return this.getIdentifier() == null ? null : this.getIdentifier().getID();
    }

    public Identifier getIdentifier () {
        return this.identifier;
    }

    public RenderLayer setIdentifier (Identifier identifier) {
        this.identifier = identifier;
        return this;
    }

    public RenderLayer setId (String id) {
        if (this.identifier == null) {
            this.identifier = new Identifier(id);
        } else {
            this.identifier.setID(id);
        }
        return this;
    }
}
