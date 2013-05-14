package gui;

import detector.SpectrumAnalyser;
import detector.SpectrumInfo;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
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
    }

    @Override
    public void setBounds(int x, int y, int width, int height)
    {
        super.setBounds(x, y, width, height);
        yScale = height;
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
        // draw signal
        int xPos = 0;
        for ( int x = 0 ; x < analyser.getHistorySize() ; x++ )
        {   
            SpectrumInfo info = analyser.getSpectrumInfo(x);
            if ( (info == null) || !info.isDefined() ) break;
            
            for ( int i = 0; i < info.intensity.length; i++ )
            {
                g.setColor(RainbowColourMap.getColor(info.intensity[i], 0, 6));
                g.drawLine(xPos, i * yScale / info.intensity.length, xPos, getBounds().height);
            }
            
            xPos++;
        }        
    }
    
    private int              yScale;
    private SpectrumAnalyser analyser;    
}
