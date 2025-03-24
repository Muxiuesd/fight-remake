var TAG = "测试mod";

var fileLoader = File.getFileLoader("testmod");

fileLoader.load(
    "testmod:grass",
    "assets/grass.png",
    Texture.class,
    new FileLoadCallback(function (file) {
        Log.print(TAG, "资源" + file + "加载完成！");
    })
);

var blockReg = ModRegistrant.getBlockRegister("testmod");

blockReg.register("test_block", newBlockSupplier(function () {
    return newBlock(new Property().setFriction(1.0), "testmod:grass");
}));

World.event.add("entityAttacked", function (attackObject, victim) {
    Log.print(TAG, "攻击物：" + attackObject + "，受攻击者：" + victim);
});
World.event.add("keyInput", function (key) {
    if (key === Keys.P) {
        Log.print(TAG, "Key is: P");
    }
    Log.print(TAG, "Key: " + key);
});
World.event.add("buttonInput", function (screenX, screenY, pointer, button) {
    if (button === Buttons.LEFT) {
        Log.print(TAG, "Button is: 左键");
    }
    Log.print(TAG, "Button: " + button + "坐标：(" + screenX + "," + screenY + ")");
});

