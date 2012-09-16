package animated_sprite_viewer;

import animated_sprite_viewer.events.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.border.Border;
import sprite_renderer.AnimationState;
import sprite_renderer.PoseList;//added by me
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
 * @author  Richard McKenna &
 *          Debugging Enterprises
 * @version 1.0
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
 
    // WE'LL ONLY ACTUALLY HAVE ONE SPRITE AT A TIME IN HERE,
    // THE ONE THAT WE ARE CURRENTLY VIEWING
    private ArrayList<Sprite> spriteList;
    
    // WE'LL LOAD ALL THE SPRITE TYPES INTO LIST
    // FROM AN XML FILE
    private ArrayList<String> spriteTypeNames;

    // THIS WILL DO OUR XML FILE LOADING FOR US
    private AnimatedSpriteXMLLoader xmlLoader;

    // THE WEST WILL PROVIDE SPRITE TYPE AND ANIM STATE SELECTION CONTROLS
    private JPanel westOfSouthPanel;

    // THIS WILL STORE A SELECTABLE LIST OF THE LOADED SPRITES
    private JScrollPane spriteTypesListJSP;
    private JList spriteTypesList;
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
        setTitle(APP_TITLE);//This is a constant is I set up there
        setSize(700, 700);//was like 500 700 or something
        setLocation(0, 0);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * Loads all the data needed for the sprite types. Note that we'll
     * need all the sprite states, including their art and animations,
     * loaded before we initialize the GUI.
     */
    private void initData()
    {
        //spriteTypeNames and xmlLoader gets filled but not spriteList
        
        
        
        // WE'LL ONLY PUT ONE SPRITE IN THIS
        spriteList = new ArrayList<Sprite>();
        
        // WE'LL PUT ALL THE SPRITE TYPES HERE
        spriteTypeNames = new ArrayList<String>();
        
        loadSprite();
        
        // LOAD THE SPRITE TYPES FROM THE XML FILE
        try
        {
            // THIS WILL LOAD AND VALIDATE
            // OUR XML FILES
            xmlLoader = new AnimatedSpriteXMLLoader(this);
            
            // FIRST UP IS THE SPRITE TYPES LIST
            xmlLoader.loadSpriteTypeNames(SPRITES_DATA_PATH,
                             SPRITE_TYPE_LIST_FILE, spriteTypeNames);           
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
        for(int p =0;p<4;p++){}
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
        clearAnimationStatesComboBox();

        // NOW LET'S ARRANGE ALL OUR CONTROLS IN THE WEST
        westOfSouthPanel = new JPanel();
        westOfSouthPanel.setLayout(new BorderLayout());
        westOfSouthPanel.add(spriteTypesList, BorderLayout.NORTH);
        westOfSouthPanel.add(spriteStateCombobox, BorderLayout.SOUTH);
        
        // AND LET'S PUT A TITLED BORDER AROUND THE WEST OF THE SOUTH
        Border etchedBorder = BorderFactory.createEtchedBorder();
        Border titledBorder = BorderFactory.createTitledBorder(etchedBorder, "Sprite Type Selection");
        westOfSouthPanel.setBorder(titledBorder);       

        // NOW THE STUFF FOR THE SOUTH
        animationToolbar = new JPanel();         
        MediaTracker mt = new MediaTracker(this);
        startButton = initButton(   "StartAnimationButton.png",     "Start Animation",      mt, 0, animationToolbar);
        stopButton = initButton(   "StopAnimationButton.png",     "Start Animation",      mt, 0, animationToolbar);
        slowDownButton = initButton(   "SlowDownAnimationButton.png",     "Start Animation",      mt, 0, animationToolbar);
        speedUpButton = initButton(   "SpeedUpAnimationButton.png",     "Start Animation",      mt, 0, animationToolbar);
        //
        //
        //
        //
        try { mt.waitForAll(); }
        catch(InterruptedException ie)
        { ie.printStackTrace(); }

        // LET'S PUT OUR STUFF IN THE SOUTH
        southPanel = new JPanel();
        southPanel.add(westOfSouthPanel);
        southPanel.add(animationToolbar);
        
        // AND OF COURSE OUR RENDERING PANEL
        sceneRenderingPanel = new SceneRenderer(spriteList);
        sceneRenderingPanel.setBackground(Color.white);
        sceneRenderingPanel.startScene();
        
        // AND LET'S ARRANGE EVERYTHING IN THE FRAME
        add(sceneRenderingPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * This helper method empties the combo box with animations
     * and disables the component.
     */
    private void clearAnimationStatesComboBox()
    {
        spriteStateComboBoxModel.removeAllElements();
        spriteStateComboBoxModel.addElement(SELECT_ANIMATION_TEXT);        
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
        // CONSTRUCT AND REGISTER ALL THE HANDLERS
        StartAnimationHandler sah = new StartAnimationHandler(sceneRenderingPanel);
        startButton.addActionListener(sah);
        stopButton.addActionListener(sah);
        slowDownButton.addActionListener(sah);
        speedUpButton.addActionListener(sah);
    }
    /**
     * This helper method loads our player, including its art and poses
     * and then initializing the player sprite and then adding it to
     * the scene.
     */
    private void loadSprite()
    {
        SpriteType duhSprite = loadSpriteType();
        
        // NOW LET'S INIT OUR PLAYER SPRITE
        Sprite player = new Sprite(duhSprite, AnimationState.IDLE);
        player.setPositionX(250);
        player.setPositionY(250);
        player.setVelocityX(0);
        player.setVelocityY(0);
        
        // AND PUT THE PLAYER IN THE SCENE
        spriteList.add(player);
    }
    
    /**
     * This method hard-codes the construction of a sprite type
     * for representing a box man sprite. 
     * 
     * @return A constructed and fully setup box man sprite.
     */
    /*
     * still no difference to explain the fuckup
     * 
     */
    private SpriteType loadSpriteType()
    {
        // WE'LL USE THESE TO INITIALIZE OUR SPRITE TYPE
        String prefix = "round_man";
        String path = "./data/sprite_types/" + prefix + "/";
        int idCounter = 1;
        AnimationState[] roundManStates = { AnimationState.IDLE,
                                            AnimationState.BOUNCING};                               
        
        // WE NEED THE TRACKER TO ENSURE FULL IMAGE LOADING
        MediaTracker tracker = new MediaTracker(this);
        
        // AND HERE'S THE ACTUAL SPRITE TYPE
        SpriteType roundManType = new SpriteType();

        // AGAIN, WE ARE WAY OVER-SIMPLIFYING THE INITIALIZATION
        // OF THIS DATA. THIS SHOULD REALLY BE DONE FROM A FILE.
        // HERE WE'RE JUST MAKING A COUPLE OF SIMPLE PoseLists, 
        // ONE FOR EACH STATE
        for (int i = 0; i < roundManStates.length; i++)
        {
            // NOTICE THAT WE HAVE HARD-CODED THE POSES,
            // i.e. THE IMAGE ID/DURATION NUMBERS BELOW
            
            // CREATE A NEW LIST
            PoseList poseList = roundManType.addPoseList(roundManStates[i]);

            // AND ADD THE POSES. THE midPoseID VALUE IS JUST 
            // A SILLY LITTLE MECHANISM TO HARD-CODE FIVE
            // IMAGES TOGETHER INTO A POSE SEQUENCE (PoseList)
            int midPoseID = idCounter + 2;
            poseList.addPose(midPoseID,     15);
            poseList.addPose(midPoseID+1,    5);
            poseList.addPose(midPoseID+2,   10);
            poseList.addPose(midPoseID+1,    5);
            poseList.addPose(midPoseID,     15);
            poseList.addPose(midPoseID-1,    5);
            poseList.addPose(midPoseID-2,   10);
            poseList.addPose(midPoseID-1,    5);

            // AND NOW LOAD THE IMAGES
            for (int j = 1; j <= 5; j++)
            {
                // THE IAMGE NAMES ARE PREDICABLE
                String fileName = prefix + "_"
                        + roundManStates[i] + "_" + j + ".png";
                
                // LOAD THE IMAGE
                Image img = loadImageInBatch(path, fileName, tracker, idCounter);
                
                // GIVE IT TO THE SPRITE TYPE
                roundManType.addImage(idCounter, img);
                idCounter++;////////
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
        return roundManType;
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