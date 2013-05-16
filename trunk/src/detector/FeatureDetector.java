package detector;

/**
 * Base class for an audio feature detector;
 * 
 * @author  Stefan Marks
 * @version 1.0 - 16.05.2013: Created
 */
public abstract class FeatureDetector 
{ 
    protected FeatureDetector(Feature feature)
    {
        this.feature = feature;
    }
    
    public abstract boolean detectFeature(SpectrumAnalyser analyser);
    
    public final Feature getFeature()
    { 
        return feature;
    }
    
    // the feature that this detector is looking for
    private final Feature feature;
}
