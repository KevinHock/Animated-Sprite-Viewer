/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
package animated_sprite_viewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JComboBox;

/**
 *
 * @author Kevin
 *//*
public class jaeMyActionListener implements ActionListener{

    private ArrayList spriteAnimationStates;
    
    public jaeMyActionListener(ArrayList spriteAnimationStates)
    {
        this.spriteAnimationStates = spriteAnimationStates;
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        JComboBox cb = (JComboBox)evt.getSource();
        Object item = cb.getSelectedItem();
        if(item!=null)
            for(int dowd=0;dowd<spriteAnimationStates.size();dowd++)
                if(item.equals(spriteAnimationStates.get(dowd)))
                    loadSprite((String)item,man,mons);
    }
    
    public void actionPerformed(ActionEvent evt, int v) {
        JComboBox cb = (JComboBox)evt.getSource();
        Object item = cb.getSelectedItem();
        if(item!=null)
            for(int dowd=0;dowd<spriteAnimationStates.size();dowd++)
                if(item.equals(spriteAnimationStates.get(dowd)))
                    loadSprite((String)item,man,mons);
    }
}
*/