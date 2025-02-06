
World.event.add("entityAttacked", function (attackObject, victim) {
    //Log.print("攻击物：" + attackObject + "，受攻击者：" + victim);
});
World.event.add("keyInput", function (key) {
    if (key === Keys.P) {
        Log.print("Key is: P");
    }
    Log.print("Key: " + key);
});
World.event.add("buttonInput", function (screenX, screenY, pointer, button) {
    if (button === Buttons.LEFT) {
        Log.print("Button is: 左键");
    }
    Log.print("Button: " + button + "坐标：(" + screenX + "," + screenY + ")");
});
