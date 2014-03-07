uniform vec3 u_lightDir;
varying vec3 v_normal;
uniform sampler2D u_texture;
varying vec2 v_texCoord0;

float toonify(in float intensity) {
    float amount = 0;

    if (intensity > 0.8)
        amount = 1.2;
    else if (intensity > 0.5)
        amount = 0.9;
    else if (intensity > 0.3)
        amount = 0.8;
    else
        amount = 0.8;

    return amount;
}

void main(){
    vec4 color = texture2D(u_texture, v_texCoord0);
    float factor = toonify(max(color.r, max(color.g, color.b)));
    gl_FragColor = vec4(factor*color.rgb, color.a);
}