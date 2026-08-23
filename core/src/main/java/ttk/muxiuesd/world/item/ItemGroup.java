package ttk.muxiuesd.world.item;

import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.world.item.abs.Item;

import java.util.ArrayList;

/**
 * 物品组
 * */
public class ItemGroup {
    private final Identifier identifier;
    private ItemStack iconItemStack;
    private ArrayList<ItemStack> itemsList;


    public ItemGroup (Identifier identifier) {
        this(identifier, ItemStack.VOID);
    }
    public ItemGroup (Identifier identifier, ItemStack iconItemStack) {
        this(identifier, iconItemStack, new ArrayList<>());
    }
    public ItemGroup(Identifier identifier, ItemStack iconItemStack, ArrayList<ItemStack> itemsList) {
        this.identifier = identifier;
        this.iconItemStack = iconItemStack;
        this.itemsList = itemsList;
    }

    public ItemGroup selfAction (Self self) {
        self.action(this);
        return this;
    }

    /**
     * 获取数量
     * */
    public int getItemCount () {
        return this.getItemsList().size();
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
        return ItemStack.VOID;
    }

    public Identifier getIdentifier () {
        return this.identifier;
    }

    public String getGroupID () {
        return this.getIdentifier().getID();
    }

    public ArrayList<ItemStack> getItemsList() {
        return this.itemsList;
    }

    public ItemGroup setItemsList (ArrayList<ItemStack> itemsList) {
        this.itemsList = itemsList;
        return this;
    }

    public ItemStack getIconItemStack () {
        return this.iconItemStack;
    }

    public ItemGroup setIconItemStack (ItemStack iconItemStack) {
        this.iconItemStack = iconItemStack;
        return this;
    }



    @FunctionalInterface
    public interface Self {
        void action (ItemGroup group);
    }

    /**
     * 构造器
     * */
    public static class Builder {
        private Identifier identifier;
        private ItemStack iconItemStack = ItemStack.VOID;

        public Builder () {}

        public Builder setIdentifier (Identifier identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder setIdentifier (String id) {
            this.identifier = Identifier.of(id);
            return this;
        }

        public Builder setIconItemStack (Item item) {
            this.iconItemStack = new ItemStack(item);
            return this;
        }

        public Builder setIconItemStack (ItemStack iconItemStack) {
            this.iconItemStack = iconItemStack;
            return this;
        }

        public ItemGroup build () {
            return new ItemGroup(this.identifier, this.iconItemStack);
        }
    }
}
