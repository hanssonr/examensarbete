#ifdef GL_ES
#define LOWP lowp
#define MED mediump
precision lowp float;
#else
#define LOWP
#define MED
#endif

uniform sampler2D u_texture;
varying MED vec2 v_texCoords0;
varying MED vec2 v_texCoords1;
varying MED vec2 v_texCoords2;
varying MED vec2 v_texCoords3;
varying MED vec2 v_texCoords4;
varying MED vec2 v_texCoords5;
varying MED vec2 v_texCoords6;
varying MED vec2 v_texCoords7;
varying MED vec2 v_texCoords8;
void main()
{
    float MED alpha = (texture2D(u_texture, v_texCoords0).a
        + texture2D(u_texture, v_texCoords1).a
        + texture2D(u_texture, v_texCoords2).a
    + texture2D(u_texture, v_texCoords3).a
    + texture2D(u_texture, v_texCoords4).a
    + texture2D(u_texture, v_texCoords5).a
    + texture2D(u_texture, v_texCoords6).a
    + texture2D(u_texture, v_texCoords7).a
    + texture2D(u_texture, v_texCoords8).a) / 9.0;
    if(alpha > 0.4){
        gl_FragColor = vec4(0.0,0.0,0.0, 1.0);
    }
    else{
        gl_FragColor = vec4(1.0,1.0,1.0,0.0);
    }
}