package ttk.muxiuesd.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
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
public class LightSystem extends WorldSystem {

    // 假设每个光源位置是2个float,第3个是光的强度，一个light占4个，第四个没用，但是为了对齐内存，创建vec3也会要四个内存，glsl内部是vec4好计算
    private float[] lightp = new float[200 * 4];

    // 假设每个光源颜色是3个float(我认为光不需要透明度)，第4个同样为了对齐内存，如果有需求可以用来传递额外内容
    private float[] lightColors = new float[200 * 4];

    private static int lightSize =0;
    private static int uboId;
    boolean inied=false;

    public LightSystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {

    }

    @Override
    public void draw(Batch batch) {
        //通过ubo的id绑定当前ubo为此ubo，该uboId已经创建ubo，所以不会再创建了
        if(!inied)
        {
            int programID = getWorld().getScreen().batch.getShader().getHandle();//获取着色器句柄，以供获取Uniform Block 的索引
            int blockIndex = Gdx.gl30.glGetUniformBlockIndex(programID, "LightBlock"); // 获取 Uniform Block 的索引
            Gdx.gl30.glUniformBlockBinding(programID, blockIndex, 0); // 绑定到绑定点 GPU会来0这个位置找数据

            uboId =Gdx.gl30.glGenBuffer();//获取一个还没用的uboID

            //根据uboID创建ubo，并绑定到ID，顺便绑定当前ubo为此ubo
            Gdx.gl30.glBindBuffer(GL30.GL_UNIFORM_BUFFER/*表示要创建的东西为UBO*/, uboId);

            Gdx.gl30.glBufferData(GL30.GL_UNIFORM_BUFFER,
                (lightp.length * 4 + lightColors.length * 4+4)/*ubo空间大小:位置强度+颜色+记录光源个数的int*/,
                null/*null表示不立即上传数据,因为主要在draw()通过glBufferSubData更新数据*/,
                GL20.GL_DYNAMIC_DRAW/*表示数据需要频繁更新*/);

            // 将创建UBO 绑定到绑定点 0,和前面"LightBlock"的相同，GPU早到的就是这个已经创建好了的ubo
            Gdx.gl30.glBindBufferBase(GL30.GL_UNIFORM_BUFFER, 0, uboId);

            Gdx.gl30.glBindBuffer(GL30.GL_UNIFORM_BUFFER, 0);//解除绑定当前ubo，以免其它东西受影响
            inied=true;
        }
        Gdx.gl30.glBindBuffer(GL30.GL_UNIFORM_BUFFER, uboId);
        if(lightSize !=0)
        {
            // 创建一个大小为lightSize *4 的 FloatBuffer，单位为一个一个float
            FloatBuffer lightpbuffer = BufferUtils.newFloatBuffer(lightSize *4);
            lightpbuffer.put(lightp, 0, lightSize *4); // 将需要更新的数据填充到 FloatBuffer 中
            lightpbuffer.flip(); // 翻转缓冲区，使其准备好被ubo读取

            /**/

            // 创建一个大小为 lightSize *4 的 FloatBuffer，颜色个数和光源个数是相等的
            FloatBuffer lightColorbuffer = BufferUtils.newFloatBuffer(lightSize *4);
            lightColorbuffer.put(lightColors, 0, lightSize *4); // 将需要更新的数据填充到 FloatBuffer 中
            lightColorbuffer.flip(); // 翻转缓冲区，使其准备好读取

            // 更新UBO数据,glBufferSubData用于更新ubo的数据
            // lightSize *4*4为字节，即表示往从索引0开始添加，添加lightSize *4*4字节的内容（一个float4个字节）
            Gdx.gl30.glBufferSubData(GL30.GL_UNIFORM_BUFFER, 0/*索引开始*/, lightSize *4*4/*长度*/, lightpbuffer);
            Gdx.gl30.glBufferSubData(GL30.GL_UNIFORM_BUFFER, (lightp.length) * 4/*索引开始，glsl端通过ubo里开始变量*/,
                lightSize *4*4, lightColorbuffer);



        }

        IntBuffer lighsizebuffer = BufferUtils.newIntBuffer(1);// 创建一个大小为1 的 IntBuffer
        lighsizebuffer.put(new int[]{lightSize}/*lightSize放进去 (这注释写了跟没写一样)*/, 0, 1);
        lighsizebuffer.flip(); // 翻转缓冲区，使其准备好读取

        /*根据在着色器里定义的顺序，该int应为最后定义，故数据从索引(lightp.length )* 4+(lightColors.length) * 4
        开始(前面的为前两个变量的内存大小,ubo的内存存储是连续的)*/
        Gdx.gl30.glBufferSubData(GL30.GL_UNIFORM_BUFFER, (lightp.length )* 4+(lightColors.length) * 4, 4, lighsizebuffer);

        Gdx.gl30.glBindBuffer(GL30.GL_UNIFORM_BUFFER, 0);//解除绑定

        lightSize =0;//清除数组大小标记，以便下一帧重新收集

    }

    public void useLight(Array<? extends Particle> particleArray) {
        for (Particle p:particleArray)
        {
            if(p instanceof ShinyParticle)
            {
                ShinyParticle sp=(ShinyParticle)p;
                useLight(sp.light);
            }
        }
    }

    public void useLight (PointLight light) {
        //在这一帧里收集调用该方法的y所有光源数据
        if(lightSize<100)
        {
            //往数组里传入light的数据
            lightp[lightSize*4]=light.getPosition().x;
            lightp[lightSize*4+1]=light.getPosition().y;
            lightp[lightSize*4+2]=light.getIntensity();
            // 颜色
            lightColors[lightSize*4]=light.getColor().r;
            lightColors[lightSize*4+1]=light.getColor().g;
            lightColors[lightSize*4+2]=light.getColor().b;
            //数组可用长度
            lightSize++;
        }
    }
}
