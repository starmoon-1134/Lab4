import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ImageComponent extends JComponent {

    private BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_3BYTE_BGR);
    
    public boolean setImage(String path) {
        try {
			img = ImageIO.read(new File(path));
		} catch (IOException e) {
			return false;
		}
        setZoom(1);
        return true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension dim = getPreferredSize();
        g.drawImage(img, 0, 0, dim.width, dim.height, this);
    }

    public void setZoom(double zoom) {
        int w = (int) (zoom * img.getWidth());
        int h = (int) (zoom * img.getHeight());
        setPreferredSize(new Dimension(w, h));
        revalidate();
        repaint();
    }
}