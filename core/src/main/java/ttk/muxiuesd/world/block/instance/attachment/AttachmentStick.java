package ttk.muxiuesd.world.block.instance.attachment;

import ttk.muxiuesd.world.block.abs.Attachment;

/**
 * 木棍附着物
 * <p>
 * 没有每格状态，所以是{@link Attachment}的<b>共享型</b>：全世界所有木棍共用注册时创建的那一个实例，
 * 因此不覆写{@link #createInstance()}（基类默认返回自身）。
 * {@link #createSelf()}只是基类抽象声明的占位，共享型永远不会调用它。
 * <p>
 * 共享型的固有性质：读档时{@link Attachment#CODEC}经{@link Attachment#fromId(String)} ->
 * {@link Attachment#createInstance()}拿到的就是这个原型，随后编解码器会把解出的属性赋回它
 * （发生在区块加载线程）。木棍无状态、每份存档写出的属性完全一致，所以无害；
 * <b>若以后给木棍加上任何每格不同的状态，必须改成每格独享（覆写createInstance），
 * 否则状态会在全世界所有木棍之间串</b>
 * */
public class AttachmentStick extends Attachment {
    /**
     * 区块生成时，每个草方块格生成木棍的概率
     * */
    public static final float SPAWN_CHANCE = 1f / 64f;

    public AttachmentStick () {
        super(createProperty());
    }

    @Override
    protected AttachmentStick createSelf () {
        return new AttachmentStick();
    }
}
