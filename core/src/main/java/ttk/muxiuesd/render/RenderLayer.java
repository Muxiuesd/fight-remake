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
        //Identifier 只在注册阶段给定，注册过后不允许修改
        if (this.identifier != null && !this.identifier.equals(identifier)) {
            throw new IllegalStateException("Identifier 已设置，禁止修改！渲染层级：" + this.identifier.getID() + " -> " + identifier.getID());
        }
        this.identifier = identifier;
        return this;
    }
}
