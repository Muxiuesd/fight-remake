package ttk.muxiuesd.system;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;
import game.muxiuesd.bedrockcore.serialization.DataResult;
import game.muxiuesd.bedrockcore.serialization.RawObject;
import game.muxiuesd.bedrockcore.serialization.RawObjectJsonConverter;
import game.muxiuesd.bedrockcore.util.Log;
import game.muxiuesd.bedrockcore.util.UnifiedFileUtil;
import ttk.muxiuesd.data.abs.JsonDataOutput;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.instance.BlockFurnace;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.item.ItemStack;

import java.util.Optional;

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

        Player player = getWorld().getSystem(PlayerSystem.class).getPlayer();

        if (player.getHandItemStack() != null && KeyBindings.PlayerShift.wasJustPressed()) {
            ItemStack handItemStack = player.getHandItemStack();
            RawObject rawObject = ItemStack.CODEC.encode(handItemStack);
            String json = RawObjectJsonConverter.toJson(rawObject);


            DataResult<ItemStack> result = ItemStack.CODEC.decode(rawObject);
            if (result.isSuccess()) {
                ItemStack itemStack = result.result().get();
                itemStack.setAmount(1);
                player.getBackpack().addItem(itemStack);
                Log.print(TAG(), "物品解码成功！");

                Log.print(TAG(), "原始物品的json：" + json);
                Log.print(TAG(), "解码物品的json：" + RawObjectJsonConverter.toJson(ItemStack.CODEC.encode(itemStack)));
            }else {
                Log.error(TAG(), "物品解码失败！");
            }

            RawObject rawBackpackObj = Backpack.CODEC.encode(player.getBackpack());
            JsonDataWriter writer = new JsonDataWriter();
            Log.print(TAG(), RawObjectJsonConverter.toJson(writer, rawBackpackObj));
            new JsonDataOutput() {
                @Override
                public void output (JsonDataWriter writer) {
                    Json json = writer.getWriter();
                    String string = json.getWriter().getWriter().toString();
                    UnifiedFileUtil
                        .createFile("A:@test/", "test_backpack.json")
                        .writeString(json.prettyPrint(string), false);
                }
            }.output(writer);
        }

        //方块实体编码测试
        if (KeyBindings.PlayerInteract.wasJustPressed()) {
            Vector2 mouseWorldPosition = Util.getMouseWorldPosition();
            ChunkSystem cs = getWorld().getSystem(ChunkSystem.class);
            Block mouseBlock = cs.getBlock(mouseWorldPosition.x, mouseWorldPosition.y);
            if (mouseBlock instanceof BlockFurnace blockFurnace) {
                //测试从对象编码到json
                RawObject rawObject = BlockFurnace.CODEC.encode(blockFurnace);
                JsonDataWriter writer = new JsonDataWriter();
                Log.print(TAG(), "熔炉方块编码成功！");
                Log.print(TAG(), RawObjectJsonConverter.toJson(writer, rawObject));

                new JsonDataOutput() {
                    @Override
                    public void output (JsonDataWriter writer) {
                        String string = writer.getResult();
                        UnifiedFileUtil
                            .createFile("A:@test/", "test_block_furnace.json")
                            .writeString(writer.getWriter().prettyPrint(string), false);
                    }
                }.output(writer);

                //测试从json解码到对象
                String jsonString = writer.getResult();
                RawObject blockFurnaceRawObj = RawObjectJsonConverter.fromJson(jsonString);
                DataResult<BlockFurnace> furnaceDataResult = BlockFurnace.CODEC.decode(blockFurnaceRawObj);
                if (furnaceDataResult.isSuccess()) {
                    Log.print(TAG(), "熔炉方块解码成功！");
                    Optional<BlockFurnace> optional = furnaceDataResult.result();
                    BlockFurnace bf = optional.get();
                    BlockEntity blockEntity = bf.getBlockEntity();
                    Log.print(TAG(), "熔炉方块：" + bf + " 熔炉方块实体：" + blockEntity);
                }
            }
        }
    }
}
