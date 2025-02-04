package ttk.muxiuesd.mod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.entity.bullet.Bullet;
import ttk.muxiuesd.world.event.EventBus;
import ttk.muxiuesd.world.event.abs.*;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
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
    //mod根目录
    private final String root = "mods/";
    private String libCode;
    private ScriptEngine libEngine;

    private ModLoader() {
        this.checkRoot();
        this.loadLib();
        Log.print(TAG, "模组加载器初始化完毕！");
    }

    /**
     * 加载mod的底层库
    * */
    private void loadLib (){
        FileHandle libFile = Gdx.files.internal("modlib/lib.js");
        this.libCode = libFile.readString();
        this.libEngine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            this.libEngine.eval(this.getLibCode());
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
        Log.print(TAG, "模组底层库加载完毕。");
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
        this.addModEventCaller();
        Log.print(TAG, "所有模组加载完成！共加载" + ModContainer.getInstance().getAllMods().size() + "个模组");
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

            /*if (ModContainer.getInstance().hasMod(namespace)) {
                Log.print(TAG, "Mod的命名空间：" + namespace + "已经存在，不可重复添加！！！");
                return;
            }*/
            Mod mod = new Mod(info, modDir, this.libEngine.getContext());
            ModContainer.getInstance().add(namespace, mod);
            //this.mods.put(info.get("namespace").asString(), mod);

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
     * namespace不可重复！
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
     * 向EventBus添加mod里的事件调用，使得mod里注册的事件能被正确调用
     * */
    private void addModEventCaller () {
        EventBus eventBus = EventBus.getInstance();
        eventBus.addEvent(EventBus.EventType.BulletShoot, new BulletShootEvent() {
            @Override
            public void call (Entity shooter, Bullet bullet) {
                //Log.print(TAG, "射击者：" + shooter + " 射出子弹：" + bullet);
                Invocable invocable = (Invocable) getLibEngine();
                try {
                    invocable.invokeFunction("callBulletShootEvent", shooter, bullet);
                } catch (ScriptException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        eventBus.addEvent(EventBus.EventType.EntityAttacked, new EntityAttackedEvent() {
            @Override
            public void call (Entity attackObject, Entity victim) {
                Invocable invocable = (Invocable) getLibEngine();
                try {
                    invocable.invokeFunction("callEntityAttackedEvent",  attackObject, victim);
                } catch (ScriptException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        eventBus.addEvent(EventBus.EventType.EntityDeath, new EntityDeathEvent() {
            @Override
            public void call (Entity deadEntity) {
                Invocable invocable = (Invocable) getLibEngine();
                try {
                    invocable.invokeFunction("callEntityDeadEvent", deadEntity);
                } catch (ScriptException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        eventBus.addEvent(EventBus.EventType.KeyInput, new KeyInputEvent() {
            @Override
            public void call (int key) {
                Invocable invocable = (Invocable) getLibEngine();
                try {
                    invocable.invokeFunction("callWorldKeyInputEvent", key);
                } catch (ScriptException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        eventBus.addEvent(EventBus.EventType.ButtonInput, new ButtonInputEvent() {
            @Override
            public void call (int screenX, int screenY, int pointer, int button) {
                Invocable invocable = (Invocable) getLibEngine();
                try {
                    invocable.invokeFunction("callWorldButtonInput", screenX, screenY, pointer, button);
                } catch (ScriptException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * 获取所有的mod的文件根目录
     * */
    public FileHandle[] getModDirs() {
        return this.getRootFile().list();
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
        return ModContainer.getInstance().getAllMods();
    }

    /**
     * 获取底层库的代码
     * */
    public String getLibCode () {
        return this.libCode;
    }

    public ScriptEngine getLibEngine () {
        return this.libEngine;
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
