package ttk.muxiuesd.render.world.entity;

import ttk.muxiuesd.world.entity.abs.Enemy;

/**
 * 敌对实体的渲染器
 * */
public class EnemyRenderer<T extends Enemy<?>> extends LivingEntityRenderer<T> {
    /**
     * @param textureId 身体贴图资源的id（一般与实体id相同）
     * @param texturePath 身体贴图文件在 texture/entity 目录下的路径
     * */
    public EnemyRenderer (String textureId, String texturePath) {
        super(textureId, texturePath);
    }
}
