package ttk.muxiuesd.ui.screen;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue;
import game.muxiuesd.bedrockcore.app.ui.abs.UIScreen;
import game.muxiuesd.bedrockcore.app.ui.components.UITextField;
import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.registry.Fonts;
import ttk.muxiuesd.registry.WorldInfoTypes;
import ttk.muxiuesd.ui.components.CreateNewWorldButtonUI;
import ttk.muxiuesd.ui.components.FightUITextField;
import ttk.muxiuesd.ui.components.SavesListUI;
import ttk.muxiuesd.ui.components.WorldSaveButtonUI;
import ttk.muxiuesd.ui.text.Text;
import ttk.muxiuesd.util.AbsFileUtil;
import ttk.muxiuesd.world.WorldInfo;

import java.util.Arrays;

/**
 * 世界存档选择菜单UIScreen
 * <p>
 * 列出读取到的所有世界的存档数据，以列表的形式展现
 * */
public class WorldsMenuUIScreen extends UIScreen {
    //存档的UI列表面板
    private SavesListUI savesList;
    private CreateNewWorldButtonUI createNewWorldButton;
    private UITextField worldNameTextField;

    public WorldsMenuUIScreen() {
        this.savesList = new SavesListUI();
        this.savesList.setSize(150f, 230f);
        this.savesList.setPosition(
            - this.savesList.getWidth() / 2f,
            - this.savesList.getHeight() / 2f
        );

        this.createNewWorldButton = new CreateNewWorldButtonUI(this);
        this.createNewWorldButton.setPosition(
            - this.createNewWorldButton.getWidth() / 2f,
            this.savesList.getY() - this.createNewWorldButton.getHeight()
        );

        this.worldNameTextField = new FightUITextField(
            this.savesList.getWidth(), this.createNewWorldButton.getHeight(), Fonts.MC
        );
        this.worldNameTextField.setPosition(
            - this.worldNameTextField.getWidth() / 2f,
            this.createNewWorldButton.getY() - this.worldNameTextField.getHeight()
        );

        addComponent(this.savesList);
        addComponent(this.createNewWorldButton);
        addComponent(this.worldNameTextField);
    }

    /**
     * 刷新存档列表
     * */
    public void flashSaveList () {
        //清理旧的
        this.getSavesList().clearItems();
        this.readSavesDir();
    }

    /**
     * 读取存档目录
     * */
    public void readSavesDir () {
        boolean saveDirIsExist = AbsFileUtil.dirExists(Fight.PATH_SAVE);
        if (!saveDirIsExist) {
            Log.error(this.getClass().getName(), "没有存档文件夹， 已自动创建！！！");
        }

        //读取存档文件夹下所有的存档文件目录
        FileHandle savesDirFileHandle = AbsFileUtil.getFileHandle(Fight.PATH_SAVE);
        FileHandle[] saveDirs = savesDirFileHandle.list();
        Arrays.stream(saveDirs).forEach((dir) -> {
            System.out.println(dir.name());
        });

        //读取目录中的世界信息
        for (FileHandle saveDir : saveDirs) {
            //如果不存在世界信息文件就跳过这个目录
            if (! AbsFileUtil.fileExists(saveDir, Fight.PATH_SAVE_WORLD + WorldInfo.FILE_NAME)) continue;
            //有世界信息文件就读取
            JsonValue worldInfoJsonFile = AbsFileUtil.readJsonFile(saveDir, Fight.PATH_SAVE_WORLD + WorldInfo.FILE_NAME);
            JsonDataReader jsonDataReader = new JsonDataReader(worldInfoJsonFile);
            JsonValue objValue = jsonDataReader.readObj(WorldInfoTypes.STRING.getId());
            String worldName = objValue.getString(Fight.WORLD_NAME.getKey());

            //添加存档按钮UI
            WorldSaveButtonUI worldButton = new WorldSaveButtonUI(worldName);
            worldButton.setText(new Text().add(worldName).build());
            this.getSavesList().addItem(worldButton);
        }
    }

    public SavesListUI getSavesList () {
        return this.savesList;
    }

    public WorldsMenuUIScreen setSavesList (SavesListUI savesList) {
        this.savesList = savesList;
        return this;
    }
}
