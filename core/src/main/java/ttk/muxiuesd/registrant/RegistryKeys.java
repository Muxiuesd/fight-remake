package ttk.muxiuesd.registrant;

import ttk.muxiuesd.interfaces.RegistryKey;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.item.abs.Item;

import java.util.function.Supplier;

/**
 * 注册键
 * */
public class RegistryKeys {
    public static final RegistryKey<Supplier<Item>> ITEM = new RegistryKey<>();
    public static final RegistryKey<Supplier<Block>> BLOCK = new RegistryKey<>();
}
