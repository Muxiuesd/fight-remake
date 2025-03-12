package ttk.muxiuesd.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.light.PointLight;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * 光源系统
 * */
public class LightSystem extends WorldSystem {

    private Array<PointLight> lights;
    private int lightSize = 0;
    private int uboId;

    public LightSystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {
        this.lights = new Array<>();
    }

    @Override
    public void draw (Batch batch) {
        //super.draw(batch);
        float[] lightPos = new float[lightSize];
        float[] lightColors = new float[lightSize];



        Gdx.gl30.glBindBuffer(GL30.GL_UNIFORM_BUFFER, uboId);

        if(lightSize !=0) {
            FloatBuffer lightPosBuffer = BufferUtils.newFloatBuffer(lightSize *4); // 创建一个大小为 21 的 FloatBuffer
            lightPosBuffer.put(lightPos, 0, lightSize *4); // 将需要更新的数据填充到 FloatBuffer 中
            lightPosBuffer.flip(); // 翻转缓冲区，使其准备好读取

            FloatBuffer lightColorBuffer = BufferUtils.newFloatBuffer(lightSize *4); // 创建一个大小为 21 的 FloatBuffer
            lightColorBuffer.put(lightColors, 0, lightSize *4); // 将需要更新的数据填充到 FloatBuffer 中
            lightColorBuffer.flip(); // 翻转缓冲区，使其准备好读取

            // 更新UBO数据

            Gdx.gl30.glBufferSubData(GL30.GL_UNIFORM_BUFFER, 0/*索引开始*/, lightSize *4*4/*长度*/, lightPosBuffer);
            Gdx.gl30.glBufferSubData(GL30.GL_UNIFORM_BUFFER, (lightPos.length) * 4, lightSize *4*4, lightColorBuffer);
        }

        IntBuffer lightSizeBuffer = BufferUtils.newIntBuffer(1);
        lightSizeBuffer.put(new int[]{lightSize}, 0, 1);
        lightSizeBuffer.flip(); // 翻转缓冲区，使其准备好读取
        Gdx.gl30.glBufferSubData(GL30.GL_UNIFORM_BUFFER, (lightPos.length )* 4+(lightColors.length) * 4, 4, lightSizeBuffer);

        Gdx.gl30.glBindBuffer(GL30.GL_UNIFORM_BUFFER, 0);//解除绑定
        lightSize =0;
    }


    public void addLight (PointLight light) {
        this.lights.add(light);
        this.lightSize ++;
    }

    public void removeLight (PointLight light) {
        if (this.lights.removeValue(light, true)) {
            this.lightSize --;
        }
    }
}
