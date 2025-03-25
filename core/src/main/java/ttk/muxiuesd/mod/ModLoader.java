package ttk.muxiuesd.mod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.util.Log;

import java.util.HashMap;
import java.util.Objects;


/**
 * 模组加载器
 * <p>
 * 全局可调用
 * <p>
 * TODO mod的热加载
 * */
public class ModLoader {
    public final String TAG = this.getClass().getName();

    //这个必须是单例模式
    private static ModLoader Instance;

    //mod根目录
    private final String root = "mods/";

    private ModLoader() {
        this.checkRoot();
        //ModLibManager.getInstance().loadAllLib();
        Log.print(TAG, "模组加载器初始化完毕！");
    }

    /**
     * 加载所有mod
     * */
    public void loadAllMods () {
        FileHandle[] modDirs = this.getModDirs();
        for (int i = 0; i < modDirs.length; i++) {
            Log.print(TAG, modDirs[i].path());
            this.loadMod(modDirs[i]);
        }
        //注册mod里的事件调用
        ModEventCaller.registryAllEventCaller();

        Log.print(TAG, "所有模组加载完成！共加载：" + ModContainer.getInstance().getAllMods().size() + " 个模组");
    }

    /**
     * 加载指定目录里的mod
     * */
    private void loadMod(FileHandle modDir) {
        FileHandle infoFileHandle = modDir.child("info.json");
        String infoString = infoFileHandle.readString();
        if (infoString == null) {
            //TODO
            return;
        }

        JsonReader reader = new JsonReader();
        JsonValue info = reader.parse(infoString);
        String namespace = info.getString("namespace");

        if (! Objects.equals(info.getString("name"), "")
            && ! Objects.equals(namespace, "")
            && ! this.hasMod(info)
            && ! Objects.equals(info.getString("version"), "")
            && ! Objects.equals(info.getString("author"), "")
            && ! Objects.equals(info.getString("description"), "")
            && ! Objects.equals(info.getString("main"), "")) {

            Mod mod = new Mod(info, modDir, ModLibManager.getInstance().getLibEngine().getContext());
            ModContainer.getInstance().add(namespace, mod);
            AssetsLoader.getInstance().addModAssetManager(namespace);

            Log.print(TAG, "文件夹：" + modDir.name() +" 的模组：" + mod.getModName() +" 完成加载。");
        }else {
            Log.error(TAG, "文件夹：" + modDir.name() + "中的info.json文件不合法，跳过加载此模组！！！");
        }
    }

    /**
     * 运行所有mod
    * */
    public void runAllMods () {
        if (ModContainer.getInstance().isEmpty()) {
            Log.print(TAG, "没有加载任何模组，跳过运行");
            return;
        }

        Log.print(TAG, "开始运行所有的模组……");
        HashMap<String, Mod> allMods = ModContainer.getInstance().getAllMods();
        for (Mod mod : allMods.values()) {
            mod.run();
        }
    }

    /**
     * 检查是否有重复的mod
     * <p>
     * namespace不可重复！
     * <p>
     * TODO 更加仔细的检查
     * */
    private boolean hasMod (JsonValue modInfo) {
        String namespace = modInfo.get("namespace").asString();
        for (Mod mod : this.getMods().values()) {
            if (Objects.equals(namespace, mod.getModNamespace())){
                Log.error(TAG,
                    "待加载的模组："+ modInfo.getString("name") +" 的命名空间：" + namespace
                        + " 与已有的模组：" + mod.getModName() +" 的命名空间重复！！！");
                return true;
            }
        }
        return false;
    }


    /**
     * 获取模组加载器的实例
     * */
    public static ModLoader getInstance() {
        if (Instance == null) {
            Instance = new ModLoader();
        }
        return Instance;
    }

    /**
     * 获取所有的mod的文件根目录
     * */
    public FileHandle[] getModDirs() {
        return this.getRootFile().list();
    }

    public HashMap<String, Mod> getMods () {
        return ModContainer.getInstance().getAllMods();
    }

    public String getRoot() {
        return this.root;
    }

    /**
     *  获取mods应该存在的目录
     **/
    public FileHandle getRootFile () {
        String storagePath = Gdx.files.getLocalStoragePath();
        return Gdx.files.absolute(storagePath + this.getRoot());
    }

    /**
     * 检查root
     * */
    private void checkRoot () {
        Log.print(TAG, "mods文件夹根目录：" + this.getRootFile().toString());
        if (!this.getRootFile().exists()) {
            //新建mods文件夹
            this.getRootFile().mkdirs();
            Log.print(TAG, "创建mods文件夹");
        }else {
            Log.print(TAG, "mods文件夹已经存在：" + this.getRootFile().path() + "，跳过创建");
        }
    }
}
