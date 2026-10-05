package ttk.muxiuesd.world.block.abs;

import ttk.muxiuesd.interfaces.ICatData;
import ttk.muxiuesd.interfaces.Tickable;
import ttk.muxiuesd.world.cat.CatInt;
import ttk.muxiuesd.world.cat.CatsHolder;

/**
 * 植物
 * <p>
 * 附着物中的一种，有自己的状态数据（生长等级），所以是每格独享实例
 * <p>
 * 编解码器继承自{@link Attachment#CODEC}，本类不再单独持有一个
 * <p>
 * 不同生长等级的贴图由植物渲染器持有（见 Blocks.registerBotany）
 * */
public abstract class Botany extends Attachment implements Tickable, ICatData {
    private int growLevel = 0;  //生长等级，每一个生长等级会有不同的贴图


    public Botany (Property property) {
        super(property);
    }

    /**
     * 植物是每格独享实例，覆写此方法产生自己的副本
     * */
    @Override
    public Botany createInstance () {
        Botany instance = this.createSelf();
        //把原型上的配置复制给新的实例
        instance
            .setDroppedItem(this.getDroppedItem())
            .setIdentifier(this.getIdentifier());
        return instance;
    }

    /**
     * 生成自己的实例
     * <p>
     * 这里把返回类型收窄成{@link Botany}，子类返回自己的类型就行，调用方不需要强转
     * */
    @Override
    protected abstract Botany createSelf ();

    @Override
    public void readCatData (CatsHolder holder) {
        this.setGrowLevel(holder.getInt("growLevel", 0));
    }

    @Override
    public void writeCatData (CatsHolder holder) {
        holder.put("growLevel", new CatInt(this.getGrowLevel()));
    }

    /**
     * 生长等级增加某一值
     * */
    public Botany growLevelIncrease (int value) {
        this.setGrowLevel(this.getGrowLevel() + value);
        return this;
    }

    public int getGrowLevel () {
        return this.growLevel;
    }

    public Botany setGrowLevel (int growLevel) {
        if (growLevel >=0 ) this.growLevel = growLevel;
        return this;
    }
}
