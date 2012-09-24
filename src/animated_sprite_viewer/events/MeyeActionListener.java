package animated_sprite_viewer.events;

import animated_sprite_viewer.AnimatedSpriteViewer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;

//ActionListener for the combobox
public class MeyeActionListener implements ActionListener {
    private AnimatedSpriteViewer asv;
    public MeyeActionListener(AnimatedSpriteViewer asv){
        this.asv = asv;
    }
    public void actionPerformed(ActionEvent evt) {
        JComboBox cb = (JComboBox)evt.getSource();
        Object item = cb.getSelectedItem();
        //If the event clicked on isn't null go through every animation state and see which one the user clicked on.
        //Then load the sprite with that animation state.
        if(item!=null)
            for(int eachAnimationState=0;eachAnimationState<asv.spriteAnimationStates.size();eachAnimationState++)
                if(item.equals(asv.spriteAnimationStates.get(eachAnimationState)))
                    asv.loadSprite((String)item,asv.spriteType,asv.directoryOfSprite);
    }
}