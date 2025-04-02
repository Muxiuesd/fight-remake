package ttk.muxiuesd.mod.api;

import ttk.muxiuesd.audio.AudioLoader;
import ttk.muxiuesd.mod.Mod;
import ttk.muxiuesd.mod.ModContainer;
import ttk.muxiuesd.util.Log;

import java.util.HashMap;

/**
 * modAPI：音频注册加载器
 * */
public class ModAudioRegister {
    public static String TAG = ModAudioRegister.class.getName();

    private static HashMap<String, ModAudioRegister> loaders = new HashMap<>();

    private final AudioLoader loader;
    public final String modRoot;

    public ModAudioRegister (String modRoot) {
        this.modRoot = modRoot;
        this.loader = new AudioLoader(modRoot);
    }

    /**
     * 获取音频加载器
     * */
    public static ModAudioRegister getRegister (String namespace) {
        ModContainer modContainer = ModContainer.getInstance();
        boolean hasMod = modContainer.hasMod(namespace);
        if (!hasMod) {
            Log.error(TAG, "不存在namespace为：" + namespace + " 的模组，无法加载此mod的音频加载器");
            throw new RuntimeException("不存在namespace为：" + namespace + " 的模组！！！");
        }

        if (loaders.containsKey(namespace)) {
            return loaders.get(namespace);
        }
        Mod mod = modContainer.get(namespace);
        ModAudioRegister loader = new ModAudioRegister(mod.getModPath());
        loaders.put(namespace, loader);
        return loader;
    }


    public void registerSound (String id, String path) {
        loader.loadSound(id, path);
    }

    /**
     * 把sound加载成music，目的是为了配合空间音效
     * */
    public void registerSoundAsMusic (String id, String path) {
        loader.loadMusic(id, path);
    }

    public void registerMusic (String id, String path) {
        loader.loadMusic(id, path);
    }
}
