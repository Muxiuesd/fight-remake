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


loadFile(namespace, "mymod:sand", "assets/sand.png", Texture.class);
