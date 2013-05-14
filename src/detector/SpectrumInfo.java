package detector;

import ddf.minim.AudioSource;
import ddf.minim.analysis.FFT;

/**
 * Class with information about the spectrum at a specific time.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 12.05.2013: Created
 */
public class SpectrumInfo 
{
    public SpectrumInfo()
    {
        reset();
    }

    public void copyData(FFT fft)
    {
        if ( (intensity == null) || (intensity.length != fft.avgSize()) )
        {
            intensity = new float[fft.avgSize()];
        }
        for (int i = 0; i < fft.avgSize(); i++)
        {
            intensity[i] = (float) Math.log(1 + fft.getAvg(i));
        }
    }
    
    public void reset()
    {
        sampleIdx = 0;
        intensity = null;
    }
    
    public boolean isDefined()
    {
        return intensity != null;
    }
    
    public int     sampleIdx;
    public float[] intensity;
}
