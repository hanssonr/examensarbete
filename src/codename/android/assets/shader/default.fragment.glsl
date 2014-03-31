#ifdef GL_ES
#define LOWP lowp
#define MED mediump
#else
#define LOWP
#define MED
#endif

uniform sampler2D u_texture;
varying vec2 v_texCoord0;

void main(){
    vec4 color = texture2D(u_texture, v_texCoord0);
    gl_FragColor = color;
}