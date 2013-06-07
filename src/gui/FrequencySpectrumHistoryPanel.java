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
        
        this.analyser = analyser;
        analyser.registerListener(this);
        
        // enable mouse motion events to analyse signal when paused
        enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
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
        Graphics2D g = (Graphics2D) graphics;
        Rectangle  bounds = getBounds();
        g.setColor(getBackground());
        g.fillRect(0, 0, bounds.width, bounds.height);
        
        if ( !analyser.isAttachedToAudio() ) return;
                
        // draw signal
        int ySteps = analyser.getSpectrumBandCount();
        for ( int x = 0 ; x < analyser.getHistorySize() ; x++ )
        {   
            SpectrumInfo info = analyser.getSpectrumInfo(x);
            if ( info == null ) break;
            
            for ( int i = 0; i < info.intensity.length; i++ )
            {
                g.setColor(RainbowColourMap.getColor(info.intensity[i], 0, 6));
                g.drawLine(x, i * bounds.height / info.intensity.length, x, getBounds().height);
            }
        }   
        
        // draw a line for the frequency at the cursor Y position
        Point mp = getMousePosition();
        if ( mp != null )
        {
            int idx   = mp.y * ySteps / bounds.height;
            int y     = idx * bounds.height / ySteps;
            int yBase = bounds.height - 1;
            // draw red horizontal line for teh band selection
            g.setColor(Color.red.darker());
            g.drawLine(0, y, bounds.width, y);
            // draw intensity curve for that band
            g.setColor(Color.white);
            int yOld = yBase;    
            for ( int x = 0 ; x < analyser.getHistorySize() ; x++ )
            {   
                SpectrumInfo info = analyser.getSpectrumInfo(x);
                if ( info == null ) break;
                y = yBase - (int) (info.intensity[idx] * bounds.height / 6);
                g.drawLine(x-1, yOld, x, y);
                yOld = y;
            } 
        }        
    }
    
    private SpectrumAnalyser analyser;    
}
