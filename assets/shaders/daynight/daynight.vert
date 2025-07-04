#version 330 core

in vec4 a_color;//传入顶点颜色

layout(location = 0) in vec2 a_position;
layout(location = 1) in vec2 a_texCoord0;


out vec4 v_color;//传给片段着色器颜色
out vec2 v_texCoord;

out vec2 v_position2d;//传递位置的插值给片段着色器

uniform mat4 u_projTrans;


void main() {
    v_texCoord = a_texCoord0;
    v_color=a_color;
    v_position2d=a_position;
    gl_Position = u_projTrans * vec4(a_position, 0.0, 1.0);
}

