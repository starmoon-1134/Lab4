
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class ImageComponent extends JComponent {

  private transient BufferedImage img = new BufferedImage(
      400, 400, BufferedImage.TYPE_3BYTE_BGR);

  /**
   * javadoc×¢ÊÍÄÚÈÝ
   * 
   * @since 1.0
   * @version 1.1
   * @author xxx
   */
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

  /**
   * javadoc×¢ÊÍÄÚÈÝ
   * 
   * @since 1.0
   * @version 1.1
   * @author xxx
   */
  public void setZoom(double zoom) {
    int w = (int) (zoom * img.getWidth());
    int h = (int) (zoom * img.getHeight());
    setPreferredSize(new Dimension(w, h));
    revalidate();
    repaint();
  }
}