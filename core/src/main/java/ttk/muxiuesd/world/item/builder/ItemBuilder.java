package ttk.muxiuesd.world.item.builder;

import ttk.muxiuesd.world.item.abs.Item;

import java.util.function.Supplier;

/**
 * 物品构建器统一接口
 * <p>
 * 所有品类物品的 builder 都实现本接口，统一 {@code build()} 的返回形态，
 * 由注册层直接消费，避免不同品类各自为政的构建方式。
 * <p>
 * 继承 {@link Supplier} 使 builder 对象可直接传入 {@code register(String, Supplier<T>)} 等重载，
 * 无需额外的 ItemBuilder 专用重载，避免与 Supplier 的重载歧义。
 *
 * @param <T> 构建出的物品类型
 */
public interface ItemBuilder<T extends Item> extends Supplier<T> {
    /**
     * 用默认属性构建一个全新的物品实例
     */
    T build ();

    @Override
    default T get () {
        return build();
    }
}