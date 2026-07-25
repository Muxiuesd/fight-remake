package ttk.muxiuesd.system;

import game.muxiuesd.bedrockcore.serialization.DataResult;
import game.muxiuesd.bedrockcore.serialization.RawObject;
import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 测试系统
 * */
public class TestSystem extends WorldSystem {
    public TestSystem (World world) {
        super(world);
    }

    @Override
    public void update (float delta) {
        super.update(delta);

        if (KeyBindings.PlayerChangeItem.wasJustPressed()) {
            Player player = getWorld().getSystem(PlayerSystem.class).getPlayer();
            ItemStack handItemStack = player.getHandItemStack();
            RawObject rawObject = ItemStack.CODEC.encode(handItemStack);
            DataResult<ItemStack> result = ItemStack.CODEC.decode(rawObject);
            if (result.isSuccess()) {
                ItemStack itemStack = result.result().get();
                itemStack.setAmount(32);
                player.getBackpack().addItem(itemStack);
                Log.print(TAG(), "物品解码成功！");
            }else {
                Log.error(TAG(), "物品解码失败！");
            }
        }
    }
}
