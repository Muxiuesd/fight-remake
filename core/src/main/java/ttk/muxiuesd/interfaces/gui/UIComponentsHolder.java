package ttk.muxiuesd.interfaces.gui;

import ttk.muxiuesd.ui.abs.UIComponent;

import java.util.LinkedHashSet;

/**
 * UI组件持有者接口
 * */
public interface UIComponentsHolder {
    LinkedHashSet<UIComponent> uiComponents = new LinkedHashSet<>();

    default void addComponent (UIComponent component) {
        this.getComponents().add(component);
    }

    default void removeComponent (UIComponent component) {
        this.getComponents().remove(component);
    }

    default LinkedHashSet<UIComponent> getComponents () {
        return uiComponents;
    }

    default void setComponents (LinkedHashSet<UIComponent> components) {
        uiComponents.clear();
        uiComponents.addAll(components);
    }
}
