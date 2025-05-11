package ttk.muxiuesd.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import ttk.muxiuesd.interfaces.IWorldParticleRender;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.light.PointLight;
import ttk.muxiuesd.world.particle.abs.Particle;
import ttk.muxiuesd.world.particle.abs.ShinyParticle;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * 光源系统
 * */
public class LightSystem extends WorldSystem implements IWorldParticleRender {
    public static final int MAX_LIGHTS = 1688;
    // 假设每个光源位置是2个float,第3个是光的强度，一个light占4个，第四个没用，但是为了对齐内存，创建vec3也会要四个内存，glsl内部是vec4好计算
    private float[] lightPos = new float[MAX_LIGHTS * 4];

    // 假设每个光源颜色是3个float(我认为光不需要透明度)，第4个同样为了对齐内存，如果有需求可以用来传递额外内容
    private float[] lightColors = new float[MAX_LIGHTS * 4];

    private int lightSize = 0;
    private int uboId;

    public LightSystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {
        int programID = getWorld().getScreen().batch.getShader().getHandle();//获取着色器句柄，以供获取Uniform Block 的索引
        int blockIndex = Gdx.gl30.glGetUniformBlockIndex(programID, "LightBlock"); // 获取 Uniform Block 的索引
        Gdx.gl30.glUniformBlockBinding(programID, blockIndex, 0); // 绑定到绑定点 GPU会来0这个位置找数据

        uboId =Gdx.gl30.glGenBuffer();//获取一个还没用的uboID

        //根据uboID创建ubo，并绑定到ID，顺便绑定当前ubo为此ubo
        Gdx.gl30.glBindBuffer(GL30.GL_UNIFORM_BUFFER/*表示要创建的东西为UBO*/, uboId);

        Gdx.gl30.glBufferData(GL30.GL_UNIFORM_BUFFER,
            (lightPos.length * 4 + lightColors.length * 4+4)/*ubo空间大小:位置强度+颜色+记录光源个数的int*/,
            null/*null表示不立即上传数据,因为主要在draw()通过glBufferSubData更新数据*/,
            GL20.GL_DYNAMIC_DRAW/*表示数据需要频繁更新*/);

        // 将创建UBO 绑定到绑定点 0,和前面"LightBlock"的相同，GPU早到的就是这个已经创建好了的ubo
        Gdx.gl30.glBindBufferBase(GL30.GL_UNIFORM_BUFFER, 0, uboId);

        Gdx.gl30.glBindBuffer(GL30.GL_UNIFORM_BUFFER, 0);//解除绑定当前ubo，以免其它东西受影响
    }

    @Override
    public void draw(Batch batch) {
        this.initialize();
        //通过ubo的id绑定当前ubo为此ubo，该uboId已经创建ubo，所以不会再创建了
        Gdx.gl30.glBindBuffer(GL30.GL_UNIFORM_BUFFER, this.uboId);
        if(this.lightSize !=0)
        {
            // 创建一个大小为lightSize *4 的 FloatBuffer，单位为一个一个float
            FloatBuffer lightPosBuffer = BufferUtils.newFloatBuffer(this.lightSize *4);
            lightPosBuffer.put(this.lightPos, 0, this.lightSize *4); // 将需要更新的数据填充到 FloatBuffer 中
            lightPosBuffer.flip(); // 翻转缓冲区，使其准备好被ubo读取

            // 创建一个大小为 lightSize *4 的 FloatBuffer，颜色个数和光源个数是相等的
            FloatBuffer lightColorBuffer = BufferUtils.newFloatBuffer(this.lightSize *4);
            lightColorBuffer.put(this.lightColors, 0, this.lightSize *4); // 将需要更新的数据填充到 FloatBuffer 中
            lightColorBuffer.flip(); // 翻转缓冲区，使其准备好读取

            // 更新UBO数据,glBufferSubData用于更新ubo的数据
            // lightSize *4*4为字节，即表示往从索引0开始添加，添加lightSize *4*4字节的内容（一个float4个字节）
            Gdx.gl30.glBufferSubData(GL30.GL_UNIFORM_BUFFER, 0/*索引开始*/, this.lightSize *4*4/*长度*/, lightPosBuffer);
            Gdx.gl30.glBufferSubData(GL30.GL_UNIFORM_BUFFER, (this.lightPos.length) * 4/*索引开始，glsl端通过ubo里开始变量*/,
                lightSize *4*4, lightColorBuffer);
        }

        IntBuffer lightSizeBuffer = BufferUtils.newIntBuffer(1);// 创建一个大小为1 的 IntBuffer
        lightSizeBuffer.put(new int[]{this.lightSize}/*lightSize放进去 (这注释写了跟没写一样)*/, 0, 1);
        lightSizeBuffer.flip(); // 翻转缓冲区，使其准备好读取

        /*根据在着色器里定义的顺序，该int应为最后定义，故数据从索引(lightp.length )* 4+(lightColors.length) * 4
        开始(前面的为前两个变量的内存大小,ubo的内存存储是连续的)*/
        Gdx.gl30.glBufferSubData(GL30.GL_UNIFORM_BUFFER, (this.lightPos.length )* 4+(this.lightColors.length) * 4, 4, lightSizeBuffer);

        Gdx.gl30.glBindBuffer(GL30.GL_UNIFORM_BUFFER, 0);//解除绑定

        this.lightSize = 0;//清除数组大小标记，以便下一帧重新收集
    }

    @Override
    public void render (Batch batch, ShapeRenderer shapeRenderer) {
        this.draw(batch);
    }

    @Override
    public int getRenderPriority () {
        return 200;
    }

    /**
     * 收集所有的发光例子的数据
     * */
    public void useLight(Array<? extends Particle> particleArray) {
        for (Particle particle:particleArray) {
            if(particle instanceof ShinyParticle) {
                ShinyParticle sp = (ShinyParticle) particle;
                this.useLight(sp.getLight());
            }
        }
    }

    private void useLight (PointLight light) {
        //在这一帧里收集调用该方法的y所有光源数据
        if(this.lightSize < MAX_LIGHTS) {
            //往数组里传入light的数据
            this.lightPos[this.lightSize*4] = light.getPosition().x;
            this.lightPos[this.lightSize*4+1] = light.getPosition().y;
            this.lightPos[this.lightSize*4+2] = light.getIntensity();
            // 颜色
            this.lightColors[this.lightSize*4] = light.getColor().r;
            this.lightColors[this.lightSize*4+1] = light.getColor().g;
            this.lightColors[this.lightSize*4+2] = light.getColor().b;
            //数组可用长度
            this.lightSize++;
        }
    }
}
