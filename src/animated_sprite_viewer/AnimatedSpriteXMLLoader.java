package animated_sprite_viewer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * The AnimatedSpriteXMLLoader class knows how to load and 
 * validate xml files for represeting sprite type lists as
 * well as sprite types themselves. It also knows how to
 * load sprite types (including images) using the data found
 * in the xml files.
 * 
 * The sprite types should be listed in the following file:
 * ./data/sprite_types/sprite_types_list.xml, 
 * 
 * which can be validated by:
 * ./data/sprite_types/sprite_types_list.xsd
 * 
 * Each sprite type then has its own description found inside
 * its own directory. They can be validated by:
 * ./data/sprite_types/sprite_type.xsd
 * 
 * @author  Richard McKenna &
 *          Debugging Enterprises
 * @version 1.0
 */
public class AnimatedSpriteXMLLoader 
{
    // THE VIEW IS USED TO HELP WITH IMAGE LOADING
    private AnimatedSpriteViewer view;
    
    // DON'T BURY IMPORTANT STRING CONSTANTS INSIDE METHODS
    public static final String SCHEMA_STANDARD_SPEC_URL = "http://www.w3.org/2001/XMLSchema";
    public static final String SPRITE_TYPE_LIST_NODE_NAME = "sprite_type_list";
    public static final String SPRITE_TYPE_NODE_NAME = "sprite_type";
    public static final String WIDTH_NODE_NAME = "width";
    public static final String HEIGHT_NODE_NAME = "height";
    public static final String IMAGES_LIST_NODE_NAME = "images_list";
    public static final String IMAGE_FILE_NODE_NAME = "image_file";
    public static final String ID_ATTRIBUTE_NAME = "id";
    public static final String FILE_NAME_ATTRIBUTE_NAME = "file_name";
    public static final String ANIMATIONS_LIST_NODE_NAME = "animations_list";
    public static final String ANIMATION_STATE_NODE_NAME = "animation_state";
    public static final String STATE_NODE_NAME = "state";
    public static final String ANIMATION_SEQUENCE_NODE_NAME = "animation_sequence";
    public static final String POSE_NODE_NAME = "pose";
    public static final String IMAGE_ID_ATTRIBUTE_NAME = "image_id";
    public static final String DURATION_ATTRIBUTE_NAME = "duration";

    /**
     * Constructor for this xml loader. 
     * 
     * @param initView The view will be neede for loading the images
     */
    public AnimatedSpriteXMLLoader(AnimatedSpriteViewer initView)
    {
        // WE'LL NEED THIS LATER
        view = initView;
    }

    /**
     * This method extracts the names of all sprite types from the provided
     * xml file argument and loads these names into the spriteTypeNames
     * list.
     * 
     * @param path Path to where the sprite types home directory. Note that
     * each sprite would have its own directory inside this directory.
     * 
     * @param spriteTypesXMLFile File name for the xml file with a list of
     * all the sprite types.
     * 
     * @param spriteTypeNames List where we'll put all the sprite type
     * names we find.
     * 
     * @throws InvalidXMLFileFormatException Thrown if we encounter an xml
     * file that does not validate against its schema.
     */
    public static void loadSpriteTypeNames(    String path,
                                        String spriteTypesXMLFile,
                                        ArrayList<String> spriteTypeNames)
            throws InvalidXMLFileFormatException
    {
        // FIRST LET'S BUILD THE NAME OF THE XML FILE
        String xmlFile = (path + spriteTypesXMLFile).trim();
        
        // NOW LET'S BUILD THE NAME OF THE SCHEMA
        String xsdFile = xmlFile.substring(0, xmlFile.length()-4) + ".xsd";
        
        // IS THE XML VALID PER THE SCHEMA?
        WhitespaceFreeXMLDoc cleanDoc = loadXMLDocument(xmlFile, xsdFile);
        
        // IF THERE'S A PROBLEM LOADING THE XML FILE THEN
        // SKIP THIS SPRITE TYPE
        if (cleanDoc == null)
        {
            throw new InvalidXMLFileFormatException(xmlFile, xsdFile);
        }
        
        // IT'S A VALID XML FILE SO LET'S GET THE DATA
        WhitespaceFreeXMLNode spriteTypeListNode = cleanDoc.getRoot();//clean doc and get node sprite_type_list
        //spriteTypeListNode.getChildrenOfType("sprite_type");
        ArrayList<WhitespaceFreeXMLNode> gotRoot = spriteTypeListNode.getChildrenOfType("sprite_type");
        for(int qq=0;qq<gotRoot.size();qq++){
            String sheep = gotRoot.get(qq).getData();
            spriteTypeNames.add(sheep);
        }

        /*String ddsdsdsds = "whereZZZZZ";
        Iterator<WhitespaceFreeXMLNode> spriteTypesIterator = spriteTypeListNode.getChildren();//Iterator of sprite_type 
        while(spriteTypesIterator.hasNext())
        {
            WhitespaceFreeXMLNode spriteTypeNode = spriteTypesIterator.next();
            String spriteTypeName = spriteTypeNode.getData();//get data of sprite type
            spriteTypeNames.add(spriteTypeName);
        }*/            
    }
    
