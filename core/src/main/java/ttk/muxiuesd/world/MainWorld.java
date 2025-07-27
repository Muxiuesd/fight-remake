package ttk.muxiuesd.world;

import ttk.muxiuesd.registry.WorldInformation;
import ttk.muxiuesd.screen.MainGameScreen;
import ttk.muxiuesd.system.*;

/**
 *  主世界
 * */
public class MainWorld extends World {
    public MainWorld(MainGameScreen screen) {
        super(screen);
        //一定要最先初始化世界系统管理器
        setWorldSystemsManager(new WorldSystemsManager(this));
        addSystem("TimeSystem", new TimeSystem(this));
        addSystem("EventSystem", new EventSystem(this));
        addSystem("PlayerSystem", new PlayerSystem(this));
        addSystem("ChunkSystem", new ChunkSystem(this));
        addSystem("EntitySystem", new EntitySystem(this));
        addSystem("UndergroundEntityRenderSystem", new UndergroundEntityRenderSystem(this));
        addSystem("GroundEntityRenderSystem", new GroundEntityRenderSystem(this));
        addSystem("DaynightSystem", new DaynightSystem(this));
        addSystem("ParticleSystem", new ParticleSystem(this));
        addSystem("CameraFollowSystem", new CameraFollowSystem(this));
        addSystem("EntityCollisionSystem", new GroundEntityCollisionSystem(this));
        addSystem("BulletCollisionCheckSystem", new BulletCollisionCheckSystem(this));
        addSystem("HandleInputSystem", new HandleInputSystem(this));
        addSystem("SoundEffectSystem", new SoundEffectSystem(this));
        addSystem("MonsterGenerationSystem", new MonsterGenerationSystem(this));
        addSystem("UndergroundCreatureGenSystem", new UndergroundCreatureGenSystem(this));
        addSystem("LightSystem", new LightSystem(this));

        WorldInformation.INT.putIfAbsent("seed", 114514);
        WorldInformation.LONG.putIfAbsent("seed", 114514L);
    }


}
