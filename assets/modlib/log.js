var Logger = Java.type("ttk.muxiuesd.util.Log");
var Log = {
    print: function (tag, msg) {
        Logger.print(tag, msg);
    },
    error: function (tag, msg) {
        Logger.error(tag, msg);
    }
}
