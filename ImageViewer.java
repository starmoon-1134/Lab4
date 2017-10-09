import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class ImageViewer extends JComponent {
	final JSlider slider;
	final ImageComponent image;
	final JScrollPane scrollPane;

	public ImageViewer() {
		slider = new JSlider(0, 1000, 500);
		image = new ImageComponent();
		scrollPane = new JScrollPane(image);

		slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                image.setZoom(2. * slider.getValue() / slider.getMaximum());
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
