#version 330 core

// 顶点属性
layout(location = 0) in vec2 a_position;    // 位置
layout(location = 1) in vec4 a_color;       // 颜色
layout(location = 2) in vec2 a_texCoord0;   // 纹理坐标
layout(location = 3) in float a_glow;       // 发光强度

// 输出到片段着色器
out vec4 v_color;
out vec2 v_texCoord;
out float v_glow;

// 矩阵
uniform mat4 u_projTrans;

void main() {
    v_color = a_color;
    v_texCoord = a_texCoord0;
    v_glow = a_glow;

    gl_Position = u_projTrans * vec4(a_position, 0.0, 1.0);
}
