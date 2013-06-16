package analyser;

import ddf.minim.analysis.FFT;

/**
 * FFT that uses the maximum of a spectrum band instead of the average.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 15.06.2013: Created
 */
public class MaxFFT extends FFT
{
  /**
   * Constructs an FFT that will accept sample buffers that are
   * <code>timeSize</code> long and have been recorded with a sample rate of
   * <code>sampleRate</code>. <code>timeSize</code> <em>must</em> be a
   * power of two. This will throw an exception if it is not.
   * 
   * @param timeSize
   *          the length of the sample buffers you will be analyzing
   * @param sampleRate
   *          the sample rate of the audio you will be analyzing
   */
  public MaxFFT(int timeSize, float sampleRate)
  {
    super(timeSize, sampleRate);
  }
  
  /**
   * Calculate the maximum amplitude of the frequency band bounded by
   * <code>lowFreq</code> and <code>hiFreq</code>, inclusive.
   * 
   * @param lowFreq
   *          the lower bound of the band
   * @param hiFreq
   *          the upper bound of the band
   * @return the maximum of all spectrum values within the bounds
   */
  @Override
  public float calcAvg(float lowFreq, float hiFreq)
  {
    int lowBound = freqToIndex(lowFreq);
    int hiBound = freqToIndex(hiFreq);
    float max = 0;
    for (int i = lowBound; i <= hiBound; i++)
    {
      max = Math.max(spectrum[i], max);
    }
    return max;
  }
}
