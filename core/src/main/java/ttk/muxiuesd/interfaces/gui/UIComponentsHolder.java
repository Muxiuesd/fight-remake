package ttk.muxiuesd.interfaces.gui;

import ttk.muxiuesd.ui.abs.UIComponent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;

/**
 * UI组件持有者接口
 * */
public interface UIComponentsHolder {
    Comparator<UIComponent> ByZIndex = Comparator.comparingInt(UIComponent::getZIndex);

    default void addComponent (UIComponent component) {
        this.getComponents().add(component);
    }

    default void removeComponent (UIComponent component) {
        this.getComponents().remove(component);
    }

    default boolean hasComponent (UIComponent component) {
        return this.getComponents().contains(component);
    }

    default void setComponents (LinkedHashSet<UIComponent> components) {
        this.getComponents().clear();
        this.getComponents().addAll(components);
    }

    /**
     * 对组件进行排序
     * */
    default void sortComponents () {
        ArrayList<UIComponent> list = new ArrayList<>(this.getComponents());
        list.sort(ByZIndex);
        setComponents(new LinkedHashSet<>(list));
    }

    /**
     * 获取组件集合，需要主动实现
     * */
    LinkedHashSet<UIComponent> getComponents ();
}
