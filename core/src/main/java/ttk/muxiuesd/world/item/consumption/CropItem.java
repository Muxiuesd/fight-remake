package ttk.muxiuesd.world.item.consumption;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Attachment;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 农作物类型的物品
 * <p>
 * 对着允许种植的地面方块使用可以种植，或者可以直接食用
 * */
public class CropItem extends ConsumptionItem {
    private Attachment attachment;

    public CropItem (Attachment attachment) {
        super();
        this.attachment = attachment;
    }

    public CropItem (Property property) {
        super(property);
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity<?> user) {
        Vector2 mouseWorldPosition = Util.getMouseWorldPosition();
        ChunkSystem chunkSystem = world.getSystem(ChunkSystem.class);
        //能不能种在这里由放置规则决定（见 AttachmentPlacements），物品这边不再硬编码耕地
        //放置失败就不算使用成功，不消耗物品
        if (!chunkSystem.placeAttachment(this.getAttachment(), mouseWorldPosition.x, mouseWorldPosition.y)) {
            return false;
        }
        return super.use(itemStack, world, user);
    }

    public Attachment getAttachment () {
        return this.attachment;
    }

    public CropItem setAttachment (Attachment attachment) {
        this.attachment = attachment;
        return this;
    }
}
