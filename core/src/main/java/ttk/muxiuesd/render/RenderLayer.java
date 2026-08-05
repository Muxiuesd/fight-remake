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
        //总是创建新实例，防止修改共享的注册表 key（Identifier 的 hashCode 基于 id）
        this.identifier = new Identifier(id);
        return this;
    }
}
