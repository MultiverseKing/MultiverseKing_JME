package gui;

import com.jme3.texture.Image;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import utility.ImageConverter;

/**
 *
 * @author roah
 */
public abstract class JPropertiesPanel extends JPanel {
    
    private ImageIcon imgIcon;

    public JPropertiesPanel(Image img, String name) {
        this(ImageConverter.convertToIcon(img, 16, 16), name);
    }
    
    public JPropertiesPanel(ImageIcon icon, String name) {
        this.imgIcon = icon;
        super.setName(name);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setAlignmentX(0);
    }
    
    public ImageIcon getIcon(){
        return imgIcon;
    }

    protected void addComp(Component comp) {
        addComp(null, comp);
    }

    protected void addComp(JPanel pan, Component comp) {
        if (pan != null) {
            pan.add(Box.createRigidArea(new Dimension(0, 3)));
            pan.add(comp);
        } else {
            add(Box.createRigidArea(new Dimension(0, 3)));
            add(comp);
        }
    }
    
    public abstract void isShow();

    public abstract void isHidden();
}
