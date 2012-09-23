package animated_sprite_viewer.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import sprite_renderer.SceneRenderer;

/**
 * The SpeedUpAnimationHandler class responds to when the user
 * requests to speed up animation.
 * 
 * @author  Kevin Hock
 */

public class SpeedUpAnimationHaandler implements ActionListener
{
    private SceneRenderer renderer;
    
    /**
     * Constructor will need the renderer for when the event happens.
     * 
     * @param initRenderer Renderers can pause and unpause the rendering.
     */
    public SpeedUpAnimationHaandler(SceneRenderer initRenderer)
    {
        renderer = initRenderer;
    }    
    
    /**
     * Here's the actual method called when the user clicks the 
     * speedUp animation button, which results in speeding up of the
     * renderer, and thus the animator as well.
     * 
     * @param ae Contains information about the event.
     */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        renderer.setTimeScaler((float).8*renderer.getTimeScaler());
    }
}