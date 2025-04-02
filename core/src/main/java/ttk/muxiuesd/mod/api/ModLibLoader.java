package ttk.muxiuesd.mod.api;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ttk.muxiuesd.mod.ModLibManager;
import ttk.muxiuesd.util.Log;

import javax.script.ScriptException;
import java.util.Objects;

/**
 *  modAPI：用来加载mod底层库的类
 *  <p>
 *  支持加载游戏内部和外部的js代码
 **/
public class ModLibLoader {
    public final String TAG = ModLibLoader.class.getName();

    private String libRoot = "modlib/";

    public ModLibLoader () {
        Log.print(TAG, "ModLibLoader初始化完成");
    }

    /**
     * 加载执行库代码
     * @param space 只能为：Internal（游戏内部库）或 External（游戏外部库）
     * @param path 如果是：Internal（游戏内部库）则填游戏内部资产目录下的路径；若是：External（游戏外部库）则填游戏外部的路径
     **/
    public void load (String space, String path) {
        FileHandle libFile;
        if (Objects.equals(space, "Internal")) {
            libFile = this.getInternalLibFile(path);
        }else if (Objects.equals(space, "External")) {
            libFile = this.getExternalLibFile(path);
        }else {
            Log.error(TAG, "传入的加载空间：" + space + "不正确，只能为：Internal（游戏内部库）或 External（游戏外部库）");
            return;
        }
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

    /**
     * 游戏源码内的库代码
     * */
    private FileHandle getInternalLibFile (String path) {
        return Gdx.files.internal("modlib/" + path);
    }

    /**
     * 游戏外部的库代码，比如mod的写的库
     * */
    private FileHandle getExternalLibFile (String path) {
        return Gdx.files.absolute(path);
    }
}
