package ttk.muxiuesd.shader;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 着色器调度器
 * */
public class ShaderScheduler {
    private static ShaderScheduler instance;    //单例模式

    private final HashMap<String, ShaderProgram> shaders;   //id映射着色器
    private final HashMap<String, Batch> activeShaders;     //哪一个batch在使用着色器

    private ShaderScheduler () {
        this.shaders = new LinkedHashMap<>();
        this.activeShaders = new LinkedHashMap<>();
    }
    public static void init () {
        if (instance == null) {
            instance = new ShaderScheduler();
        }
    }
    public static ShaderScheduler getInstance () {
        return instance;
    }

    /**
     * 让一个batch开始使用一个着色器
     * */
    public ShaderProgram begin (String shaderId, Batch batch) {
        if (!this.contains(shaderId)) {
            throw new RuntimeException("着色器：" + shaderId + " 不存在！！！");
        }

        ShaderProgram shader = this.shaders.get(shaderId);
        shader.bind();
        batch.flush();
        batch.setShader(shader);
        this.activeShaders.put(shaderId, batch);

        return shader;
    }

    /**
     * 结束使用这个着色器
     * */
    public void end (String shaderId) {
        if (!this.activeShaders.containsKey(shaderId)) {
            throw new RuntimeException("着色器：" + shaderId + " 没有在使用！！！");
        }
        Batch batch = this.activeShaders.get(shaderId);
        batch.flush();
        batch.setShader(null);
        //从活跃列表中移除
        this.activeShaders.remove(shaderId);
    }

    /**
     * 注册一种着色器
     * */
    public void registry (String shaderId, ShaderProgram shaderProgram) {
        if (this.contains(shaderId)) {
            throw new RuntimeException("着色器：" + shaderId + " 已存在！！！");
        }
        if (!shaderProgram.isCompiled()) {
            throw new RuntimeException("着色器" + shaderId + " 没有完成加载，无法注册！" + shaderProgram.getLog());
        }
        this.shaders.put(shaderId, shaderProgram);
    }

    /**
     * 移除着色器
     * */
    public void remove (String shaderId) {
        if (!this.contains(shaderId)) {
            throw new RuntimeException("着色器：" + shaderId + " 不存在！！！");
        }
        this.shaders.remove(shaderId);
    }

    public boolean contains(String shaderId) {
        return this.shaders.containsKey(shaderId);
    }
}
