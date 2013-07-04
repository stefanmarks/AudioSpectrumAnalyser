package gui;

import analyser.SpectrumInfo;
import java.awt.Color;

/**
 * Interface for colour maps.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 19.06.2013: Created
 */
public interface ColourMap 
{
    /**
     * Returns a colour to render the specific intensity of the specific frequency.
     * 
     * @param info   the frequency spectrum to get the colour for
     * @param value  the frequency index to get the colour for
     * 
     * @return the colour to use for rendering
     */
    Color getColor(SpectrumInfo info, int frequencyIdx);
}
