#ifdef GL_ES
#define MED mediump
#else
#define MED
#endif

attribute vec4 a_position;
attribute vec2 a_texCoord0;
uniform vec2 size;
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
    v_texCoords0 = a_texCoord0 + vec2(0.0 / size.x, -1.0 / size.y);
    v_texCoords1 = a_texCoord0 + vec2(-1.0 / size.x, 0.0 / size.y);
    v_texCoords2 = a_texCoord0 + vec2(0.0 / size.x, 0.0 / size.y);
    v_texCoords3 = a_texCoord0 + vec2(1.0 / size.x, 0.0 / size.y);
    v_texCoords4 = a_texCoord0 + vec2(0.0 / size.x, 1.0 / size.y);
    v_texCoords5 = a_texCoord0 + vec2(-1.0 / size.x, -1.0 / size.y);
    v_texCoords6 = a_texCoord0 + vec2(-1.0 / size.x, 1.0 / size.y);
    v_texCoords7 = a_texCoord0 + vec2(1.0 / size.x, -1.0 / size.y);
    v_texCoords8 = a_texCoord0 + vec2(1.0 / size.x, 1.0 / size.y);
    gl_Position = a_position;
}