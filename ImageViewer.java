
import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ImageViewer extends JComponent {
  final JSlider slider;
  final ImageComponent image;
  final JScrollPane scrollPane;

  /**
   * javadoc×¢ÊÍÄÚÈÝ
   * 
   * @since 1.0
   * @version 1.1
   * @author xxx
   */
  public ImageViewer() {
    slider = new JSlider(0, 1000, 500);
    image = new ImageComponent();
    scrollPane = new JScrollPane(image);

    slider.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        image.setZoom(2. * slider.getValue() / slider
            .getMaximum());
      }
    });

    this.setLayout(new BorderLayout());
    this.add(slider, BorderLayout.NORTH);
    this.add(scrollPane);
  }

  public boolean setImage(String path) {
    return image.setImage(path);
  }
}
