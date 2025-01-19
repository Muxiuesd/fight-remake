System.log("My Mod!!!");

World.event.add("bulletShoot", function (shooter, bullet) {
    var group = getGroup(shooter);
    Log.print(group + "射出子弹！");
});
World.event.add("entityDead", function (deadEntity) {
    var group = getGroup(deadEntity);
    Log.print(group + "死亡!");
});
