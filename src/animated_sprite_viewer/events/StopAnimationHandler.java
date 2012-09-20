package animated_sprite_viewer.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import sprite_renderer.SceneRenderer;

/**
 * The StopAnimationHandler class responds to when the user
 * requests to stop animation.
 * 
 * @author  Kevin Hock
 */

public class StopAnimationHandler implements ActionListener
{
    private SceneRenderer renderer;
    
    /**
     * Constructor will need the renderer for when the event happens.
     * 
     * @param initRenderer Renderers can pause and unpause the rendering.
     */
    public StopAnimationHandler(SceneRenderer initRenderer)
    {
        renderer = initRenderer;
    }    
    
    /**
     * Here's the actual method called when the user clicks the 
     * stop animation button, which results in the pausing of the
     * renderer, and thus the animator as well.
     * 
     * @param ae Contains information about the event.
     */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        renderer.pauseScene();
    }
}