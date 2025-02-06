
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
        Log.print("Button is: 右键");
    }
    Log.print("Button: " + button + "坐标：(" + screenX + "," + screenY + ")");
});
