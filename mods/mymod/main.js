System.log("My Mod!!!");

World.event.add("bulletShoot", function (shooter, bullet) {
    var group = getGroup(shooter);
    System.log(group + "射出子弹！");
});
