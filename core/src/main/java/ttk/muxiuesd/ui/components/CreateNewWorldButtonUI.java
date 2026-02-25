package ttk.muxiuesd.ui.components;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.ui.screen.WorldsMenuUIScreen;
import ttk.muxiuesd.ui.text.Text;
import ttk.muxiuesd.util.FileUtil;
import ttk.muxiuesd.world.WorldInfo;

import java.util.UUID;

/**
 * 新建世界的按钮UI组件
 * */
public class CreateNewWorldButtonUI extends FightUITextButton {
    private WorldsMenuUIScreen worldsMenuUIScreen;

    public CreateNewWorldButtonUI (WorldsMenuUIScreen worldsMenuUIScreen) {
        super(
            Text.ofText(Fight.ID("button_create_new_world")),
            (button, interactPos) -> {
                //点击就新建一个世界
                String worldName = UUID.randomUUID().toString();
                String worldDirPath = Fight.PATH_SAVE + worldName + "/" + Fight.PATH_SAVE_WORLD;
                FileUtil.createDir(worldDirPath);
                FileUtil.createFile(worldDirPath, WorldInfo.FILE_NAME).writeString(
                    "{\n" +
                         "\t\"fight:string\": {\n" +
                         "\t\t\"world_name\": \"" + worldName +"\"\n" +
                         "\t}\n" +
                         "}",
                    false);
                //TODO 往世界信息文件里面写入基础信息

                ((CreateNewWorldButtonUI) button).worldsMenuUIScreen.flashSaveList();

                return false;
            }
        );

        this.worldsMenuUIScreen = worldsMenuUIScreen;
    }
}
