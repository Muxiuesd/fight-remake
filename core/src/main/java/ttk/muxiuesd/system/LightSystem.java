package ttk.muxiuesd.system;

import com.aliasifkhan.hackLights.HackLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.interfaces.render.IWorldParticleRender;
import ttk.muxiuesd.render.camera.PlayerCamera;
import ttk.muxiuesd.render.fix.FightHackLightEngine;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.light.PointLight;
import ttk.muxiuesd.world.particle.abs.Particle;
import ttk.muxiuesd.world.particle.abs.ShinyParticle;

/**
 * 光源系统
 */
public class LightSystem extends WorldSystem implements IWorldParticleRender {
    public static final int MAX_LIGHTS = 256;
    private int lightSize = 0;
    private TimeSystem timeSystem;

    private Color DAY_COLOR = new Color(1.0f, 0.98f, 0.9f, 1f);    // 浅黄色（白天）
    private Color NIGHT_COLOR = new Color(0.04f, 0.06f, 0.12f, 1f);  // 深蓝色（夜晚）
    private Color DUSK_COLOR = new Color(0.8f, 0.4f, 0.2f, 1f);    // 黄昏橙红色
    private FightHackLightEngine lightEngine;
    private HackLight[] lights;

    public LightSystem(World world) {
        super(world);
    }

    @Override
    public void initialize() {
        this.lightEngine = new FightHackLightEngine();
        this.lights = new HackLight[MAX_LIGHTS];
        TextureRegion lightRegion = new TextureRegion(new Texture("texture/light/light.png"));
        for (int i = 0; i < this.lights.length; i++) {
            this.lights[i] = new HackLight(lightRegion, 1, 1, 1, 1);
            this.lights[i].setScale(0);
            this.lightEngine.addLight(this.lights[i]);
        }
        this.lightEngine.update(640, 480);
        this.timeSystem = getWorld().getSystem(TimeSystem.class);
    }

    @Override
    public void batchRender (Batch batch) {
        //this.afterProcess();
    }

    @Override
    public void shapeRender (ShapeRenderer shapeRenderer) {
        //this.afterProcess();
    }

    @Override
    public int getRenderPriority() {
        return 200;
    }

    /**
     * 收集所有的发光粒子的数据
     */
    public void useLight(Array<? extends Particle> particleArray) {
        for (Particle particle : particleArray) {
            if (particle instanceof ShinyParticle) {
                ShinyParticle sp = (ShinyParticle) particle;
                this.useLight(sp.getLight());
            }
        }
    }

    /**
     * 应用一个光源
     */
    public void useLight(PointLight light) {
        //在这一帧里收集调用该方法的y所有光源数据
        if (this.lightSize < MAX_LIGHTS) {
            this.lights[this.lightSize].setScale(0.02f * light.getIntensity());
            this.lights[this.lightSize].setOriginBasedPosition(light.getPosition().x, light.getPosition().y);
            this.lights[this.lightSize].setColor(light.getColor());
            this.lightSize++;
        }
    }

    /**
     * 处理光照
     * */
    public void afterProcess() {
        float time = this.timeSystem.getGameTime() / 24f;
        float timeCurve = MathUtils.sin(time * 3.14159265f * 2.0f) * 0.5f + 0.5f;
        // 混合昼夜颜色
        Color lightColor = NIGHT_COLOR.cpy().lerp(DAY_COLOR, timeCurve);
        // 添加黄昏过渡
        if (timeCurve > 0.4f && timeCurve < 0.6f) {
            float duskFactor = Math.abs(timeCurve - 0.5f) * 10.0f;
            lightColor.lerp(DUSK_COLOR, 1.0f - duskFactor);
        }
        this.lightEngine.setAmbientLightColor(lightColor);
        this.lightEngine.draw(PlayerCamera.INSTANCE.getCamera().combined);
        for (HackLight light : lights) {
            light.setScale(0);
        }
        this.lightSize = 0;
    }
}
