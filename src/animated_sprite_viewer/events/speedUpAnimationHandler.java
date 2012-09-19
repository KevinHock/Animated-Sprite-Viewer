package animated_sprite_viewer.events;

//import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import sprite_renderer.SceneRenderer;

/**
 * The SlowAnimationHandler class responds to when the user
 * requests to start animation.
 * 
 * @author  Richard McKenna
 *          Debugging Enterprises
 * @version 1.0
 */

public class speedUpAnimationHandler implements ActionListener
{
    // THIS IS REALLY THE ONLY ONE WHO CAN PAUSE OR UNPAUSE ANIMATION
    private SceneRenderer renderer;
    
    /**
     * Constructor will need the renderer for when the event happens.
     * 
     * @param initRenderer Renderers can pause and unpause the rendering.
     */
    public speedUpAnimationHandler(SceneRenderer initRenderer)
    {
        // KEEP THIS FOR LATER
        renderer = initRenderer;
        //System.out.println("blah");
        //renderer.setBackground(Color.green);
        //if it was slow down button then slow down...
    }    
    /**
     * Here's the actual method called when the user clicks the 
     * start animation method, which results in unpausing of the
     * renderer, and thus the animator as well.
     * 
     * @param ae Contains information about the event.
     */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        //System.out.println("i just changed unpause to pause");
        renderer.setTimeScaler((float).5);
        renderer.unpauseScene();
    }
}