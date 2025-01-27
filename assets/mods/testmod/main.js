System.log("testmod");

World.event.add("entityAttacked", function (attackObject, victim) {
    //Log.print("攻击物：" + attackObject + "，受攻击者：" + victim);
});
