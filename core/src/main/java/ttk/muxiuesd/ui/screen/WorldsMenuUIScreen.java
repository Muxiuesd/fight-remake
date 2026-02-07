package ttk.muxiuesd.ui.screen;

import game.muxiuesd.bedrockcore.app.ui.abs.UIScreen;
import game.muxiuesd.bedrockcore.app.ui.components.UIButtonListItem;
import ttk.muxiuesd.ui.components.SavesListUI;

/**
 * 世界存档选择菜单UIScreen
 * <p>
 * 列出读取到的所有世界的存档数据，以列表的形式展现
 * */
public class WorldsMenuUIScreen extends UIScreen {
    //存档的UI列表面板
    private SavesListUI savesList;

    public WorldsMenuUIScreen() {
        this.savesList = new SavesListUI();
        this.savesList.setSize(150f, 230f);
        this.savesList.setPosition(
            - this.savesList.getWidth() / 2f,
            - this.savesList.getHeight() / 2f
        );

        this.savesList.addItem(new UIButtonListItem());
        this.savesList.addItem(new UIButtonListItem());
        this.savesList.addItem(new UIButtonListItem());
        this.savesList.addItem(new UIButtonListItem());
        this.savesList.addItem(new UIButtonListItem());
        this.savesList.addItem(new UIButtonListItem());
        this.savesList.addItem(new UIButtonListItem());
        this.savesList.addItem(new UIButtonListItem());
        this.savesList.addItem(new UIButtonListItem());


        //this.savesList.auto();

        addComponent(this.savesList);
    }

    public SavesListUI getSavesList () {
        return this.savesList;
    }

    public WorldsMenuUIScreen setSavesList (SavesListUI savesList) {
        this.savesList = savesList;
        return this;
    }
}
