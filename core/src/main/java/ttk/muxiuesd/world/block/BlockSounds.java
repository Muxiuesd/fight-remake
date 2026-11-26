package ttk.muxiuesd.world.block;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.Codecable;
import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.audio.SoundGroup;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.Sounds;

/**
 * 方块音效类
 * <p>
 * 每种音效（走、放、破）持有一个 {@link SoundGroup}，组内可以有多个音效；
 * 播放时既可以按下标指定某一首，也可以随机取一首
 * */
public class BlockSounds implements Codecable<BlockSounds> {
    public static final Codec<BlockSounds> CODEC = Codec.STRING.xmap(
        Registries.BLOCK_SOUNDS::get,
        BlockSounds::getID
    );

    //默认都为石头的音效
    public static final BlockSounds DEFAULT = Sounds.STONE;

    public enum Type {
        WALK(0),
        PUT(1),
        DESTROY(2)
        ;

        final int num;
        Type (int num) {
            this.num = num;
        }
    }
    private Identifier identifier;
    private SoundGroup[] groups;   //各种音效持有，下标对应 Type.num

    public BlockSounds (SoundGroup walk, SoundGroup put, SoundGroup destroy) {
        this(new SoundGroup[]{walk, put, destroy});
    }

    public BlockSounds (SoundGroup[] groups) {
        if (groups == null || groups.length != Type.values().length) {
            throw new IllegalArgumentException("方块音效组数量必须是 " + Type.values().length);
        }
        for (Type type : Type.values()) {
            if (groups[type.num] == null) {
                throw new IllegalArgumentException("方块音效组缺失：" + type);
            }
        }
        this.groups = groups;
    }

    /**
     * 每种音效只有一个时的便捷构造
     * */
    public BlockSounds (AudioHolder walk, AudioHolder put, AudioHolder destroy) {
        this(new SoundGroup(walk), new SoundGroup(put), new SoundGroup(destroy));
    }

    /**
     * 获取对应类型的音效组
     * */
    public SoundGroup getGroup (Type type) {
        return this.groups[type.num];
    }

    public SoundGroup getWalkGroup () {
        return this.getGroup(Type.WALK);
    }

    public SoundGroup getPutGroup () {
        return this.getGroup(Type.PUT);
    }

    public SoundGroup getDestroyGroup () {
        return this.getGroup(Type.DESTROY);
    }

    /**
     * 随机取对应类型的一首音效
     * */
    public AudioHolder random (Type type) {
        return this.getGroup(type).random();
    }

    /**
     * 取对应类型的第 index 首音效
     * */
    public AudioHolder get (Type type, int index) {
        return this.getGroup(type).get(index);
    }

    public AudioHolder walk () {
        return this.getWalkGroup().random();
    }

    public AudioHolder walk (int index) {
        return this.getWalkGroup().get(index);
    }

    public AudioHolder put () {
        return this.getPutGroup().random();
    }

    public AudioHolder put (int index) {
        return this.getPutGroup().get(index);
    }

    public AudioHolder destroy () {
        return this.getDestroyGroup().random();
    }

    public AudioHolder destroy (int index) {
        return this.getDestroyGroup().get(index);
    }

    /**
     * 获取对应类型第一首音效的id
     * <p>
     * 只给还在用旧模式的 SoundEffectSystem 留的接口，新代码请直接用 {@link #getGroup(Type)}
     * */
    public String getTypeID (Type type) {
        return this.getGroup(type).get(0).getID();
    }

    /**
     * 获取这个方块音效集合的id
     * */
    public String getID () {
        return this.identifier.getID();
    }

    public BlockSounds setIdentifier (Identifier identifier) {
        //Identifier 只在注册阶段给定，注册过后不允许修改
        if (this.identifier != null && !this.identifier.equals(identifier)) {
            throw new IllegalStateException("Identifier 已设置，禁止修改！方块音效：" + this.identifier.getID() + " -> " + identifier.getID());
        }
        this.identifier = identifier;
        return this;
    }

    public BlockSounds setGroups (SoundGroup[] groups) {
        this.groups = groups;
        return this;
    }

    @Override
    public Codec<BlockSounds> getCodec () {
        return CODEC;
    }
}
