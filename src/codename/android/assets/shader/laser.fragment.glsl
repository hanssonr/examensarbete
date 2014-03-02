#ifdef GL_ES
#define LOWP lowp
#define MED mediump
precision lowp float;
#else
#define LOWP
#define MED
#endif

varying vec4 vColor;
uniform sampler2D u_texture;
varying MED vec2 v_texCoord0;

void main() {
    //gl_FragColor = vColor;
    vec4 color = texture2D(u_texture, v_texCoord0) * vColor;
    // float factor = toonify(max(color.r, max(color.g, color.b)));
    gl_FragColor = color;
}