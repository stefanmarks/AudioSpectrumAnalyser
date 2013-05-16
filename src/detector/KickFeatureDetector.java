package detector;

import analyser.SpectrumAnalyser;
import analyser.SpectrumInfo;
import ddf.minim.analysis.FFT;

/**
 * Kick (=bass drum) feature detector.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 16.05.2013: Created
 */
public class KickFeatureDetector extends FeatureDetector
{ 
    /**
     * Creates a new kick feature detector
     * 
     * @param frequency the kick frequency to watch
     * @param bitNum    the feature bit number to set
     */
    public KickFeatureDetector(String name, float freqLow, float freqHigh, int bitNum)
    {
        super(new Feature(name, bitNum));
        this.kickFreqLow  = freqLow;
        this.kickFreqHigh = freqHigh;
    }
    
    @Override
    public boolean detectFeature(SpectrumAnalyser analyser)
    {
        boolean detected = false;
        // what spectrum index is the watch frequency range?
        FFT fft = analyser.getFFT();
        int idx0 = fft.freqToIndex(kickFreqLow);
        int idx1 = fft.freqToIndex(kickFreqHigh);
        // compare current and previous spectrum intensities
        SpectrumInfo si0 = analyser.getSpectrumInfo(0);
        SpectrumInfo si1 = analyser.getSpectrumInfo(1);
        SpectrumInfo si2 = analyser.getSpectrumInfo(2);
        if ( (si0 != null) && (si1 != null) && (si2 != null) )
        {
            float sum0 = 0;
            float sum1 = 0;
            float sum2 = 0;
            for ( int idx = idx0 ; idx <= idx1 ; idx++ )
            {
                sum0 += si0.intensityRaw[idx];
                sum1 += si1.intensityRaw[idx];
                sum2 += si2.intensityRaw[idx];
            }
            if ( 1.1 * sum0 < sum1 && sum1 > 1.1 * sum2 )
            {
                detected = true;
            }
        }
        return detected;
    }
    
    private float kickFreqLow, kickFreqHigh;
}
