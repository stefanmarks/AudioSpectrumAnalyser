package detector;

import ddf.minim.analysis.FFT;

/**
 * Class with information about the spectrum at a specific time.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 12.05.2013: Created
 */
public class SpectrumInfo 
{
    /**
     * Creates a new spectrum information instance.
     */
    public SpectrumInfo()
    {
        reset();
    }

    /**
     * Copies spectrum analysis data from an FFT analyser.
     * 
     * @param fft  the FFT analyser to use
     */
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
    
    /**
     * Resets the spectrum information.
     */
    public void reset()
    {
        sampleIdx = 0;
        intensity = null;
    }
    
    /**
     * Checks if she information in this dataset is defined or not.
     * 
     * @return <code>true</code> if the dataset is defined,
     *         <code>false</code> if not
     */
    public boolean isDefined()
    {
        return intensity != null;
    }
    
    // millisecond index into the sound file
    public int     sampleIdx;
    // array of frequency intensities
    public float[] intensity;
}
