#version 330 core
in vec2 v_texCoord;
out vec4 fragColor;

uniform sampler2D u_texture;
uniform vec3 u_grassColor; // 控制主颜色 (RGB格式)
uniform float u_brightness; // 亮度调节

void main() {
    vec4 texColor = texture(u_texture, v_texCoord);

    // 颜色混合公式（保留纹理细节）
    vec3 finalColor = texColor.rgb * u_grassColor * u_brightness;

    // 保留Alpha通道
    fragColor = vec4(finalColor, texColor.a);
}
