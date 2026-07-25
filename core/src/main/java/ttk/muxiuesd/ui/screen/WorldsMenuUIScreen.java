package ttk.muxiuesd.ui.screen;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue;
import game.muxiuesd.bedrockcore.app.ui.abs.UIScreen;
import game.muxiuesd.bedrockcore.app.ui.components.UIButton;
import game.muxiuesd.bedrockcore.app.ui.components.UITextField;
import game.muxiuesd.bedrockcore.util.Log;
import game.muxiuesd.bedrockcore.util.UnifiedFileUtil;
import ttk.muxiuesd.Fight;
import game.muxiuesd.bedrockcore.data.JsonDataReader;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;
import ttk.muxiuesd.registry.Fonts;
import ttk.muxiuesd.registry.WorldInfoTypes;
import ttk.muxiuesd.ui.components.CreateNewWorldButtonUI;
import ttk.muxiuesd.ui.components.FightUITextField;
import ttk.muxiuesd.ui.components.SavesListUI;
import ttk.muxiuesd.ui.components.WorldSaveButtonUI;
import ttk.muxiuesd.ui.text.Text;
import ttk.muxiuesd.util.AbsFileUtil;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.WorldInfo;

/**
 * 世界存档选择菜单UIScreen
 * <p>
 * 列出读取到的所有世界的存档数据，以列表的形式展现
 * */
public class WorldsMenuUIScreen extends UIScreen {
    public static final String TAG = WorldsMenuUIScreen.class.getName();
    //存档的UI列表面板
    private SavesListUI savesList;
    private CreateNewWorldButtonUI createNewWorldButton;
    private UITextField worldNameTextField;
    private UITextField worldSeedTextField;

    private final UIButton.ClickEvent CreateNewWorldButtonClickEvent = (button, interactPos) -> {
        //获取世界名称、种子
        String worldName = this.worldNameTextField.getTextStringBuilder().toString();
        String worldSeed = this.worldSeedTextField.getTextStringBuilder().toString();
        //两者都不得为空或者空白字符
        if (worldName.isEmpty() || worldSeed.isEmpty()
            || worldName.isBlank() || worldSeed.isBlank()) return false;
        //编写基础的世界json数据
        JsonDataWriter worldJsonDataWriter = new JsonDataWriter();
        worldJsonDataWriter
            .objStart()
                .objStart(WorldInfoTypes.STRING.getId())
                    .writeString(Fight.WORLD_NAME.getKey(), worldName)
                .objEnd()
                .objStart(WorldInfoTypes.LONG.getId())
                    .writeLong(Fight.WORLD_SEED.getKey(), Util.stringToLongHash(worldSeed))
                .objEnd()
            .objEnd();
        //写入文件
        String worldDirPath = Fight.PATH_SAVE + worldName + "/" + Fight.PATH_SAVE_WORLD;
        UnifiedFileUtil.createDir(worldDirPath);
        UnifiedFileUtil.createFile(worldDirPath, WorldInfo.FILE_NAME)
            .writeString(worldJsonDataWriter.getResult(),false);
        //刷新列表
        this.flashSaveList();

        return false;
    };

    public WorldsMenuUIScreen() {
        this.savesList = new SavesListUI();
        this.savesList.setSize(150f, 190f);
        this.savesList.setPosition(
            - this.savesList.getWidth() / 2f,
            - this.savesList.getHeight() / 2f
        );

        this.createNewWorldButton = new CreateNewWorldButtonUI(this, CreateNewWorldButtonClickEvent);
        this.createNewWorldButton.setPosition(
            - this.createNewWorldButton.getWidth() / 2f,
            this.savesList.getY() - this.createNewWorldButton.getHeight()
        );

        this.worldNameTextField = new FightUITextField(
            this.savesList.getWidth(), this.createNewWorldButton.getHeight(), Fonts.MC
        );
        this.worldNameTextField
            .setTipText("请输入世界名称（50字符以内）")
            .setMaxLength(50)
            .setPosition(
                - this.worldNameTextField.getWidth() / 2f,
                this.createNewWorldButton.getY() - this.worldNameTextField.getHeight()
            );

        this.worldSeedTextField = new FightUITextField(
            this.savesList.getWidth(), this.createNewWorldButton.getHeight(), Fonts.MC
        );
        this.worldSeedTextField
            .setTipText("请输入世界种子（50字符以内）")
            .setMaxLength(50)
            .setPosition(
                - this.worldSeedTextField.getWidth() / 2f,
                this.worldNameTextField.getY() - this.worldSeedTextField.getHeight()
            );

        addComponent(this.savesList);
        addComponent(this.createNewWorldButton);
        addComponent(this.worldNameTextField);
        addComponent(this.worldSeedTextField);
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
        Log.print(TAG, "探查到存档目录：");

        for (int i = 0; i < saveDirs.length; i++) {
            FileHandle dir = saveDirs[i];
            System.out.print(dir.name());
            if (i == saveDirs.length - 1) {
                System.out.println();
            }else {
                System.out.print(" | ");
            }
        }


        //读取目录中的世界信息
        for (FileHandle saveDir : saveDirs) {
            //如果不存在世界信息文件就跳过这个目录
            if (! AbsFileUtil.fileExists(saveDir, Fight.PATH_SAVE_WORLD + WorldInfo.FILE_NAME)) continue;

            //有世界信息文件就读取
            JsonValue worldInfoJsonFile = AbsFileUtil.readJsonFile(saveDir, Fight.PATH_SAVE_WORLD + WorldInfo.FILE_NAME);
            JsonDataReader jsonDataReader = new JsonDataReader(worldInfoJsonFile);

            JsonValue stringValues = jsonDataReader.readObj(WorldInfoTypes.STRING.getId());
            JsonValue longValues = jsonDataReader.readObj(WorldInfoTypes.LONG.getId());
            if (!stringValues.has(Fight.WORLD_NAME.getKey())) {
                Log.error(TAG, "世界存档信息缺失世界名称，跳过读取！！！");
                continue;
            }
            if (!longValues.has(Fight.WORLD_SEED.getKey())) {
                Log.error(TAG, "世界存档信息缺失世界种子，跳过读取！！！");
                continue;
            }

            //读取世界名称
            String worldName = stringValues.getString(Fight.WORLD_NAME.getKey());
            //读取世界种子
            long worldSeed = longValues.getLong(Fight.WORLD_SEED.getKey());

            //添加存档按钮UI
            WorldSaveButtonUI worldButton = new WorldSaveButtonUI(worldName, worldSeed);
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
