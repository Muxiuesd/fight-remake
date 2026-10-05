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

    /**
     * 生长等级增加某一值，但是不会超过生长等级上限
     * <p>
     * 骨粉之类的催熟手段用这个方法，免得把生长等级顶到上限之外
     * @return 等级有变化返回true，已经到上限返回false
     * */
    public boolean growLevelIncreaseUpToMax (int value) {
        if (this.isFullyGrown()) return false;
        //加完之后再夹到上限，避免一次加多级时冲过头
        int level = Math.min(this.getGrowLevel() + value, this.maxGrowLevel());
        this.setGrowLevel(level);
        return true;
    }

    /**
     * 是否已经长到最高等级
     * */
    public boolean isFullyGrown () {
        return this.getGrowLevel() >= this.maxGrowLevel();
    }

    /**
     * 此植物的生长等级上限
     * <p>
     * 每个植物必须自己声明：上限应当等于"生长阶段贴图张数 - 1"（见{@code Blocks.registerBotany}）。
     * <p>
     * 上限与贴图张数不匹配也不会出问题：渲染那边按{@code Math.min(等级, 贴图数 - 1)}取图，
     * 等级超出就一直是最后一张
     * */
    public abstract int maxGrowLevel ();

    public int getGrowLevel () {
        return this.growLevel;
    }

    public Botany setGrowLevel (int growLevel) {
        if (growLevel >=0 ) this.growLevel = growLevel;
        return this;
    }
}
