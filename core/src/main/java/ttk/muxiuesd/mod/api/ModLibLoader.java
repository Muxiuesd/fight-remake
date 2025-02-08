package ttk.muxiuesd.mod.api;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ttk.muxiuesd.mod.ModLibManager;
import ttk.muxiuesd.util.Log;

import javax.script.ScriptException;

/**
 *  modlib专用：用来加载mod底层库的类
 **/
public class ModLibLoader {
    public final String TAG = ModLibLoader.class.getName();

    private String libRoot = "modlib/";

    public ModLibLoader () {
        Log.print(TAG, "ModLibLoader初始化完成");
    }

    public void load (String path) {
        FileHandle libFile = getLibFile(path);
        String code = libFile.readString();
        if (!libFile.exists()) {
            Log.error(TAG, "无法加载：" + path + " ！！！");
            return;
        }
        if (code == null || code.isBlank()) {
            Log.error(TAG, path + "的内容为空！！！");
            return;
        }
        ModLibManager libManager = ModLibManager.getInstance();
        try {
            libManager.getLibEngine().eval(code);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    private FileHandle getLibFile (String path) {
        return Gdx.files.internal("modlib/" + path);
    }
}