    /**
     * This method extracts the animation states of all sprite types from the provided
     * xml file argument and loads these names into the spriteTypeNames
     * list.
     * 
     * @param path Path to where the sprite types home directory. Note that
     * each sprite would have its own directory inside this directory.
     * 
     * @param spriteTypesXMLFile File name for the xml file with a list of
     * all the sprite types.
     * 
     * @param animationStates List where we'll put all the sprite type
     * names we find.
     * 
     * @throws InvalidXMLFileFormatException Thrown if we encounter an xml
     * file that does not validate against its schema.
     */
    public static void loadSpriteAnimationStates(    String path,
                                        String spriteTypesXMLFile,
                                        ArrayList<String> animationStates)
            throws InvalidXMLFileFormatException
    {
        // FIRST LET'S BUILD THE NAME OF THE XML FILE
        String xmlFile = (path + spriteTypesXMLFile).trim();
        
        // NOW LET'S BUILD THE NAME OF THE SCHEMA
        String xsdFile = xmlFile.substring(0, xmlFile.length()-4) + ".xsd";
        
        // IS THE XML VALID PER THE SCHEMA?
        WhitespaceFreeXMLDoc cleanDoc = loadXMLDocument(xmlFile, xsdFile);
        
        // IF THERE'S A PROBLEM LOADING THE XML FILE THEN
        // SKIP THIS SPRITE TYPE
        if (cleanDoc == null)
        {
            throw new InvalidXMLFileFormatException(xmlFile, xsdFile);
        }
        
        // IT'S A VALID XML FILE SO LET'S GET THE DATA
        WhitespaceFreeXMLNode root = cleanDoc.getRoot();//clean doc and get node sprite_type_list
        //spriteTypeListNode.getChildrenOfType("sprite_type");
        ArrayList<WhitespaceFreeXMLNode> animationsList = root.getChildrenOfType("animations_list");
        WhitespaceFreeXMLNode statesParent = animationsList.get(0);
        ArrayList<WhitespaceFreeXMLNode> states = statesParent.getChildrenOfType("animation_state");
        for(int qq=0;qq<states.size();qq++){
            animationStates.add(states.get(qq).getData());
        }
        /*
        for(int qq=0;qq<gotRoot.size();qq++){
            String sheep = gotRoot.get(qq).getData();
            animationStates.add(sheep);
        }
        */
    }
    
    /**
     * This method reads in the xmlFile, validates it against the
     * schemaFile, and if valid, loads it into a WhitespaceFreeXMLDoc
     * and returns it, which helps because that's a much easier
     * format for us to deal with.
     * 
     * @param xmlFile Path and name of xml file to load.
     * 
     * @param schemaFile Path and name of schema file to use for validation.
     * 
     * @return A WhitespaceFreeXMLDoc object fully loaded with the data found
     * in the xmlFile. We like these because they don't have all the clutter
     * that whitespace requires.
     * 
     * @throws InvalidXMLFileFormatException Thrown if the xml file validation fails.
     */
    public static WhitespaceFreeXMLDoc loadXMLDocument(String xmlFile,
                                                String schemaFile)
            throws InvalidXMLFileFormatException
    {
        // FIRST LET'S VALIDATE IT
        boolean validDoc = validateXMLDoc(xmlFile, schemaFile);
        if (!validDoc)
        {
            // FAIL
            throw new InvalidXMLFileFormatException(xmlFile, schemaFile);
        }

        // THIS IS JAVA API STUFF
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try
        {            
            // FIRST RETRIEVE AND LOAD THE FILE INTO A TREE
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xmlDoc = db.parse(xmlFile);
            
            // THEN PUT IT INTO A FORMAT WE LIKE
            WhitespaceFreeXMLDoc cleanDoc = new WhitespaceFreeXMLDoc();
            cleanDoc.loadDoc(xmlDoc);
            return cleanDoc;
        }
        // THESE ARE XML-RELATED ERRORS THAT COULD HAPPEN DURING
        // LOADING AND PARSING IF THE XML FILE IS NOT WELL FORMED
        // OR IS NOW WHERE AND WHAT WE SAY IT IS
        catch(ParserConfigurationException pce)
        {
            pce.printStackTrace();
            return null;
        }
        catch(SAXException se)
        {
            se.printStackTrace();
            return null;
        }
        catch(IOException io)
        {
            io.printStackTrace();
            return null;
        }           
    }

    /**
     * This method validates the xmlDocNameAndPath doc against the 
     * xmlSchemaNameAndPath schema and returns true if valid, false
     * otherwise. Note that this is taken directly (with comments)
     * from and example on the IBM site with only slight modifications.
     * 
     * @see http://www.ibm.com/developerworks/xml/library/x-javaxmlvalidapi/index.html
     * 
     * @param xmlDocNameAndPath XML Doc to validate
     * 
     * @param xmlSchemaNameAndPath XML Schema to use in validation
     * 
     * @return true if the xml doc is validate, false if it does not.
     */
    public static boolean validateXMLDoc(  String xmlDocNameAndPath,
                                    String xmlSchemaNameAndPath)
    {
        try
        {
            // 1. Lookup a factory for the W3C XML Schema language
            SchemaFactory factory = 
                    SchemaFactory.newInstance(SCHEMA_STANDARD_SPEC_URL);
            
            // 2. Compile the schema. 
            // Here the schema is loaded from a java.io.File, but you could use 
            // a java.net.URL or a javax.xml.transform.Source instead.
            File schemaLocation = new File(xmlSchemaNameAndPath);
            Schema schema = factory.newSchema(schemaLocation);
            
            // 3. Get a validator from the schema.
            Validator validator = schema.newValidator();
            
            // 4. Parse the document you want to check.
            Source source = new StreamSource(xmlDocNameAndPath);
            
            // 5. Check the document
            validator.validate(source);
            return true;
        }
        catch (Exception e) 
        {
            return false;
        }          
    }
}