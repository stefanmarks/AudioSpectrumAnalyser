package gui;

import analyser.SpectrumInfo;
import java.awt.Color;

/**
 * Rainbow colour map.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 12.05.2013: Created
 */
public class RainbowColourMap implements ColourMap
{
    public static final RainbowColourMap INSTANCE = new RainbowColourMap();
    
    private RainbowColourMap()
    {
        map = null;
    }
    
    @Override
    public Color getColor(SpectrumInfo info, int frequencyIdx)
    {
        if ( map == null ) 
        {
            initColourMap();
        }
        float intensity = info.intensity[frequencyIdx];
        int idx = (int) (intensity * map.length);
        if ( idx < 0           ) { idx = 0; }
        if ( idx >= map.length ) { idx = map.length - 1; }
        return map[idx];
    }
    
    private void initColourMap()
    {
        map = new Color[256];
        for ( int i = 0 ; i < map.length ; i++ )
        {
            float f = (float) i / map.length;
            map[i] = Color.getHSBColor(0.70f - 0.70f * f, 1, 0.5f + 0.5f * f);
        }
    }
        
    @Override
    public String toString()
    {
        return "Rainbow Colour Map";
    }
    
    private Color[] map;    
}
