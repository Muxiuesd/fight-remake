#version 330 core

in vec2 v_texCoord;
in vec4 v_color;//从顶点着色器传入的纹理颜色
in vec2 v_position2d;//传入位置

out vec4 fragColor;

uniform sampler2D u_texture;

void main() {
    // 基础纹理颜色
    vec4 initialTexColor=texture(u_texture, v_texCoord)*v_color;//乘以顶点颜色
    fragColor = initialTexColor;
}

