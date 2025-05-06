var TAG = "Muxiuesd的mod";
var namespace = "mymod";

World.event.add("bulletShoot", function (world, shooter, bullet) {
    var group = Entity.getGroup(shooter);
    if (group === "Enemy") {
        var soundEffectSystem = World.systems.get("SoundEffectSystem");
        soundEffectSystem.newSpatialSound("testmod:ignite", shooter);
    }
});
World.event.add("entityDead", function (world, deadEntity) {
    var group = Entity.getGroup(deadEntity);

});
World.event.add("buttonInput", function (world, screenX, screenY, pointer, button) {
    if (button === Buttons.RIGHT) {
        Log.print(TAG, "Button is: 右键");
    }
    //Log.print(TAG, "Button: " + button + "坐标：(" + screenX + "," + screenY + ")");
});
World.event.add("worldTick", function (world, delta) {
    //Audio.getPlayer().playMusic("testmod:ignite");
});

File.loadFile(namespace, "mymod:sand", "assets/sand.png", Texture.class);

var fun = Library.import("Fun");
fun.say();
