package utility;

import com.jme3.texture.Image;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import jme3tools.converters.ImageToAwt;

/**
 * Convert texture loaded using jme to a BufferedImage, ImageIcon...
 * @author roah
 */
public class ImageConverter {
    
    public static ImageIcon convertToIcon(Image img, int sizeX, int sizeY) {
        BufferedImage imgConvert = ImageConverter.getImage(img);
        return new ImageIcon(imgConvert.getScaledInstance(sizeX, sizeY, java.awt.Image.SCALE_DEFAULT));
    }
    
    public static BufferedImage getImage(Image img){
        return createFlipped(ImageToAwt.convert(img, false, false, 0));
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
}
