#ifdef GL_ES
#define MED mediump
#else
#define MED
#endif

attribute vec3 a_position;
attribute vec4 a_color;
uniform mat4 u_projTrans;
varying vec4 vColor;
attribute vec2 a_texCoord0;
varying MED vec2 v_texCoord0;

void main() {
    v_texCoord0 = a_texCoord0;
    vColor = a_color;
    gl_Position =  u_projTrans * vec4(a_position.xyz, 1.0);
}