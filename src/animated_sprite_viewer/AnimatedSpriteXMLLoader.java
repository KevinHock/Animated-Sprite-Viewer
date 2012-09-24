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
     * Constructor for this XML loader. 
     * 
     * @param initView The view will be needed for loading the images
     */
    public AnimatedSpriteXMLLoader(AnimatedSpriteViewer initView)
    {
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
    public static void loadSpriteTypeNames(String path,
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
        
        //A placeholder string to hold each sprite name.
        String eachNameOfSprite;
        
        // IF THERE'S A PROBLEM LOADING THE XML FILE THEN
        // SKIP THIS SPRITE TYPE
        if (cleanDoc == null)
        {
            throw new InvalidXMLFileFormatException(xmlFile, xsdFile);
        }
        
        // IT'S A VALID XML FILE SO LET'S GET THE DATA
        //Gets the root of sprite_type_list.xml which is <sprite_type_list>
        WhitespaceFreeXMLNode spriteTypeListNode = cleanDoc.getRoot();
        
        //Gets the children of <sprite_type_list> named <sprite_type> containing the different names of the sprites and puts them in an ArrayList.
        ArrayList<WhitespaceFreeXMLNode> listOfSpriteTypes = spriteTypeListNode.getChildrenOfType("sprite_type");
        
        //A loop to go through each <sprite_type> and get the names of each sprite to add to the arrayList of Strings named spriteTypeNames.
        for(int index=0;index<listOfSpriteTypes.size();index++)
        {
            eachNameOfSprite = listOfSpriteTypes.get(index).getData();
            spriteTypeNames.add(eachNameOfSprite);
        }
    }
    
    /**
     * This method extracts the animation states and attributes of all sprite types from the provided
     * xml file argument and loads these names into the animationStates and animationAttributes
     * lists.
     * 
     * @param pathToSprite Path to where the sprite is.
     * 
     * @param xmlOfSpriteType File name for the xml file of the sprite.
     * 
     * @param animationStates List where we'll put all the sprite animation
     * states.
     * 
     * @param animationAttributes List where we'll put all the sprite animation
     * attributes.
     * 
     * @throws InvalidXMLFileFormatException Thrown if we encounter an xml
     * file that does not validate against its schema.
     */
    public static void loadSpriteAnimationStatesAndAttributes(String pathToSprite,
                                                 String xmlOfSpriteType,
                                                 ArrayList<String> animationStates,
                                                 ArrayList<String[][]> animationAttributes)
                                                 throws InvalidXMLFileFormatException
    {
        // FIRST LET'S BUILD THE NAME OF THE XML FILE
        String xmlFile = (pathToSprite + xmlOfSpriteType).trim();
        
        // NOW LET'S BUILD THE NAME OF THE SCHEMA
        String xsdFile = "./data/sprite_types/sprite_type.xsd";
        
        // IS THE XML VALID PER THE SCHEMA?
        WhitespaceFreeXMLDoc cleanDoc = loadXMLDocument(xmlFile, xsdFile);

        // IF THERE'S A PROBLEM LOADING THE XML FILE THEN
        // SKIP THIS SPRITE TYPE
        if (cleanDoc == null)
        {
            throw new InvalidXMLFileFormatException(xmlFile, xsdFile);
        }

        // IT'S A VALID XML FILE SO LET'S GET THE DATA
        //Gets the root of sprite_type_list.xml which is <sprite_type>.
        WhitespaceFreeXMLNode root = cleanDoc.getRoot();
        
        //Puts all the animations_list's in an ArrayList.
        ArrayList<WhitespaceFreeXMLNode> listOfAnimations_Lists = root.getChildrenOfType("animations_list");
        
        //Make an ArrayList for each nested loop so every child of every child can be accessed.
        ArrayList<WhitespaceFreeXMLNode> listOfAnimation_States,listOfStates,listOfAnimation_Sequences,listOfPoses;
        
        //Go through each animation list and get each animation_state.
        for(int eachAnimations_List=0;eachAnimations_List<listOfAnimations_Lists.size();eachAnimations_List++){
            listOfAnimation_States = listOfAnimations_Lists.get(eachAnimations_List).getChildrenOfType("animation_state");
            
            //Go through each animation state and get each state.
            for(int eachAnimation_State=0;eachAnimation_State<listOfAnimation_States.size();eachAnimation_State++){
                listOfStates = listOfAnimation_States.get(eachAnimation_State).getChildrenOfType("state");
                
                //Add each state to the animationStates parameter.
                for(int eachState=0;eachState<listOfStates.size();eachState++)
                    animationStates.add(listOfStates.get(eachState).getData());
                
                //Puts all the animation_sequences's in an ArrayList.
                listOfAnimation_Sequences = listOfAnimation_States.get(eachAnimation_State).getChildrenOfType("animation_sequence");
                
                //Go through each animation sequence and get each pose.
                for(int eachPose=0;eachPose<listOfAnimation_Sequences.size();eachPose++){
                    listOfPoses = listOfAnimation_Sequences.get(eachPose).getChildrenOfType("pose");
                    
                    //Make an array for the 2 attributes of each pose.
                    String attributeArray[][] = new String[2][listOfPoses.size()];
                    
                    //Fill the array with each attribute.
                    for(int eachAttribute=0;eachAttribute<listOfPoses.size();eachAttribute++){
                        attributeArray[0][eachAttribute] = listOfPoses.get(eachAttribute).getAttributeValue("duration");
                        attributeArray[1][eachAttribute] = listOfPoses.get(eachAttribute).getAttributeValue("image_id");
                    }
                    
                    //Add the attribute array to the animationAttributes parameter holding all the attribute arrays for each pose.
                    animationAttributes.add(attributeArray);
                }
            }
        }
    }
    /**
     * This method extracts the animation states and attributes of all sprite types from the provided
     * xml file argument and loads these names into the animationStates and animationAttributes
     * lists.
     * 
     * @param pathToSprite Path to where the sprite is.
     * 
     * @param xmlOfSpriteType File name for the xml file of the sprite.
     * 
     * @param 
     * 
     * @throws InvalidXMLFileFormatException Thrown if we encounter an xml
     * file that does not validate against its schema.
     */
    public static void loadNumberOfImagesForEachState(String pathToSprite,
                                           String xmlOfSpriteType,
                                           ArrayList<Integer> numberOfImagesForEachState)
                                           throws InvalidXMLFileFormatException
    {
        // FIRST LET'S BUILD THE NAME OF THE XML FILE
        String xmlFile = (pathToSprite + xmlOfSpriteType).trim();
        
        // NOW LET'S BUILD THE NAME OF THE SCHEMA
        String xsdFile = "./data/sprite_types/sprite_type.xsd";
        
        // IS THE XML VALID PER THE SCHEMA?
        WhitespaceFreeXMLDoc cleanDoc = loadXMLDocument(xmlFile, xsdFile);

        // IF THERE'S A PROBLEM LOADING THE XML FILE THEN
        // SKIP THIS SPRITE TYPE
        if (cleanDoc == null)
        {
            throw new InvalidXMLFileFormatException(xmlFile, xsdFile);
        }

        // IT'S A VALID XML FILE SO LET'S GET THE DATA
        //Gets the root of sprite_type_list.xml which is <sprite_type>.
        WhitespaceFreeXMLNode root = cleanDoc.getRoot();
        
        //Puts all the animations_list's in an ArrayList.
        ArrayList<WhitespaceFreeXMLNode> listOfImages_List = root.getChildrenOfType("images_list");
        
        ArrayList<WhitespaceFreeXMLNode> listOfImages;
        int numberOfImages;
        //Go through each Images list.
        for(int eachImages_List=0;eachImages_List<listOfImages_List.size();eachImages_List++){
            listOfImages = listOfImages_List.get(eachImages_List).getChildrenOfType("image_file");
            numberOfImages = listOfImages.size();
            
        }
        
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
            File schemaLocation = new File(xmlSchemaNameAndPath);//950 for sprite_type_list
            Schema schema = factory.newSchema(schemaLocation);
            
            // 3. Get a validator from the schema.
            Validator validator = schema.newValidator();
            
            // 4. Parse the document you want to check.
            Source source = new StreamSource(xmlDocNameAndPath);
            
            // 5. Check the document
            validator.validate(source);//1148 = stream for source and  2144 for fuckup
            return true;
        }
        catch (Exception e) 
        {
            return false;
        }          
    }
}
