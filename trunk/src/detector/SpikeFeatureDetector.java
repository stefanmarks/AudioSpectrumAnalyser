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
     * @param peakSize  the number of samples necessary to be above the average
     * @param avgSize   the number of samples to average
     */
    public SpikeFeatureDetector(String name, int bitNum, float freqLow, float freqHigh, int peakSize, int avgSize)
    {
        super(new Feature(name, bitNum));
        this.freqLow  = freqLow;
        this.freqHigh = freqHigh;
       
        specIdxFrom = -1;
        specIdxTo   = -1;
        info     = new SpectrumInfo[peakSize + avgSize];
        sum      = new float[info.length];
        this.peakSize = peakSize;
    }
    
    private void determineSpectrumIndices(FFT fft)
    {
        if ( specIdxFrom < 0 )
        {
            specIdxFrom = fft.freqToIndex(freqLow);
            specIdxTo   = fft.freqToIndex(freqHigh);
            specCount   = 1 + specIdxTo - specIdxFrom;
            System.out.println(
                "Spike Feature detector '" + getFeature().getName() + 
                "': analysing spectrum indices " + specIdxFrom + " to " + specIdxTo);
        }
    }
    
    @Override
    public boolean detectFeature(SpectrumAnalyser analyser)
    {
        boolean detected = false;
        
        // get current and previous spectrum intensities
        boolean allDefined = true;
        for ( int i = 0 ; i < info.length ; i++ )
        {
            info[i] = analyser.getSpectrumInfo(i);
            if ( info[i] == null ) 
            {
                // all info entries must be non-null for the next part of the analysis
                allDefined = false;
            }
        }
        
        // did we get all the spectrum info?
        if ( allDefined )
        {
            // what spectrum index is the watch frequency range?
            determineSpectrumIndices(analyser.getFFT());

            // calculate sums
            for ( int iIdx = 0 ; iIdx < info.length ; iIdx++ )
            {
                SpectrumInfo si = info[iIdx];
                sum[iIdx] = 0.0f;        
                for ( int sIdx = specIdxFrom ; sIdx <= specIdxTo ; sIdx++ )
                {
                    sum[iIdx] += si.intensityRaw[sIdx];
                }
                sum[iIdx] /= specCount;
            }
            
            // calculate average
            float avg = 0;
            for ( int i = peakSize ; i < sum.length ; i++ )
            {
                avg += sum[i];
            }
            avg /= sum.length - peakSize;
            
            int aboveCount = 0;
            float peakSum = 0;
            for ( int i = 0 ; i < peakSize ; i++ )
            {
                peakSum += sum[i];
                if ( sum[i] > avg ) { aboveCount++; }
            }
            peakSum /= peakSize;
            detected = (peakSum > 1.1 * avg) &&
                       (aboveCount > peakSize * 4 / 5);
        }
        return detected;
    }
    
    @Override
    public int getDetectionDelay()
    {
        return peakSize; 
    }
    
    private float          freqLow, freqHigh;
    private int            specIdxFrom, specIdxTo, specCount;
    private SpectrumInfo[] info;
    private float[]        sum;
    private int            peakSize;
    
}
