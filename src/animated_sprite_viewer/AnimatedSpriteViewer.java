package animated_sprite_viewer;

import animated_sprite_viewer.events.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.border.Border;
import sprite_renderer.AnimationState;
import sprite_renderer.PoseList;
import sprite_renderer.SceneRenderer;
import sprite_renderer.Sprite;
import sprite_renderer.SpriteType;

/**
 * The AnimatedSpriteViewer application lets one load and view
 * sprite states and then view their animation. Note that all
 * data concerning the sprites should be done via .xml files.
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
 * @author  Kevin Hock
 */
public class AnimatedSpriteViewer extends JFrame
{
    // FOR THE TITLE BAR
    public static final String APP_TITLE = "Animated Sprite Viewer by Kevin Hock";
    
    // FOR LOCATING ASSETS
    public static final String GUI_IMAGES_PATH = "./data/buttons/";
    public static final String SPRITES_DATA_PATH = "./data/sprite_types/";
    public static final String SPRITE_TYPE_LIST_FILE = "sprite_type_list.xml";
    public static final String SPRITE_TYPE_LIST_SCHEMA_FILE = "sprite_type_list.xsd";
    public static final String SPRITE_TYPE_SCHEMA_FILE = "sprite_type.xsd";
    public static final String SELECT_SPRITE_TYPE_TEXT = "Select Sprite Type";
    public static final String SELECT_ANIMATION_TEXT = "Select Animation State";
 
    // ArrayList for all of the sprites
    private ArrayList<Sprite> spriteList;
    
    // ArrayList for type names
    private ArrayList<String> spriteTypeNames;
    
    //Array list for Animation State
    public ArrayList<String> spriteAnimationStates;
    
    // THIS WILL DO OUR XML FILE LOADING FOR US
    private AnimatedSpriteXMLLoader xmlLoader;
    
    // THE WEST WILL PROVIDE SPRITE TYPE AND ANIM STATE SELECTION CONTROLS
    private JPanel westOfSouthPanel;
    
    // THIS WILL STORE A SELECTABLE LIST OF THE LOADED SPRITES
    private JScrollPane spriteTypesListJSP;
    public JList spriteTypesList;//try later with private
    private DefaultListModel spriteTypesListModel;
    
    // THIS WELL LET THE USER CHOOSE DIFFERENT ANIMATION STATES TO VIEW
    private JComboBox spriteStateCombobox;
    private DefaultComboBoxModel spriteStateComboBoxModel;
    
    // THIS PANEL WILL ORGANIZE THE CENTER
    private JPanel southPanel;
    
    // THIS PANEL WILL RENDER OUR SPRITE
    private SceneRenderer sceneRenderingPanel;
    
    // THIS TOOLBAR WILL ALLOW THE USER TO CONTROL ANIMATION
    private JPanel animationToolbar;
    private JButton startButton,stopButton,slowDownButton,speedUpButton;
    
    //Self-explanatory strings
    public String directoryOfSprite,spriteType;
    private String xmlOfSpriteType;
    private ArrayList<String[][]> spriteAnimationAttributes;
    
    /**
     * The entire application will be initialized from here, including
     * the loading of all the sprite states from the xml file.
     */
    public AnimatedSpriteViewer()
    {
        initWindow();
        initData();
        initGUI();
        initHandlers();
    }
    
