package gui;

import detector.SpectrumAnalyser;
import detector.SpectrumInfo;
import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 * A panel that renders the frequency spectrum.
 * 
 * @author Stefan Marks
 * @verison 1.0 - 10.05.2013: Created
 */
public class FrequencySpectrumRenderPanel 
    extends JPanel 
    implements SpectrumAnalyser.Listener
{
    public FrequencySpectrumRenderPanel(SpectrumAnalyser analyser)
    {
        super(null, false);
        
        spectrum = null;
        
        strokeCentreLine = new BasicStroke(1);
        strokeSignal     = new BasicStroke(1);
        
        setBackground(Color.black);
        setForeground(Color.white);
        
        this.analyser = analyser;
        analyser.registerListener(this);
        
        // enable mouse motion events to analyse signal when paused
        enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    @Override
    public void setBounds(int x, int y, int width, int height)
    {
        super.setBounds(x, y, width, height);
        yPos   = height * 9 / 10;
        yScale = height * 8 / 10;
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
        spectrum = analyser.getSpectrumInfo(0);
        repaint();
    }
  
    @Override
    protected void paintComponent(Graphics graphics)
    {
        Graphics2D g = (Graphics2D) graphics;
        Rectangle  bounds = getBounds();
        g.setColor(getBackground());
        g.fillRect(0, 0, bounds.width, bounds.height);
        // draw X axes
        g.setColor(getForeground().darker());
        g.setStroke(strokeCentreLine);
        g.drawLine(0, yPos,  bounds.width - 1, yPos);
        Rectangle r = new Rectangle();
        
        // draw signal
        if ( (spectrum != null) && spectrum.isDefined() )
        {   
            xScale = getBounds().width / spectrum.intensity.length;
            g.setStroke(strokeSignal);
            for ( int i = 0 ; i < spectrum.intensity.length ; i++ )
            {
                // calculate rectangle fro full bar
                r.x      = i * xScale + 1;
                r.width  = xScale - 2;
                r.y      = 0;
                r.height = getBounds().height;
                        
                // check if mouse is within that rect 
                Point mp = getMousePosition();
                boolean selected = (mp != null) && r.contains(mp);
                
                // calculate real bar height based on intensity
                final float maxSI = 5;
                float si = spectrum.intensity[i];
                r.height = (int) (yScale * si / maxSI);
                r.y = yPos - r.height;
                g.setColor(RainbowColourMap.getColor(si, 0, maxSI));
                // draw bar
                if ( selected ) 
                {
                    g.drawRect(r.x, r.y, r.width, r.height);
                    // draw frequency info
                    g.setColor(getForeground());
                    String str = String.format("Frequency: %.0f Hz", analyser.getFFT().getAverageCenterFrequency(i));
                    g.drawString(str, getBounds().width / 2, 20);
                    str = String.format("Amplitude: %.2f", si);
                    g.drawString(str, getBounds().width / 2, 40);
                }
                else
                {
                    g.fillRect(r.x, r.y, r.width, r.height);
                }
                
            }
        }  
    }

    SpectrumAnalyser   analyser;
    private int        yPos, yScale, xScale;
    SpectrumInfo       spectrum;
    private Stroke     strokeCentreLine, strokeSignal;
  
    
}
