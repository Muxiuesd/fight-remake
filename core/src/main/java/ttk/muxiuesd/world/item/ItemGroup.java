package ttk.muxiuesd.world.item;

import ttk.muxiuesd.world.item.abs.Item;

import java.util.ArrayList;

/**
 * 物品组
 * */
public class ItemGroup {
    private final String groupID;
    private final ArrayList<ItemStack> itemsList;

    public ItemGroup (String groupID) {
        this.groupID = groupID;
        this.itemsList = new ArrayList<>();
    }

    /**
     * 添加一个物品
     * */
    public ItemGroup add (Item item) {
        return this.add(new ItemStack(item));
    }
    public ItemGroup add (ItemStack itemStack) {
        if (!this.getItemsList().contains(itemStack)) {
            this.getItemsList().add(itemStack);
        }
        return this;
    }

    public ItemStack get (int index) {
        if (this.getItemsList().size() > index) {
            return this.getItemsList().get(index);
        }
        return null;
    }

    public String getGroupID () {
        return this.groupID;
    }

    public ArrayList<ItemStack> getItemsList() {
        return this.itemsList;
    }
}
