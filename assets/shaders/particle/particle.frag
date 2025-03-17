#version 330 core

// 输入
in vec4 v_color;
in vec2 v_texCoord;
in float v_glow;

// 输出
out vec4 FragColor;

// 纹理
uniform sampler2D u_texture;
uniform vec3 u_glowColor;      // 发光颜色
uniform float u_intensity; // 全局强度系数

void main() {
    // 采样基础纹理
    vec4 texColor = texture(u_texture, v_texCoord)*v_color;

    // 基础颜色
    vec4 baseColor = texColor * v_color;

    // 计算发光强度（基于Alpha和自定义强度）
    float glowFactor = texColor.a * v_glow * u_intensity;

    // 发光颜色叠加
    vec3 glow = u_glowColor * glowFactor;

    // 最终颜色（保留原始透明度）
    FragColor = vec4(baseColor.rgb + glow, baseColor.a);
}
