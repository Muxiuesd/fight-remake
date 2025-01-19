package ttk.muxiuesd.mod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.entity.bullet.Bullet;
import ttk.muxiuesd.world.event.EventBus;
import ttk.muxiuesd.world.event.abs.BulletShootEvent;
import ttk.muxiuesd.world.event.abs.EntityAttackedEvent;
import ttk.muxiuesd.world.event.abs.EntityDeathEvent;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Objects;


/**
 * 模组加载器
 * */
public class ModLoader {
    public final String TAG = this.getClass().getName();

    //这个必须是单例模式
    private static ModLoader Instance;

    private final String root = "../mods/";
    private String lib;
    private HashMap<String, Mod> mods = new HashMap<>();

    private ModLoader() {

        if (!Gdx.files.local(root).exists()) {
            //新建mods文件夹
            Gdx.files.local(root).mkdirs();
        }
        //加载底层库
        FileHandle libFile = Gdx.files.internal("lib/lib.js");
        this.lib = libFile.readString();
        Log.print(TAG, "模组底层库加载完毕。");
        /*boolean exists = Gdx.files.local(root + "testmod/main.js").exists();
        if (exists) {
            Log.print(TAG, "Found main.js");
        }else {
            Log.print(TAG, "No main.js");
        }*/
    }

    /**
     * 对外开放的方法：加载所有mod
     * */
    public void loadAllMods () {
        FileHandle[] modDirs = this.getModDirs();
        for (int i = 0; i < modDirs.length; i++) {
            Log.print(TAG, modDirs[i].path());
            this.loadMod(modDirs[i]);
        }
        this.addModEventCaller();
        Log.print(TAG, "所有模组加载完成！共加载" + mods.size() + "个模组");
    }

    /**
     * 加载mod
     * */
    private void loadMod(FileHandle modDir) {
        FileHandle infoFileHandle = Gdx.files.local(modDir.path() + "/info.json");
        String infoString = infoFileHandle.readString();
        if (infoString == null) {
            //TODO
            return;
        }

        JsonReader reader = new JsonReader();
        JsonValue info = reader.parse(infoString);

        if (! Objects.equals(info.getString("name"), "")
            && ! Objects.equals(info.getString("namespace"), "")
            && !this.hasMod(info)
            && ! Objects.equals(info.getString("version"), "")
            && ! Objects.equals(info.getString("author"), "")
            && ! Objects.equals(info.getString("description"), "")
            && ! Objects.equals(info.getString("main"), "")) {

            Mod mod = new Mod(info, modDir);
            this.mods.put(info.get("namespace").asString(), mod);

            Log.print(TAG, "文件夹：" + modDir.name() +" 的模组：" + mod.getModName() +" 完成加载。");
        }else {
            Log.error(TAG, "文件夹：" + modDir.name() + "中的info.json文件不合法，跳过加载此模组！！！");
        }

    }

    /**
     * 运行所有mod
    * */
    public void runAllMods () {
        if (mods.isEmpty()) {
            Log.print(TAG, "没有加载任何模组，跳过运行");
            return;
        }
        Log.print(TAG, "开始运行所有的模组……");
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
        String namespace = modInfo.get("namespace").asString();
        for (Mod mod : mods.values()) {
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
     * 向EventBus添加mod里的事件调用，使得mod里注册的事件能被正确调用
     * */
    private void addModEventCaller () {
        EventBus eventBus = EventBus.getInstance();
        eventBus.addEvent(EventBus.BulletShoot, new BulletShootEvent() {
            @Override
            public void call (Entity shooter, Bullet bullet) {
                //Log.print(TAG, "射击者：" + shooter + " 射出子弹：" + bullet);
                //调用mod里的事件
                HashMap<String, Mod> mods = ModLoader.getInstance().getMods();
                for (Mod mod : mods.values()) {
                    Invocable invocable = (Invocable) mod.getEngine();
                    try {
                        invocable.invokeFunction("callBulletShootEvent", shooter, bullet);
                    } catch (ScriptException | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        eventBus.addEvent(EventBus.EntityAttacked, new EntityAttackedEvent() {
            @Override
            public void call (Entity attackObject, Entity victim) {
                HashMap<String, Mod> mods = ModLoader.getInstance().getMods();
                for (Mod mod : mods.values()) {
                    Invocable invocable = (Invocable) mod.getEngine();
                    try {
                        invocable.invokeFunction("callEntityAttackedEvent", attackObject, victim);
                    } catch (ScriptException | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        eventBus.addEvent(EventBus.EntityDeath, new EntityDeathEvent() {
            @Override
            public void call (Entity deadEntity) {
                HashMap<String, Mod> mods = ModLoader.getInstance().getMods();
                for (Mod mod : mods.values()) {
                    Invocable invocable = (Invocable) mod.getEngine();
                    try {
                        invocable.invokeFunction("callEntityDeadEvent", deadEntity);
                    } catch (ScriptException | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    /**
     * 获取所有的mod的文件根目录
     * */
    public FileHandle[] getModDirs() {
        return Gdx.files.local(root).list();
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

    public HashMap<String, Mod> getMods () {
        return this.mods;
    }

    /**
     * 获取底层库的代码
     * */
    public String getLib () {
        return this.lib;
    }

    public String getRoot() {
        return this.root;
    }
}
