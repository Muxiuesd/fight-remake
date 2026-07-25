package ttk.muxiuesd.world.block;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.Codecable;
import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.Sounds;

/**
 * 方块音效类
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
    private AudioHolder[] audioHolders;   //各种音效持有

    public BlockSounds (AudioHolder walk, AudioHolder put, AudioHolder destroy) {
        this(new AudioHolder[]{walk, put, destroy});
    }

    public BlockSounds (AudioHolder[] audioHolders) {
        this.audioHolders = audioHolders;
    }

    public BlockSounds () {
    }

    public AudioHolder walk () {
        return audioHolders[Type.WALK.num];
    }

    public AudioHolder put () {
        return audioHolders[Type.PUT.num];
    }

    public AudioHolder destroy () {
        return audioHolders[Type.DESTROY.num];
    }

    /**
     * 获取对应类型的音效id
     * */
    public String getTypeID (Type type) {
        return this.audioHolders[type.num].getID();
    }

    /**
     * 获取这个方块音效集合的id
     * */
    public String getID () {
        return this.identifier.getID();
    }

    public BlockSounds setIdentifier (Identifier identifier) {
        this.identifier = identifier;
        return this;
    }

    public BlockSounds setAudioHolders (AudioHolder[] audioHolders) {
        this.audioHolders = audioHolders;
        return this;
    }

    @Override
    public Codec<BlockSounds> getCodec () {
        return CODEC;
    }
}
