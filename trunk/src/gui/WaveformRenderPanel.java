package gui;

import detector.SpectrumAnalyser;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import javax.swing.JPanel;

/**
 * A panel that renders the pure waveform.
 * 
 * @author Stefan Marks
 * @verison 1.0 - 10.05.2013: Created
 */
public class WaveformRenderPanel extends JPanel implements SpectrumAnalyser.Listener
{
    private float[]    samplesL;
    private float[]    samplesR;
    private int        yLeft, yRight, yScale;
    private float      xScale; 
    private Stroke     strokeCentreLine, strokeSignal;
  
    public WaveformRenderPanel(SpectrumAnalyser analyser)
    {
        super(null, false);
        
        samplesL  = null; 
        samplesR = null;
        
        strokeCentreLine = new BasicStroke(1);
        strokeSignal     = new BasicStroke(1);
        
        setBackground(Color.black);
        setForeground(Color.green);
        
        analyser.registerListener(this);
    }

    @Override
    public void setBounds(int x, int y, int width, int height)
    {
        super.setBounds(x, y, width, height);
        yLeft  = height * 1 / 4;
        yRight = height * 3 / 4;
        yScale = height / 4 - 2;
    }

    @Override
    public void analysisUpdated(SpectrumAnalyser analyser)
    {
      samplesL = analyser.getAudioDataL();
      samplesR = analyser.getAudioDataR();
      repaint();
    }
  
    @Override
    protected void paintComponent(Graphics graphics)
    {
        // draw X axes
        Graphics2D g = (Graphics2D) graphics;
        Rectangle  bounds = getBounds();
        g.setColor(getBackground());
        g.fillRect(0, 0, bounds.width, bounds.height);
        g.setColor(getForeground().darker());
        g.setStroke(strokeCentreLine);
        g.drawLine(0, yLeft,  bounds.width, yLeft);
        g.drawLine(0, yRight, bounds.width, yRight);
        // draw signal
        if ( samplesL != null && samplesR != null )
        {   
            int x, xOld, yL, yR, yOldL, yOldR;
            xOld = -10; yOldL = yLeft; yOldR = yRight;
            xScale = getBounds().width / (float) samplesL.length;        
            g.setColor(getForeground());
            g.setStroke(strokeSignal);
            for ( int i = 0; i < samplesL.length; i++ )
            {
                x = (int) (i * xScale);
                yL = yLeft  + (int) (samplesL[i]  * yScale);
                yR = yRight + (int) (samplesR[i] * yScale);
                g.drawLine(xOld, yOldL, x, yL);
                g.drawLine(xOld, yOldR, x, yR);
                xOld  = x;
                yOldL = yL;
                yOldR = yR;
            }
        }  
    }

}
