package analyser;

import ddf.minim.analysis.FFT;
import detector.Feature;

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
    public void copySpectrumData(FFT fft)
    {
        int spectrumSize = fft.avgSize();
        if ( (intensity == null) || (intensity.length != spectrumSize) )
        {
            intensity    = new float[spectrumSize];
            intensityRaw = new float[fft.specSize()];
        }
        for (int i = 0; i < spectrumSize; i++)
        {
            intensity[i] = (float) Math.log(1 + fft.getAvg(i));
        }
        for (int i = 0; i < fft.specSize(); i++)
        {
            intensityRaw[i] = (float) Math.log(1 + fft.getBand(i));
        }
    }
    
    /**
     * Resets the spectrum information.
     */
    public void reset()
    {
        sampleIdx    = 0;
        intensity    = null;
        intensityRaw = null;
        features     = 0;
    }
    
    /**
     * Checks if the information in this dataset is defined or not.
     * 
     * @return <code>true</code> if the dataset is defined,
     *         <code>false</code> if not
     */
    public boolean isDefined()
    {
        return intensity != null;
    }
    
    /**
     * Checks if a specific feature has been detected.
     * 
     * @return <code>true</code> if the feature has been detected,
     *         <code>false</code> if not
     */
    public boolean hasFeature(Feature f)
    {
        return (features & f.getBitmask()) != 0;
    }
        
    
    // millisecond index into the sound file
    public int     sampleIdx;
    // array of frequency intensities
    public float[] intensity;
    // array of frequency intensities 
    public float[] intensityRaw;
    // bitmap with detected features
    public long    features;
}
