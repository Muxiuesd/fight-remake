package ttk.muxiuesd.mod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * 模组加载器
 * */
public class ModLoader {
    public final String TAG = this.getClass().getName();

    private String root = "../mods/";

    private HashMap<String, Mod> mods = new HashMap<>();

    public ModLoader() {
        if (!Gdx.files.local(root).exists()) {
            //新建mods文件夹
            Gdx.files.local(root).mkdirs();
        }

        boolean exists = Gdx.files.local(root + "testmod/main.js").exists();
        if (exists) {
            Log.print(TAG, "Found main.js");
        }else {
            Log.print(TAG, "No main.js");
        }

        FileHandle[] modDirs = this.getModDirs();
        for (int i = 0; i < modDirs.length; i++) {
            Log.print(TAG, modDirs[i].path());
            this.loadMod(modDirs[i]);
        }
        Log.print(TAG, "所有模组加载完成！共加载" + mods.size() + "个模组");
    }

    /**
     * 加载mod
     * */
    public void loadMod(FileHandle modDir) {
        FileHandle infoFileHandle = Gdx.files.local(modDir.path() + "/info.json");
        String infoString = infoFileHandle.readString();
        if (infoString == null) {
            //TODO
            return;
        }

        JsonReader reader = new JsonReader();
        JsonValue info = reader.parse(infoString);

        if (info.get("name") != null
            && info.get("namespace") != null
            && !this.hasMod(info)
            && info.get("version") != null
            && info.get("author") != null
            && info.get("description") != null
            && info.get("main") != null) {

            Mod mod = new Mod(info, modDir);
            this.mods.put(info.get("namespace").asString(), mod);
        }
    }

    /**
     * 运行mod
    * */
    public void runMod() {
        for (Mod mod : this.mods.values()) {
            mod.run();
        }
    }

    /**
     * 检查是否有重复的mod
     * namespace不可重复！
     * TODO 更加仔细的检查
     * */
    private boolean hasMod (JsonValue modInfo) {
        for (Mod mod : mods.values()) {
            if (Objects.equals(modInfo.get("namespace").asString(), mod.getModNamespace())){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有的mod的文件根目录
     * */
    public FileHandle[] getModDirs() {
        return Gdx.files.local(root).list();
    }

    public String getRoot() {
        return this.root;
    }
}
