package ttk.muxiuesd.world.item.consumption;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Attachment;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 农作物类型的物品（种子类）
 * <p>
 * 种植算"物品对方块的交互"：手持对着允许种植的方块按右键种下去（见{@link #useOn}）。
 * <p>
 * 本类不覆写{@link ttk.muxiuesd.world.item.abs.Item#use}，所以左键用手持的种子不会有任何效果、
 * 也不会消耗（默认实现返回false，用不上的时候要静默失败）
 * */
public class CropItem extends ConsumptionItem {
    /// 默认的种植音效
    public static final AudioHolder[] PLANT_SOUNDS = new AudioHolder[] {
        Sounds.CROP_PLANT_1, Sounds.CROP_PLANT_2, Sounds.CROP_PLANT_3,
        Sounds.CROP_PLANT_4, Sounds.CROP_PLANT_5, Sounds.CROP_PLANT_6,
    };

    private Attachment attachment;

    public CropItem (Attachment attachment, AudioHolder[] plantSounds) {
        super();
        this.attachment = attachment;
        setConsumeSounds(plantSounds);
    }


    /**
     * 对着目标位置种植
     * @param targetPos 鼠标指向的世界坐标
     * */
    @Override
    public boolean useOn (ItemStack itemStack, World world, LivingEntity<?> user, Vector2 targetPos) {
        ChunkSystem chunkSystem = world.getSystem(ChunkSystem.class);
        //能不能种在这里由放置规则决定（见 AttachmentPlacements），物品这边不再硬编码耕地
        //放置失败就不算使用成功，不消耗物品的数量
        boolean result = chunkSystem.placeAttachment(this.getAttachment(), targetPos.x, targetPos.y);

        if (result) playUseSound(world, user);

        return result;
    }

    public Attachment getAttachment () {
        return this.attachment;
    }

    public CropItem setAttachment (Attachment attachment) {
        this.attachment = attachment;
        return this;
    }
}
