package core;

import com.jme3.asset.AssetManager;
import com.jme3.texture.Image;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import jme3tools.converters.ImageToAwt;

/**
 *
 * @author roah
 */
public class ComboBoxRenderer extends JLabel implements ListCellRenderer {

    private final AssetManager manager;
    private List<String> textureKeys;
    private Font uhOhFont;
    private final ImageIcon[] images;
    private final Integer[] intArray;

    public ComboBoxRenderer(AssetManager manager, List<String> textureKeys) {
        this.manager = manager;
        this.textureKeys = textureKeys;

        //Load the pet images and create an array of indexes.
        images = new ImageIcon[textureKeys.size()];
        intArray = new Integer[textureKeys.size()];
        for (int i = 0; i < textureKeys.size(); i++) {
            intArray[i] = new Integer(i);
            images[i] = createImageIcon(textureKeys.get(i));
//            images[i].getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_DEFAULT);
            if (images[i] != null) {
                images[i].setDescription(textureKeys.get(i));
            }
        }
    }

    public Integer[] getArray() {
        return intArray;
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    private ImageIcon createImageIcon(String name) {
        Image img = manager.loadTexture(
                "Textures/Icons/Buttons/" + name + ".png").getImage();
//        java.net.URL imgURL = CustomComboBoxDemo.class.getResource(path);
        if (img != null) {
            BufferedImage imgConvert = createFlipped(ImageToAwt.convert(img, false, false, 0));
            return new ImageIcon(imgConvert.getScaledInstance(18, 18, java.awt.Image.SCALE_FAST));
        } else {
            System.err.println("Couldn't find img : " + name);
            return null;
        }
    }

    private static BufferedImage createFlipped(BufferedImage image) {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(1, -1));
        at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
        return createTransformed(image, at);
    }

    private static BufferedImage createTransformed(BufferedImage image, AffineTransform at) {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    /*
     * This method finds the image and text corresponding
     * to the selected value and returns the label, set up
     * to display the text and image.
     */
    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        //Get the selected index. (The index param isn't
        //always valid, so just use the value.)
        int selectedIndex = ((Integer) value).intValue();

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        //Set the icon and text.  If icon was null, say so.
        ImageIcon icon = images[selectedIndex];
        String texture = textureKeys.get(selectedIndex);
        setIcon(icon);
        if (icon != null) {
            setText(texture);
            setFont(list.getFont());
        } else {
            setUhOhText(texture + " (no image available)",
                    list.getFont());
        }

        return this;
    }

    //Set the font and text when no image was found.
    protected void setUhOhText(String uhOhText, Font normalFont) {
        if (uhOhFont == null) { //lazily create this font
            uhOhFont = normalFont.deriveFont(Font.ITALIC);
        }
        setFont(uhOhFont);
        setText(uhOhText);
    }
}
