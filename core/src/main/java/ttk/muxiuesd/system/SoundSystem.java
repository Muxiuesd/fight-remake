package ttk.muxiuesd.system;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudio;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioSource;
import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.audio.SoundGroup;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.system.game.SpatialAudioSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.player.Player;

import java.util.HashMap;

/**
 * 游戏世界的空间音效系统
 * <p>
 * 只要是在游戏世界内播放的音效，就调用这个系统来播放。这里面调用播放的音频，出了游戏世界就不再播放，比如退出游戏世界后停止播放里面的一切音频。
 * */
public class SoundSystem extends WorldSystem {

    private ChunkSystem cs;
    private PlayerSystem ps;
    private EntitySystem es;

    /** 走路时在音效组内换一首的间隔（秒） */
    private static final float WALK_SWITCH_SPAN = 0.4f;

    //正在播放的音效
    private Array<SpatialAudio> activeSounds;
    private Array<SpatialAudio> needRemoved;

    // 走路音效缓存：每首走路音只创建一次 SpatialAudio，切换时只调音量而不是 stop+new
    private final HashMap<AudioHolder, SpatialAudio> walkAudioCache = new HashMap<>();
    private SoundGroup curWalkGroup;
    private AudioHolder curWalkAudio;
    private SpatialAudio curWalkAudioInstance;
    private float walkSwitchTimer;

    public SoundSystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {
        this.activeSounds = new Array<>();
        this.needRemoved = new Array<>();
        this.ps = getWorld().getSystem(PlayerSystem.class);
        this.cs = getWorld().getSystem(ChunkSystem.class);
        this.es = getWorld().getSystem(EntitySystem.class);
    }

    @Override
    public void update (float delta) {
        this.activeSounds.forEach( (audio) -> {
            if (!audio.isPlaying()) this.needRemoved.add(audio);
        });
        this.activeSounds.removeAll(this.needRemoved, true);
        this.needRemoved.clear();

        this.updatePlayerSoundEffect(delta);
        this.updateEnemySoundEffect(delta);
        this.updateEnvironmentalEffects(delta);
    }

    @Override
    public void dispose () {
        this.muteWalkSound();
        this.walkAudioCache.values().forEach(SpatialAudio::stop);
        this.walkAudioCache.clear();
        this.activeSounds.forEach(SpatialAudio::stop);
        this.activeSounds.clear();
    }

    /**
     * 玩家相关的音效
     * */
    private void updatePlayerSoundEffect (float delta) {
        this.updatePlayerWalkSoundEffect(delta);
    }

    /**
     *  玩家走路音效
     * */
    private void updatePlayerWalkSoundEffect (float delta) {
        if (this.ps.playerMoved()) {
            Player player = this.ps.getPlayer();
            Vector2 playerCenter = player.getCenterPos();
            Block underfootBlock = cs.getBlock(playerCenter.x, playerCenter.y);
            SoundGroup walkGroup = underfootBlock.getProperty().getSounds().getWalkGroup();

            //脚下方块的材质变了才换组，同一种地面连续走只在组内随机换音
            if (this.curWalkGroup != walkGroup) {
                this.muteWalkSound();
                this.curWalkGroup = walkGroup;
                this.walkSwitchTimer = 0f;
                this.playWalkSound(walkGroup.random(), player);
            } else {
                this.walkSwitchTimer += delta;
                if (this.walkSwitchTimer >= WALK_SWITCH_SPAN) {
                    this.walkSwitchTimer = 0f;
                    this.switchWalkSound(walkGroup, player);
                }
            }

            // 更新声源位置（玩家在移动）
            if (this.curWalkAudioInstance != null) {
                this.curWalkAudioInstance.setBoundSource(player.getSounder());
            }
        } else if (this.curWalkGroup != null) {
            this.muteWalkSound();
            this.curWalkAudio = null;
            this.curWalkGroup = null;
            this.walkSwitchTimer = 0f;
        }
    }

    /**
     * 在同一个音效组内换一首
     * <p>
     * 旧音只静音不停止：停止会被音频引擎当作播放完毕而回收 source，切回来时又得重新创建
     * */
    private void switchWalkSound (SoundGroup group, Entity<?> walker) {
        int lastIndex = group.indexOf(this.curWalkAudio);
        this.muteWalkSound();
        this.playWalkSound(group.randomExcluding(lastIndex), walker);
    }

