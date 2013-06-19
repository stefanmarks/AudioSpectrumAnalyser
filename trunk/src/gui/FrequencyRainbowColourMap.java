package gui;

import analyser.SpectrumInfo;
import java.awt.Color;

/**
 * Rainbow colour map based on frequencies.
 * Inspired by Gerbrand van Melle.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 19.06.2013: Created
 */
public class FrequencyRainbowColourMap implements ColourMap
{
    public static final FrequencyRainbowColourMap INSTANCE = new FrequencyRainbowColourMap();
    
    private FrequencyRainbowColourMap()
    {
        map = null;
    }
    
    @Override
    public Color getColor(SpectrumInfo info, int frequencyIdx)
    {
        if ( (map == null) || (map.length != info.intensity.length) ) 
        {
            initColourMap(info.intensity.length);
        }
        float intensity = info.intensity[frequencyIdx];
        int maxIdx = map[0].length;
        int idx = (int) (intensity * maxIdx);
        if ( idx < 0       ) { idx = 0;          }
        if ( idx >= maxIdx ) { idx = maxIdx - 1; }
        return map[frequencyIdx][idx];
    }
    
    private void initColourMap(int bands)
    {
        map = new Color[bands][256];
        for ( int b = 0 ; b < bands ; b++ )
        {
            for ( int i = 0 ; i < map[b].length ; i++ )
            {
                float hue = (float) b / map.length;
                float sat = (float) i / map[b].length;
                sat = 1.0f - Math.abs(1 - 2 * sat);
                float brt = (float) i / map[b].length;
                brt = Math.min(1.0f, brt * 2);
                map[b][i] = Color.getHSBColor(hue, sat, brt);
            }
        }
    }
        
    @Override
    public String toString()
    {
        return "Frequency-Based Rainbow Colour Map";
    }
    
    private Color[][] map = null;    
}
