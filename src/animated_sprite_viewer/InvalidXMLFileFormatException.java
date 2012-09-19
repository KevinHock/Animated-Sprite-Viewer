package animated_sprite_viewer;

/**
 * The InvalidXMLFileFormatException is a checked exception that
 * represents the occasion where an xml document does not validate
 * against its schema.
 * 
 * @author  Richard McKenna &
 *          Debugging Enterprises
 * @version 1.0
 */
public class InvalidXMLFileFormatException extends Exception
{
    // NAME OF XML FILE THAT DID NOT VALIDATE
    private String xmlFileWithError;
    
    // NAME OF XML SCHEMA USED FOR VALIDATION
    private String xsdFile;

    /**
     * Constructor for this exception, these are simple objects,
     * we'll just store some info about the error.
     * 
     * @param initXMLFileWithError XML doc file name that didn't validate
     * 
     * @param initXSDFile XML schema file used in validation     
     */    
    public InvalidXMLFileFormatException(   String initXMLFileWithError,
                                            String initXSDFile)
    {
        // KEEP IT FOR LATER
        xmlFileWithError = initXMLFileWithError;
        xsdFile = initXSDFile;
    }

    /**
     * This method builds and returns a textual description of this
     * object, which basically summarizes what went wrong.
     * 
     * @return This message will be useful for describing where
     * validation failed.
     */
    public String toString()
    {
        return "XML Document (" + xmlFileWithError 
                + ") does not conform to Schema (" + xsdFile + ")";
    }
}