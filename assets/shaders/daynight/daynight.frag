#version 330 core

in vec2 v_texCoord;
out vec4 fragColor;

uniform sampler2D u_texture;
uniform float u_time; // 0.0(午夜) ~ 1.0(次日午夜)

// 定义昼夜颜色配置
#define DAY_COLOR vec3(1.0, 0.98, 0.9)    // 浅黄色（白天）
#define NIGHT_COLOR vec3(0.2, 0.3, 0.8)   // 深蓝色（夜晚）
#define DUSK_COLOR vec3(0.8, 0.4, 0.2)    // 黄昏橙红色

void main() {
    // 基础纹理颜色
    vec4 texColor = texture(u_texture, v_texCoord);

    // 计算时间曲线（平滑过渡）
    float timeCurve = sin(u_time * 3.14159265 * 2.0) * 0.5 + 0.5;

    // 混合昼夜颜色
    vec3 lightColor = mix(NIGHT_COLOR, DAY_COLOR, smoothstep(0.2, 0.8, timeCurve));

    // 添加黄昏过渡
    if(timeCurve > 0.4 && timeCurve < 0.6) {
        float duskFactor = abs(timeCurve - 0.5) * 10.0;
        lightColor = mix(lightColor, DUSK_COLOR, 1.0 - duskFactor);
    }

    // 亮度调整（夜晚更暗）
    float brightness = mix(0.6, 1.2, smoothstep(0.3, 0.7, timeCurve));

    // 最终颜色混合
    fragColor = vec4(texColor.rgb * lightColor * brightness, texColor.a);
}
