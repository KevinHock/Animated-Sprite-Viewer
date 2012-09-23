package animated_sprite_viewer;

import animated_sprite_viewer.events.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
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
    /***
     * Go over the global variables and describe their use
     * Put the two listeners in their own classes.
     * Should I sceneRenderingPanel.unpauseScene(); after it's clicked?
     * Go over the comments in the handler classes.
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     */
    
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
 
    // WE'LL ONLY ACTUALLY HAVE ONE SPRITE AT A TIME IN HERE,
    // THE ONE THAT WE ARE CURRENTLY VIEWING
    private ArrayList<Sprite> spriteList;
    
    // WE'LL LOAD ALL THE SPRITE TYPES INTO LIST
    // FROM AN XML FILE
    private ArrayList<String> spriteTypeNames;
    
    //WE'LL LOAD ALL THE SPRITE TYPES INTO LIST
    //FROM AN XML FILE
    //Unsure
    private ArrayList<String> spriteAnimationStates;
    
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
    
    private String directoryOfSprite,spriteType;
    
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
        //Adds the SELECT_ANIMATION_TEXT element and temporarily disables the ComboBox until the list of sprites is clicked on.
        clearAnimationStatesComboBox(false,0);
        
        
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
        
        // AND OF COURSE OUR RENDERING PANEL
        sceneRenderingPanel = new SceneRenderer(spriteList);
        sceneRenderingPanel.setBackground(Color.white);
        sceneRenderingPanel.startScene();
        
        //Unsure of whether or not to render right after the user clicks on the combo box for the first time.
        sceneRenderingPanel.unpauseScene();
        
        // AND LET'S ARRANGE EVERYTHING IN THE FRAME
        add(sceneRenderingPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }
    
    /**
     * This helper method empties the combo box with animations
     * and disables the component.
     * 
     * @param fromMouse Whether or not it was called from mouseListener
     * @param indexOfName Contains the index of the name of the sprite to load the poses of into the combo box.
     */
    public void clearAnimationStatesComboBox(boolean fromMouse, int indexOfName)
    {
        spriteStateComboBoxModel.removeAllElements();
        spriteStateComboBoxModel.addElement(SELECT_ANIMATION_TEXT);
        //For when the user clicks on the JList
        if(fromMouse){
            //INITIALIZE THE ARRAYLIST THAT loadSpriteAnimationStatesAndAttributes needs
            spriteAnimationStates = new ArrayList<String>();
            spriteAnimationAttributes = new ArrayList<String[][]>();
            //USE THESE STRINGS FOR PARAMETERS IN loadSpriteAnimationStatesAndAttributes
            spriteType = spriteTypeNames.get(indexOfName);
            directoryOfSprite = SPRITES_DATA_PATH;
            directoryOfSprite += spriteType;
            directoryOfSprite += "/";
            String xmlOfSpriteType = spriteType; 
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
            class MyActionListener implements ActionListener {
                public void actionPerformed(ActionEvent evt) {
                JComboBox cb = (JComboBox)evt.getSource();
                Object item = cb.getSelectedItem();
                if(item!=null)
                    for(int eachAnimationState=0;eachAnimationState<spriteAnimationStates.size();eachAnimationState++)
                        if(item.equals(spriteAnimationStates.get(eachAnimationState)))
                            loadSprite((String)item,spriteType,directoryOfSprite);
                }
            }
            ///reeeeeeel sloppy Listeners
            MyActionListener actionListener = new MyActionListener();
            spriteStateCombobox.addActionListener(actionListener);
            spriteStateCombobox.setEnabled(true);
        }
        //For when the ComboBox is first made.
        else
            spriteStateCombobox.setEnabled(false);
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
     * This helper method loads our player, including its art and poses
     * and then initializing the player sprite and then adding it to
     * the scene.
     */
    private void loadSprite(String state,String type_man, String pathOfMan)
    {
        SpriteType duhSprite = loadSpriteType(type_man,pathOfMan);
        
        AnimationState cool = AnimationState.valueOf(state);
        
        // NOW LET'S INIT OUR PLAYER SPRITE
        Sprite player = new Sprite(duhSprite, cool);
        player.setPositionX(250);
        player.setPositionY(250);
        player.setVelocityX(0);
        player.setVelocityY(0);
        
        //CLEARS DUH SPRITE LIST SO ONLY ONE GETS ANIMATED
        spriteList.clear();//yeahhh
        // AND PUT THE PLAYER IN THE SCENE
        spriteList.add(player);
    }
    
   private SpriteType loadSpriteType(String type_man, String pathOfMan)
    {
        // WE'LL USE THESE TO INITIALIZE OUR SPRITE TYPE
        int idCounter = 1;
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
            //For every imageID there is going to be a duration so we'll just get the length of how many durations there are.
            String[] anotherArray = array[0];
            int howManyDurations = anotherArray.length;
            
            //For how ever many durations/imageIDs there are add them to the postList.
            for(int eachAttribute=0;eachAttribute<howManyDurations;eachAttribute++)
                poseList.addPose(Integer.parseInt(array[1][eachAttribute]),Integer.parseInt(array[0][eachAttribute]));
            
            // AND NOW LOAD THE IMAGES
            for (int pngNumber = 1; pngNumber<= 5; pngNumber++)
            {
                // THE IMAGE NAMES ARE PREDICABLE
                String fileName = type_man + "_"
                        + listOfAnimationStates.get(eachAnimationState) + "_" + pngNumber + ".png";
                
                // LOAD THE IMAGE
                Image img = loadImageInBatch(pathOfMan, fileName, tracker, idCounter);
                
                // GIVE IT TO THE SPRITE TYPE
                man.addImage(idCounter, img);
                idCounter++;
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
        Toolkit tk = Toolkit.getDefaultToolkit();
        String fullFileNameWithPath = path + fileName;
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