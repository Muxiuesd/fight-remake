package ttk.muxiuesd.mod.api;

/**
 * mod专用：获取注册器
 * */
public class ModRegistrant {
    public static ModBlockRegister getBlockRegister(String namespace) {
        return new ModBlockRegister(namespace);
    }

    /*public static Registrant<Entity> getEntityRegister(String namespace) {
        return RegistrantGroup.getRegistrant(namespace, Entity.class);
    }*/
}
