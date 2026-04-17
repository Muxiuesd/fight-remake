package ttk.muxiuesd.world.block.abs;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.ICatData;
import ttk.muxiuesd.interfaces.Tickable;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.cat.CatInt;
import ttk.muxiuesd.world.cat.CatsHolder;

/**
 * 植物基础抽象类
 * <p>
 * 姑且算方块，使用方块的逻辑
 * */
public abstract class Botany extends Block implements Tickable, ICatData {
    public static TextureRegion loadTextureRegion (String name) {
        return Util.loadTextureRegion(Fight.ID(name), Fight.BotanyTexturePath("crops/" + name));
    }


    private int growLevel = 0;  //生长等级，每一个生长等级会有不同的贴图
    private TextureRegion[] textureRegions; //不同生长等级的贴图


    public Botany (Property property) {
        super(property);
    }

    /**
     * 生成自己的实例
     * */
    public abstract Botany createSelf ();

    @Override
    public void readCatData (JsonValue values) {
        this.setGrowLevel(values.getInt("growLevel"));
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

    @Override
    public TextureRegion getTextureRegion () {
        return this.getCurGrowLevelTextureRegion();
    }

    /**
     * 获取当前的生长等级对应的贴图材质
     * */
    public TextureRegion getCurGrowLevelTextureRegion () {
        int level = this.getGrowLevel();
        TextureRegion[] regions = this.getTextureRegions();
        if (level > regions.length - 1) {
            //如果生长等级超过对应的贴图，就返回最大的
            return regions[regions.length - 1];
        }
        return regions[level];
    }

    /**
     * 设置每一个生长等级对应的贴图
     * */
    public Botany setGrowLevelTextureRegions (TextureRegion... textureRegion) {
        return this.setTextureRegions(textureRegion);
    }

    public TextureRegion[] getTextureRegions() {
        return this.textureRegions;
    }

    public Botany setTextureRegions (TextureRegion[] textureRegions) {
        this.textureRegions = textureRegions;
        return this;
    }
}
