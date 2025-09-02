package ttk.muxiuesd.interfaces.gui;

import ttk.muxiuesd.ui.abs.UIComponent;

import java.util.LinkedHashSet;

/**
 * UI组件持有者接口
 * */
public interface UIComponentsHolder {

    default void addComponent (UIComponent component) {
        this.getComponents().add(component);
    }

    default void removeComponent (UIComponent component) {
        this.getComponents().remove(component);
    }

    default void setComponents (LinkedHashSet<UIComponent> components) {
        this.getComponents().clear();
        this.getComponents().addAll(components);
    }

    /**
     * 获取组件集合，需要主动实现
     * */
    LinkedHashSet<UIComponent> getComponents ();
}
