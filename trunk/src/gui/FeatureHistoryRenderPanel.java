package gui;

import analyser.SpectrumAnalyser;
import analyser.SpectrumInfo;
import detector.Feature;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;
import javax.swing.JPanel;

/**
 * A panel that renders the detected features over time.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 10.05.2013: Created
 */
public class FeatureHistoryRenderPanel 
    extends JPanel 
    implements SpectrumAnalyser.Listener
{
    public FeatureHistoryRenderPanel(SpectrumAnalyser analyser)
    {
        super(null, false);
        
        setBackground(Color.black);
        setForeground(Color.white);
        
        this.analyser = analyser;
        analyser.registerListener(this);
        
        setPreferredSize(new Dimension(20, 20));
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
        g.setColor(getForeground());
        // draw features
        List<Feature> features = analyser.getDetectedFeatures();
        int ySize = bounds.height / features.size();
        for ( int x = 0 ; x < analyser.getHistorySize() ; x++ )
        {   
            SpectrumInfo info = analyser.getSpectrumInfo(x);
            if ( info == null ) break;
            
            int y = 0;
            for ( Feature f : features )
            {
                if ( info.hasFeature(f) )
                {
                    g.drawLine(x, y, x, y + ySize - 1);
                }
                y += ySize;
            }
        }          
    }
    
    private SpectrumAnalyser analyser;    
}
