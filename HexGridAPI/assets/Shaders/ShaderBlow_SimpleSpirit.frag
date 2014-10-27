varying vec2 texCoordAni;
uniform sampler2D m_AniTexMap;

#ifdef MULTIPLY_COLOR
    uniform vec4 m_Multiply_Color;
#endif

void main(){

vec4 AniTex = texture2D(m_AniTexMap, vec2(texCoordAni));

#ifdef MULTIPLY_COLOR
    gl_FragColor.rgb = m_Multiply_Color.rgb * AniTex.rgb;
#else
    gl_FragColor.rgb = AniTex.rgb;
#endif

gl_FragColor.a = AniTex.a;

}