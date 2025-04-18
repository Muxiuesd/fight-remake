var TAG = "测试mod";
var namespace = "testmod";

var fileLoader = File.getFileLoader(namespace);

fileLoader.load(
    "testmod:grass",
    "assets/grass.png",
    Texture.class,
    new FileLoadCallback(function (file) {
        Log.print(TAG, "资源" + file + "加载完成！");
    })
);

//方块注册
var blockReg = ModRegistrant.getBlockRegister(namespace);
blockReg.register("test_block", Blocks.newSupplier(function () {
    return Blocks.newBlock(Blocks.properties.newProperty().setFriction(1.0), "testmod:grass");
}));

var audioRegister = Audio.getRegister(namespace);
audioRegister.registerSoundAsMusic("testmod:ignite", "assets/audio/sound/ignite.ogg");

fileLoader.load(
    "testmod:sword",
    "assets/items/wooden_sword.png",
    Texture.class,
    new FileLoadCallback(function (file) {
        Log.print(TAG, "资源" + file + "加载完成！");
    })
);
//物品注册
var itemRegister = ModRegistrant.getItemRegister(namespace);
itemRegister.register("sword", Items.newSupplier(function () {
    return Items.newItem(Items.types.COMMON, Items.properties.newProperty(), "testmod:sword");
}));

//实体注册
var entityRegister = ModRegistrant.getEntityRegister(namespace);


World.event.add("entityAttacked", function (attackObject, victim) {
    Log.print(TAG, "攻击物：" + attackObject + "，受攻击者：" + victim);
});
World.event.add("keyInput", function (key) {
    if (key === Keys.P) {
        Log.print(TAG, "Key is: P");
        var playerSystem = World.systems.get("PlayerSystem");
        playerSystem.setItemStack(4, "testmod:sword");
    }
    Log.print(TAG, "Key: " + key);
});
World.event.add("buttonInput", function (screenX, screenY, pointer, button) {
    if (button === Buttons.LEFT) {
        Log.print(TAG, "Button is: 左键");
    }
    Log.print(TAG, "Button: " + button + "坐标：(" + screenX + "," + screenY + ")");
});

Library.export("Fun", {
    count: 0,
    say: function () {
        Log.print(TAG, "Hello other Mod!" + this.count);
        this.count++;
    }
});

var bFun = Library.import("bFun");
bFun.call();
