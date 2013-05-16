package detector;

/**
 * Class for a feature of the analysed audio, 
 * e.g., bass drum kick, string section.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 16.05.2013: Created
 */
public class Feature 
{
    /**
     * Creates an instance of an audio feature.
     * 
     * @param name   the name of the feature
     * @param bitNum the bit index (0..15) of the feature
     */
    public Feature(String name, int bitNum)
    {
        this.name = name;
        this.bitMask = 1 << bitNum;
    }

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Gets the feature's bitmask.
     * 
     * @return the feature's bitmask
     */
    public long getBitmask()
    {
        return bitMask;
    }

    private final String name;
    private final int    bitMask;
}
