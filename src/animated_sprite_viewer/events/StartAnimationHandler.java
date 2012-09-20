package animated_sprite_viewer.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import sprite_renderer.SceneRenderer;

/**
 * The StartAnimationHandler class responds to when the user
 * requests to start animation.
 * 
 * @author  Kevin Hock
 */

public class StartAnimationHandler implements ActionListener
{
    private SceneRenderer renderer;
    
    /**
     * Constructor will need the renderer for when the event happens.
     * 
     * @param initRenderer Renderers can pause and unpause the rendering.
     */
    public StartAnimationHandler(SceneRenderer initRenderer)
    {
        renderer = initRenderer;
    }    
    /**
     * Here's the actual method called when the user clicks the 
     * start animation button, which results in the unpausing of the
     * renderer, and thus the animator as well.
     * 
     * @param ae Contains information about the event.
     */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        renderer.unpauseScene();
    }
}