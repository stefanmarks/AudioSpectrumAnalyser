package gui;

import analyser.SpectrumAnalyser;
import analyser.SpectrumInfo;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 * A panel that renders the frequency spectrum over time.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 10.05.2013: Created
 */
public class FrequencySpectrumHistoryPanel extends JPanel implements SpectrumAnalyser.Listener
{
    public FrequencySpectrumHistoryPanel(SpectrumAnalyser analyser)
    {
        super(null, false);
        
        setBackground(Color.black);
        setForeground(Color.white);
        colourMap = FrequencyRainbowColourMap.INSTANCE;
        
        this.analyser = analyser;
        analyser.registerListener(this);
        
        // enable mouse motion events to analyse signal when paused
        enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }
    
    /**
     * Gets the colour map used for rendering the frequency spectrum.
     * 
     * @return the colour map used for rendering
     */
    public ColourMap getColourMap()
    {
        return colourMap;
    }

    /**
     * Sets the colour map for rendering the frequency spectrum.
     * 
     * @param map  the new colour map used for rendering the spectrum 
     */
    public void setColourMap(ColourMap map)
    {
        colourMap = map;
    }
    
    @Override
    protected void processMouseMotionEvent(MouseEvent e)
    {
        super.processMouseMotionEvent(e);
        repaint();
    }
    
    @Override
    public void analysisUpdated(SpectrumAnalyser analyser)
    {
        repaint();
    }
  
    @Override
    protected void paintComponent(Graphics graphics)
    {
        final float maxSI = SpectrumInfo.MAX_SPECTRUM;
                
        Graphics2D g = (Graphics2D) graphics;
        Rectangle  bounds = getBounds();
        int        height = bounds.height;
        int        maxY   = height - 1;
        g.setColor(getBackground());
        g.fillRect(0, 0, bounds.width, bounds.height);
        
        if ( !analyser.isAttachedToAudio() ) return;
                
        // draw signal
        int xSize  = Math.min(bounds.width, analyser.getHistorySize());
        int ySteps = analyser.getSpectrumBandCount();
        
        for ( int x = 0 ; x < xSize ; x++ )
        {   
            SpectrumInfo info = analyser.getSpectrumInfo(x);
            if ( info == null ) break;
            
            for ( int i = 0; i < ySteps ; i++ )
            {
                g.setColor(colourMap.getColor(info, i));
                g.drawLine(x, maxY - i * height / ySteps, x, maxY - (i+1) * height / ySteps);
            }
        }   
        
        // draw a line for the frequency at the cursor Y position
        Point mp = getMousePosition();
        if ( mp != null )
        {
            int idx   = (maxY - mp.y) * ySteps / bounds.height;
            int y     = maxY - (int) ((idx + 0.5f) * bounds.height / ySteps);
            int yBase = bounds.height - 1;
            // draw red horizontal line for the band selection
            g.setColor(Color.red.darker());
            g.drawLine(0, y, bounds.width, y);
            // draw intensity curve for that band
            g.setColor(Color.white);
            int yOld = yBase;    
            for ( int x = 0 ; x < xSize ; x++ )
            {   
                SpectrumInfo info = analyser.getSpectrumInfo(x);
                if ( info == null ) break;
                y = yBase - (int) (info.intensity[idx] * bounds.height / maxSI);
                g.drawLine(x-1, yOld, x, y);
                yOld = y;
            } 
        }        
    }
    
    private SpectrumAnalyser analyser;   
    private ColourMap        colourMap;
}
