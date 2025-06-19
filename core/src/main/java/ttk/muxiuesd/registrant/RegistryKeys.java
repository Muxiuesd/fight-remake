package ttk.muxiuesd.registrant;

import ttk.muxiuesd.interfaces.IItemStackBehaviour;
import ttk.muxiuesd.property.PropertyType;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.item.abs.Item;

import java.util.function.Supplier;

/**
 * 注册键
 * */
public class RegistryKeys {
    public static final RegistryKey<Item> ITEM = new RegistryKey<>();
    public static final RegistryKey<Block> BLOCK = new RegistryKey<>();
    public static final RegistryKey<Supplier<Entity>> ENTITY = new RegistryKey<>();
    public static final RegistryKey<PropertyType<?>> PROPERTY_TYPE = new RegistryKey<>();

    public static final RegistryKey<IItemStackBehaviour> ITEM_STACK_BEHAVIOUR = new RegistryKey<>();
}
