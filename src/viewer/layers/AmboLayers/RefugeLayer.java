package viewer.layers.AmboLayers;

import AUR.util.knd.AURAreaGraph;
import AUR.util.knd.AURWorldGraph;
import rescuecore2.standard.entities.Refuge;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import sun.misc.BASE64Decoder;
import viewer.K_ScreenTransform;
import viewer.K_ViewerLayer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

/**
 *
 * @author armanaxh - 2018
 */

public class RefugeLayer extends K_ViewerLayer {


    private static BufferedImage decodeToImage(String imageString) {
        BufferedImage image = null;
        byte[] imageByte;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public static BufferedImage refugeImage = decodeToImage("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");

    @Override
    public void paint(Graphics2D g2, K_ScreenTransform kst, AURWorldGraph wsg, AURAreaGraph selected_ag) {
        g2.setStroke(new BasicStroke(1));
        g2.setFont(new Font("Arial", 0, 10));
        int r = 10;
        for(StandardEntity entity: wsg.wi.getEntitiesOfType(StandardEntityURN.REFUGE)){

            Refuge refuge = (Refuge)entity;
            g2.drawImage(refugeImage, kst.xToScreen(refuge.getX()) - r , kst.yToScreen(refuge.getY()) - r , 2 * r , 2 * r , null);

        }

    }

}