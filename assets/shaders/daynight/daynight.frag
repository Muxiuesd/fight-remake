#version 330 core

in vec2 v_texCoord;
in vec4 v_color;//从顶点着色器传入的纹理颜色
in vec2 v_position2d;//传入位置

out vec4 fragColor;

uniform sampler2D u_texture;
uniform float u_time; // 0.0(午夜) ~ 1.0(次日午夜)

layout(std140) uniform LightBlock {
    vec4 u_light[200];
    vec4 u_lightColor[200];
    int u_lighsize;
};

// 定义昼夜颜色配置
#define DAY_COLOR vec3(1.0, 0.98, 0.9)    // 浅黄色（白天）
#define NIGHT_COLOR vec3(0.2, 0.3, 0.8)   // 深蓝色（夜晚）
#define DUSK_COLOR vec3(0.8, 0.4, 0.2)    // 黄昏橙红色

void main() {
    // 基础纹理颜色
    vec4 initialTexColor=texture(u_texture, v_texCoord)*v_color;//乘以顶点颜色
    vec4 texColor = initialTexColor;

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


    //发光光源
    vec3 sumLightColor =vec3(0.0);
    for (int i = 0; i < u_lighsize; i++) {
        vec2 lightPos = u_light[i].xy;
        float lightIntensity = u_light[i].z;  // 光源强度

        //计算片段到光源的距离
        float distance = distance(v_position2d, lightPos);
        float attenuation = 1.0 / (1.0 + 10.0* distance*distance);  // 衰减因子
        //计算光照贡献
        vec3 lightColor2 = u_lightColor[i].rgb * lightIntensity * attenuation;
        sumLightColor += lightColor2;
    }

    // 最终颜色混合
    vec3 finalColor=clamp(texColor.rgb * lightColor * brightness+sumLightColor,vec3(0.0),initialTexColor.rgb+0.1*(sumLightColor));
    finalColor=clamp(finalColor,0.0,1.0);//限制颜色在0~1之间，避免可能产生未定义行为
    fragColor = vec4(finalColor, texColor.a);
}

