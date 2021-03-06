package animated_sprite_viewer.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import sprite_renderer.SceneRenderer;

/**
 * The SlowAnimationHandler class responds to when the user
 * requests to slow animation.
 * 
 * @author  Kevin Hock
 */

public class SlowDownAnimationHaandler implements ActionListener
{
    private SceneRenderer renderer;
    
    /**
     * Constructor will need the renderer for when the event happens.
     * 
     * @param initRenderer Renderers can pause and unpause the rendering.
     */
    public SlowDownAnimationHaandler(SceneRenderer initRenderer)
    {
        renderer = initRenderer;
    }    
    
    /**
     * Here's the actual method called when the user clicks the 
     * slow animation button, which results in the slowing down of the
     * renderer, and thus the animator as well.
     * 
     * @param ae Contains information about the event.
     */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        renderer.setTimeScaler((float)1.2*renderer.getTimeScaler());
    }
}