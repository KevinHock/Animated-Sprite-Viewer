package animated_sprite_viewer.events;

import animated_sprite_viewer.AnimatedSpriteViewer;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * The JListHandler class responds to when the user
 * chooses the sprite type they want to see.
 * 
 * @author  Kevin Hock
 */
public class JListHaandler implements MouseListener{
    private AnimatedSpriteViewer asv;
    
    /**
     * Constructor will need the AnimatedSpriteViewer.
     * 
     * @param asv AnimatedSpriteViewer to give access to all of it's methods non-statically.
     */
    public JListHaandler(AnimatedSpriteViewer asv)
    {
        this.asv = asv;
    }
    /**
     * Here's the actual method called when the user clicks on the JList.
     * 
     * @param ae Contains information about what sprite type the user clicks.
     */
    @Override
    public void mouseClicked(MouseEvent me) {
        if (me.getClickCount() == 1) {
                    int index = asv.spriteTypesList.locationToIndex(me.getPoint());
                    asv.clearAnimationStatesComboBox(true, index);
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {}

    @Override
    public void mouseReleased(MouseEvent me) {}

    @Override
    public void mouseEntered(MouseEvent me) {}

    @Override
    public void mouseExited(MouseEvent me) {}
}
