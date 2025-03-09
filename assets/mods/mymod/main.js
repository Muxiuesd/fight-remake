var TAG = "Muxiuesd的mod";
var namespace = "mymod";

World.event.add("bulletShoot", function (shooter, bullet) {
    var group = getGroup(shooter);
    //Log.print(group + "射出子弹！");
});
World.event.add("entityDead", function (deadEntity) {
    var group = getGroup(deadEntity);
    //Log.print(group + "死亡!");
});
World.event.add("buttonInput", function (screenX, screenY, pointer, button) {
    if (button === Buttons.RIGHT) {
        Log.print(TAG, "Button is: 右键");
    }
    Log.print(TAG, "Button: " + button + "坐标：(" + screenX + "," + screenY + ")");
});
World.event.add("worldTick", function (world, delta) {
    //Log.print(TAG, "世界："+ world +" 调用tick，间隔时间：" + delta);
});

loadFile(namespace, "mymod:sand", "assets/sand.png", Texture.class);
