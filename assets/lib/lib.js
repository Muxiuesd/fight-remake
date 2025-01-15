var system = Java.type("java.lang.System")
var System = {
    log: function (msg) {
        system.out.println(msg);
    },
    error: function (msg) {
        system.err.println(msg);
    }
}