    /**
     * Sets up the window for use.
     */
    private void initWindow()
    {
        setTitle(APP_TITLE);//"Animated Sprite Viewer by Kevin Hock"
        setSize(700, 700);
        setLocation(0, 0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * Loads all the data needed for the sprite types. Note that we'll
     * need all the sprite states, including their art and animations,
     * loaded before we initialize the GUI.
     */
    private void initData()
    {
        // WE'LL ONLY PUT ONE SPRITE IN THIS
        spriteList = new ArrayList<Sprite>();
        
        // WE'LL PUT ALL THE SPRITE TYPES HERE
        spriteTypeNames = new ArrayList<String>();
        
        // LOAD THE SPRITE TYPES FROM THE XML FILE
        try
        {
            // THIS WILL LOAD AND VALIDATE
            // OUR XML FILES
            xmlLoader = new AnimatedSpriteXMLLoader(this);
            
            // FIRST UP IS THE SPRITE TYPES LIST
            //                       "./data/sprite_types/"   "sprite_type_list.xml"   Empty ArrayList of Strings     
            xmlLoader.loadSpriteTypeNames(SPRITES_DATA_PATH,SPRITE_TYPE_LIST_FILE, spriteTypeNames);
        }
        catch(InvalidXMLFileFormatException ixffe)
        {
            // IF WE DON'T HAVE A VALID SPRITE TYPE 
            // LIST WE HAVE NOTHING TO DO, WE'LL POP
            // OPEN A DIALOG BOX SO THE USER KNOWS
            // WHAT HAPPENED
            JOptionPane.showMessageDialog(this, ixffe.toString());
            System.exit(0);
        }
    }
    
    /**
     * This initializes all the GUI components and places
     * them into the frame in their appropriate locations.
     */
    private void initGUI()
    {
        // NOTE THAT WE'VE ALREADY LOADED THE XML FILE
        // WITH ALL THE SPRITE TYPES, SO WE CAN USE
        // THEM HERE TO POPULATE THE JList
        spriteTypesListModel = new DefaultListModel();
        Iterator<String> spriteTypeNamesIt = spriteTypeNames.iterator();
        while (spriteTypeNamesIt.hasNext())
        {
            String spriteTypeName = spriteTypeNamesIt.next();
            spriteTypesListModel.addElement(spriteTypeName);
        }
        spriteTypesList = new JList();
        
        spriteTypesList.setModel(spriteTypesListModel);
        spriteTypesListJSP = new JScrollPane(spriteTypesList);
        
        // OUR COMBO BOX STARTS OUT EMPTY
        spriteStateComboBoxModel = new DefaultComboBoxModel();        
        spriteStateCombobox = new JComboBox();
        spriteStateCombobox.setModel(spriteStateComboBoxModel);
        //Adds the SELECT_ANIMATION_TEXT element
        spriteStateComboBoxModel.addElement(SELECT_ANIMATION_TEXT);
        //Temporarily disables the ComboBox until the list of sprites is clicked on.
        spriteStateCombobox.setEnabled(false);
        
        // NOW LET'S ARRANGE ALL OUR CONTROLS IN THE WEST
        westOfSouthPanel = new JPanel();
        westOfSouthPanel.setLayout(new BorderLayout());
        westOfSouthPanel.add(spriteTypesList, BorderLayout.NORTH);
        westOfSouthPanel.add(spriteStateCombobox, BorderLayout.SOUTH);
        
        // AND LET'S PUT A TITLED BORDER AROUND THE WEST OF THE SOUTH
        Border etchedBorder = BorderFactory.createEtchedBorder();
        Border titledBorder = BorderFactory.createTitledBorder(etchedBorder, "Sprite Type");
        westOfSouthPanel.setBorder(titledBorder);       
        
        // NOW THE STUFF FOR THE SOUTH INCLUDING ALL THE BUTTONS
        animationToolbar = new JPanel();         
        MediaTracker mt = new MediaTracker(this);
        startButton = initButton(   "StartAnimationButton.png",     "Start Animation",      mt, 0, animationToolbar);
        stopButton = initButton(   "StopAnimationButton.png",     "Start Animation",      mt, 0, animationToolbar);
        slowDownButton = initButton(   "SlowDownAnimationButton.png",     "Start Animation",      mt, 0, animationToolbar);
        speedUpButton = initButton(   "SpeedUpAnimationButton.png",     "Start Animation",      mt, 0, animationToolbar);
        try{
            mt.waitForAll(); 
        }
        catch(InterruptedException ie){
            ie.printStackTrace(); 
        }

        // LET'S PUT OUR STUFF IN THE SOUTH
        southPanel = new JPanel();
        southPanel.add(westOfSouthPanel);
        southPanel.add(animationToolbar);
        
        
        //make north and south
        //add to west and east
        
        
        // AND OF COURSE OUR RENDERING PANEL
        sceneRenderingPanel = new SceneRenderer(spriteList);
        sceneRenderingPanel.setBackground(Color.white);
        sceneRenderingPanel.startScene();
        sceneRenderingPanel.unpauseScene();
        
        // AND LET'S ARRANGE EVERYTHING IN THE FRAME
        add(sceneRenderingPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }
    
    /**
     * This helper method fills the combo box with the poses of the sprite clicked on and gets the attributes for later.
     * 
     * @param indexOfName Contains the index of the name of the sprite to load the poses of into the combo box.
     */
    public void fillComboBox(int indexOfName)
    {
        spriteStateComboBoxModel.removeAllElements();
        spriteStateComboBoxModel.addElement(SELECT_ANIMATION_TEXT);
        //INITIALIZE THE ARRAYLIST THAT loadSpriteAnimationStatesAndAttributes needs
        spriteAnimationStates = new ArrayList<String>();
        spriteAnimationAttributes = new ArrayList<String[][]>();
        //USE THESE STRINGS FOR PARAMETERS IN loadSpriteAnimationStatesAndAttributes
        spriteType = spriteTypeNames.get(indexOfName);
        directoryOfSprite = SPRITES_DATA_PATH;
        directoryOfSprite += spriteType;
        directoryOfSprite += "/";
        xmlOfSpriteType = spriteType; 
        xmlOfSpriteType += ".xml";
        try{
            // THIS WILL LOAD AND VALIDATE
            // OUR XML FILES
            xmlLoader = new AnimatedSpriteXMLLoader(this);
            // FIRST UP ARE THE ANIMATIONS STATES AND ATTRIBUTES
            xmlLoader.loadSpriteAnimationStatesAndAttributes(directoryOfSprite, xmlOfSpriteType, spriteAnimationStates, spriteAnimationAttributes);        
        }
        catch(InvalidXMLFileFormatException ixffe){
            // IF WE DON'T HAVE A VALID SPRITE TYPE 
            // LIST WE HAVE NOTHING TO DO, WE'LL POP
            // OPEN A DIALOG BOX SO THE USER KNOWS
            // WHAT HAPPENED
            JOptionPane.showMessageDialog(this, ixffe.toString());
            System.exit(0);
        }
        //PUT ALL OF THE ANIMATION STATES INTO THE COMBO BOX
        for(int eachAnimationState=0;eachAnimationState<spriteAnimationStates.size();eachAnimationState++)
            spriteStateComboBoxModel.addElement(spriteAnimationStates.get(eachAnimationState));
        //Put a listener on the combo box
        MeyeActionListener actionListener = new MeyeActionListener(this);
        spriteStateCombobox.addActionListener(actionListener);
        spriteStateCombobox.setEnabled(true);
    }
    
    /**
     * This is a helper method for making a button. It loads the image and sets
     * it as the button image. It then puts it in the panel.
     * 
     * @param iconFilename Image for button
     * @param tooltip Tooltip for button
     * @param mt Used for batch loading of images
     * @param id Numeric id of image to help with batch loading
     * @param panel The container to place the button into
     * 
     * @return The fully constructed button, ready for use.
     */
    private JButton initButton(String iconFilename, String tooltip, MediaTracker mt, int id, JPanel panel)
    {
        // LOAD THE IMAGE
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image img = tk.getImage(GUI_IMAGES_PATH + iconFilename);
        mt.addImage(img, id);
        
        // AND USE IT TO BUILD THE BUTTON
        ImageIcon ii = new ImageIcon(img);
        JButton button = new JButton(ii);
        button.setToolTipText(tooltip);
        
        // LET'S PUT A LITTLE BUFFER AROUND THE IMAGE AND THE EDGE OF THE BUTTON
        Insets insets = new Insets(2,2,2,2);
        button.setMargin(insets);
        
        // PUT THE BUTTON IN THE CONTAINER
        panel.add(button);
        
        // AND SEND THE CONSTRUCTED BUTTON BACK
        return button;
    }
    
    /**
     * This helper links up all the components with their event
     * handlers, ensuring the proper responses.
     */
    private void initHandlers()
    {
        /* Note the handler for the combo box has to be added after the JListHandler
         * is clicked on so it is within the method fillComboBox().
         */
        
        // CONSTRUCT AND REGISTER ALL THE HANDLERS FOR THE JLIST
        JListHaandler jListListener = new JListHaandler(this);
        spriteTypesList.addMouseListener(jListListener);
        // CONSTRUCT AND REGISTER ALL THE HANDLERS FOR THE BUTTONS
        StartAnimationHandler startah = new StartAnimationHandler(sceneRenderingPanel);
        startButton.addActionListener(startah);
        StopAnimationHandler stopah = new StopAnimationHandler(sceneRenderingPanel);
        stopButton.addActionListener(stopah);
        SlowDownAnimationHaandler slowah = new SlowDownAnimationHaandler(sceneRenderingPanel);
        slowDownButton.addActionListener(slowah);
        SpeedUpAnimationHaandler speedah = new SpeedUpAnimationHaandler(sceneRenderingPanel);
        speedUpButton.addActionListener(speedah);
    }
    /**
     * This helper method fills the combo box with the poses of the sprite clicked on and gets the attributes for later.
     * 
     * @param indexOfName Contains the index of the name of the sprite to load the poses of into the combo box.
     */
    public ArrayList<String> getFileNames(ArrayList<Integer> numberOfImagesForEachState)
    {
        ArrayList<String> names = new ArrayList<String>();
        try{
            // THIS WILL LOAD AND VALIDATE
            // OUR XML FILES
            xmlLoader = new AnimatedSpriteXMLLoader(this);
            //NEXT UP ARE THE NUMBERS OF IMAGES FOR EVERY STATE
            xmlLoader.loadFileNames(directoryOfSprite, xmlOfSpriteType, numberOfImagesForEachState,names);        
        }
        catch(InvalidXMLFileFormatException ixffe){
            // IF WE DON'T HAVE A VALID SPRITE TYPE 
            // LIST WE HAVE NOTHING TO DO, WE'LL POP
            // OPEN A DIALOG BOX SO THE USER KNOWS
            // WHAT HAPPENED
            JOptionPane.showMessageDialog(this, ixffe.toString());
            System.exit(0);
        }
        return names;
    }
    /**
     * This helper method loads our player, including its art and poses
     * and then initializing the player sprite and then adding it to
     * the scene.
     */
    public void loadSprite(String state,String type_man, String pathOfMan)
    {
        //Loads the spirte type
        SpriteType duhSprite = loadSpriteType(pathOfMan);//type_man,
        
        //Turns the string state into the AnimationState state
        AnimationState aniState = AnimationState.valueOf(state);
        
        // NOW LET'S INIT OUR PLAYER SPRITE
        Sprite player = new Sprite(duhSprite, aniState);
        player.setPositionX(250);
        player.setPositionY(250);
        player.setVelocityX(0);
        player.setVelocityY(0);
        
        //CLEARS DUH SPRITE LIST SO ONLY ONE GETS ANIMATED
        spriteList.clear();//yeahhh
        // AND PUT THE PLAYER IN THE SCENE
        spriteList.add(player);
    }
    
   private SpriteType loadSpriteType(String pathOfMan)//String type_man, 
   {
        // WE'LL USE THESE TO INITIALIZE OUR SPRITE TYPE
        ArrayList<AnimationState> listOfAnimationStates = new ArrayList<AnimationState>();
        
        
        
        
        //Adds all of those animation states in Animation State form (not String form like spriteAnimationStates)
        for(int index=0;index<spriteAnimationStates.size();index++)
            listOfAnimationStates.add(AnimationState.valueOf(spriteAnimationStates.get(index)));
        
        // WE NEED THE TRACKER TO ENSURE FULL IMAGE LOADING
        MediaTracker tracker = new MediaTracker(this);
        
        // AND HERE'S THE ACTUAL SPRITE TYPE
        SpriteType man = new SpriteType();
        
        
        
        
        //Add all of the images
        for (int eachAnimationState = 0; eachAnimationState < listOfAnimationStates.size(); eachAnimationState++){
            // CREATE A NEW LIST
            PoseList poseList = man.addPoseList(listOfAnimationStates.get(eachAnimationState));
            //Get the two-dimensional attribute array for each animation state.
            String[][] array = spriteAnimationAttributes.get(eachAnimationState);
            //For every imageID there is going to be a duration so we'll just get the length of how many imageIDs there are.
            String[] imageIDsArray = array[1];
            int howManyImageIDs = imageIDsArray.length;
            
            //For how ever many durations/imageIDs there are add them to the postList.
            for(int eachAttribute=0;eachAttribute<howManyImageIDs;eachAttribute++)
                poseList.addPose(Integer.parseInt(array[1][eachAttribute]),Integer.parseInt(array[0][eachAttribute]));
            ArrayList<Integer> imageIDsArrayList = new ArrayList<Integer>();
            for(int i=0;i<howManyImageIDs;i++)
                imageIDsArrayList.add(Integer.parseInt(imageIDsArray[i]));
            ArrayList<Integer> uniqueIDList = new ArrayList<Integer>();
            for(int eachImageID=0;eachImageID<howManyImageIDs;eachImageID++)
                if(!uniqueIDList.contains(imageIDsArrayList.get(eachImageID)))
                    uniqueIDList.add(imageIDsArrayList.get(eachImageID));
            
            //Make sure the file names are in sync with the imageIDs
            Collections.sort(uniqueIDList);
            //Get's the number of images for each state.
            ArrayList<String> names = getFileNames(uniqueIDList);
            
            // AND NOW LOAD THE IMAGES
            for (int i=0; i<names.size(); i++)
            {
                //Get file name
                String fileName = names.get(i);
                // LOAD THE IMAGE
                Image img = loadImageInBatch(pathOfMan, fileName, tracker, uniqueIDList.get(i));
                
                // GIVE IT TO THE SPRITE TYPE
                man.addImage(uniqueIDList.get(i), img);
            }
        }
        
        // WE BATCH LOADED THE SPRITES. BELOW THE MEDIA TRACKER
        // MAKES SURE ALL OF THEM ARE FULLY IN MEMORY BEFORE
        // WE GO ON. IT WAITS FOR THEM SO THAT THE APP DOESN'T
        // START WITHOUT THEM. THIS IS BECAUSE Java's METHODS
        // FOR LOADING AN IMAGE WORK ASYNCHRONOUSLY IN THE BACKGROUND,
        // i.e. ANOTHER THREAD
        try
        {
            tracker.waitForAll();
        }
        catch(InterruptedException ie)
        {
            // THIS SHOULD NEVER HAPPEN, BUT WE SHOULD AT LEAST PRINT THE
            // STACK TRACE JUST IN CASE THE IMPOSSIBLE IS MADE POSSIBLE
            ie.printStackTrace();
        }
        
        // RETURN OUR NEWLY CONSTRUCTED SPRITE TYPE FOR USE
        return man; 
    }
   
    /**
     * This method is used to load an individual image among many
     * in a batch. The reason for batch loading is to use a single
     * MediaTracker to ensure that all images in the batch are fully
     * loaded before continuing.
     * 
     * @param path Relative path to the image location from the working directory.
     * 
     * @param fileName File name of the image to load
     * 
     * @param tracker The MediaTracker to ensure the image is fully loaded.
     * 
     * @param id A unique number for the image so the tracker can identify it.
     * 
     * @return A reference to the Image represented by the path + fileName.
     */
    private Image loadImageInBatch( String path,
                                    String fileName,
                                    MediaTracker tracker,
                                    int id)
    {
        Toolkit tk = Toolkit.getDefaultToolkit();//CToolKit
        String fullFileNameWithPath = path + fileName;
        System.out.println("hi");

        Image ibsfmwl = tk.getImage(fullFileNameWithPath);
        Image img = tk.getImage(fullFileNameWithPath);
        tracker.addImage(img, id);
        return img;
    }
    
    /**
     * This is where this app starts. The main method just constructs
     * the frame and then sets it visible, handing off control to Swing.
     * 
     * @param args 
     */
    public static void main(String[] args)
    {
        // START IT UP
        AnimatedSpriteViewer appWindow = new AnimatedSpriteViewer();
        appWindow.setVisible(true);
    }
}