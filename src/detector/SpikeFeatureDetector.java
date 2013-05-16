package detector;

import analyser.SpectrumAnalyser;
import analyser.SpectrumInfo;
import ddf.minim.analysis.FFT;

/**
 * Spike feature detector.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 16.05.2013: Created
 */
public class SpikeFeatureDetector extends FeatureDetector
{ 
    /**
     * Creates a new spike feature detector.
     * 
     * @param name      the name of the feature
     * @param bitNum    the feature bit number to set
     * @param freqLow   the lower end of the spectrum to observe
     * @param freqHigh  the upper end of the spectrum to observe
     */
    public SpikeFeatureDetector(String name, int bitNum, float freqLow, float freqHigh)
    {
        super(new Feature(name, bitNum));
        this.freqLow  = freqLow;
        this.freqHigh = freqHigh;
    }
    
    @Override
    public boolean detectFeature(SpectrumAnalyser analyser)
    {
        boolean detected = false;
        // what spectrum index is the watch frequency range?
        FFT fft = analyser.getFFT();
        int specIdxFrom = fft.freqToIndex(freqLow);
        int specIdxTo   = fft.freqToIndex(freqHigh);
        // compare current and previous spectrum intensities
        SpectrumInfo si0 = analyser.getSpectrumInfo(0);
        SpectrumInfo si1 = analyser.getSpectrumInfo(1);
        SpectrumInfo si2 = analyser.getSpectrumInfo(2);
        if ( (si0 != null) && (si1 != null) && (si2 != null) )
        {
            float sum0 = 0;
            float sum1 = 0;
            float sum2 = 0;
            for ( int idx = specIdxFrom ; idx <= specIdxTo ; idx++ )
            {
                sum0 += si0.intensityRaw[idx]; // sustain?
                sum1 += si1.intensityRaw[idx]; // peak?
                sum2 += si2.intensityRaw[idx]; // low attack
            }
            if ( (sum1 > sum0) && (sum1 > sum2) && 
                 (sum1 - sum2) > (sum1 - sum0) && (sum1-sum2) > 2)
            {
                detected = true;
                System.out.println(sum1-sum2);
            }
        }
        return detected;
    }

    private float freqLow, freqHigh;
}
