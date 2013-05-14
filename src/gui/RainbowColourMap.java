package gui;

import java.awt.Color;

/**
 * Rainbow colour map.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 12.05.2013: Created
 */
public class RainbowColourMap 
{
    public static Color getColor(float value, float min, float max)
    {
        if ( map == null ) 
        {
            initColourMap();
        }
        int idx = (int) ((value - min) / (max - min) * map.length);
        if ( idx < 0           ) { idx = 0; }
        if ( idx >= map.length ) { idx = map.length - 1; }
        return map[idx];
    }
    
    private static void initColourMap()
    {
        map = new Color[256];
        for ( int i = 0 ; i < map.length ; i++ )
        {
            float f = (float) i / map.length;
            map[i] = Color.getHSBColor(0.70f - 0.70f * f, 1, 0.5f + 0.5f * f);
        }
    }
        
    private static Color[] map = null;    
}