    /**
     * 播放/恢复方块类（方块 / 墙体 / 附着物）的走路音效
     * <p>
     * 三者都继承 Block，音效都挂在 Block.Property 的 BlockSounds 上，所以统一从这三个方法播放
     * @param block 脚下的方块类
     * @param walker 走路的实体
     * */
    public SpatialAudio playWalkSound (Block block, Entity<?> walker) {
        return this.playWalkSound(block.getProperty().getSounds().walk(), walker);
    }

    /**
     * 播放/恢复指定下标的走路音效
     * @param index 走路音效组内的下标
     * */
    public SpatialAudio playWalkSound (Block block, Entity<?> walker, int index) {
        return this.playWalkSound(block.getProperty().getSounds().walk(index), walker);
    }

    /**
     * 播放/恢复走路音效（优先复用缓存）
     * */
    private SpatialAudio playWalkSound (AudioHolder walkAudio, Entity<?> walker) {
        SpatialAudio audio = this.walkAudioCache.get(walkAudio);
        if (audio == null || !audio.isPlaying()) {
            audio = this.playSpatialSound(walkAudio, walker);
            audio.setLooping(true);
            this.walkAudioCache.put(walkAudio, audio);
        } else {
            audio.setBoundSource(walker.getSounder());
            audio.setVolume(1f);
        }
        this.curWalkAudio = walkAudio;
        this.curWalkAudioInstance = audio;
        return audio;
    }

    /**
     * 播放方块类（方块 / 墙体 / 附着物）的放置音效
     * @param block 被放置的方块类
     * */
    public SpatialAudio playPutSound (Block block, float wx, float wy) {
        return this.playSpatialSound(block.getProperty().getSounds().put(), this.atPosition(wx, wy));
    }

    /**
     * 播放指定下标的放置音效
     * @param index 放置音效组内的下标
     * */
    public SpatialAudio playPutSound (Block block, float wx, float wy, int index) {
        return this.playSpatialSound(block.getProperty().getSounds().put(index), this.atPosition(wx, wy));
    }

    /**
     * 播放方块类（方块 / 墙体 / 附着物）的破坏音效
     * @param block 被破坏的方块类
     * */
    public SpatialAudio playDestroySound (Block block, float wx, float wy) {
        return this.playSpatialSound(block.getProperty().getSounds().destroy(), this.atPosition(wx, wy));
    }

    /**
     * 播放指定下标的破坏音效
     * @param index 破坏音效组内的下标
     * */
    public SpatialAudio playDestroySound (Block block, float wx, float wy, int index) {
        return this.playSpatialSound(block.getProperty().getSounds().destroy(index), this.atPosition(wx, wy));
    }

    /**
     * 一次性世界坐标音源（每次的破坏/放置位置都不同，不复用）
     * */
    private SpatialAudioSource atPosition (float wx, float wy) {
        Vector3 pos = new Vector3(wx, wy, 0);
        return () -> pos;
    }

    /**
     * 静音当前的走路音效（不停止播放，避免引擎 dispose）
     * */
    public void muteWalkSound () {
        if (this.curWalkAudioInstance != null) {
            this.curWalkAudioInstance.setVolume(0f);
            this.curWalkAudioInstance = null;
        }
    }

    /**
     * 敌人音效
     * */
    private void updateEnemySoundEffect (float delta) {

    }

    /**
     * 环境音效
     * */
    private void updateEnvironmentalEffects (float delta) {

    }

    /**
     * 由实体来播放一个空间音效
     * @param audioHolder 音效的注册类
     * @param sounderEntity 发出声音的实体
     * */
    public SpatialAudio playSpatialSound (AudioHolder audioHolder, Entity<?> sounderEntity) {
        return this.playSpatialSound(audioHolder, sounderEntity.getSounder());
    }

    /**
     * 由方块实体来播放一个空间音效
     * @param audioHolder 音效的注册类
     * @param blockEntity 发出声音的方块实体
     * */
    public SpatialAudio playSpatialSound (AudioHolder audioHolder, BlockEntity blockEntity) {
        return this.playSpatialSound(audioHolder, blockEntity.getSounder());
    }

    /**
     * 播放一个空间音效
     * @param audioHolder 音效的注册类
     * @param source      声音源
     * */
    public SpatialAudio playSpatialSound (AudioHolder audioHolder, SpatialAudioSource source) {
        SpatialAudio audio = SpatialAudioSystem.getInstance().playAudio(audioHolder, source);
        this.activeSounds.add(audio);
        return audio;
    }

}
