var ModAudioRegister = Java.type("ttk.muxiuesd.mod.api.ModAudioRegister");

var Audio = {
    getRegister: function (namespace) {
        return ModAudioRegister.getRegister(namespace);
    }
}
