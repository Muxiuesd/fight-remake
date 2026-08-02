package ttk.muxiuesd.serialization.codecs;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.DataResult;
import game.muxiuesd.bedrockcore.serialization.RawObject;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.entity.abs.StatusEffect;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 活物实体状态效果的现代化编解码器
 * <p>
 * 格式：{ "状态效果id": { "duration": 持续时间, "level": 等级 }, ... }
 */
public class CodecStatusEffects {
    public static final Codec<LinkedHashMap<StatusEffect, StatusEffect.Data>> CODEC = new Codec<>() {
        @Override
        public RawObject encode (LinkedHashMap<StatusEffect, StatusEffect.Data> effects) {
            Map<String, Object> map = new LinkedHashMap<>();
            effects.forEach((effect, data) -> {
                Map<String, Object> dataMap = new LinkedHashMap<>();
                dataMap.put("duration", Codec.FLOAT.encode(data.getDuration()).unwrap());
                dataMap.put("level", Codec.INT.encode(data.getLevel()).unwrap());
                map.put(effect.getId(), dataMap);
            });
            return RawObject.ofMap(map);
        }

        @Override
        public DataResult<LinkedHashMap<StatusEffect, StatusEffect.Data>> decode (RawObject input) {
            if (!input.isMap()) return DataResult.error("Expected a map");

            LinkedHashMap<StatusEffect, StatusEffect.Data> result = new LinkedHashMap<>();
            StringBuilder errors = new StringBuilder();
            boolean hasError = false;

            for (Map.Entry<String, Object> rawEntry : input.asMap().get().entrySet()) {
                String id = rawEntry.getKey();
                if (Registries.STATUS_EFFECT.contains(id)) {
                    StatusEffect effect = Registries.STATUS_EFFECT.get(id);
                    RawObject dataObj = Codec.wrap(rawEntry.getValue());
                    if (dataObj.isMap()) {
                        Map<String, Object> dataMap = dataObj.asMap().get();
                        float duration = Codec.FLOAT.decode(Codec.wrap(dataMap.get("duration"))).result().orElse(0f);
                        int level = Codec.INT.decode(Codec.wrap(dataMap.get("level"))).result().orElse(0);
                        result.put(effect, new StatusEffect.Data(duration, level));
                    }
                }else {
                    hasError = true;
                    errors.append("有不存在的状态效果ID：").append(id).append("；");
                }
            }

            if (hasError) return DataResult.error(errors.toString(), result);
            return DataResult.success(result);
        }
    };
}
