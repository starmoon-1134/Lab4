import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.*;

public class MainFrame {
    private final int SELECTION_COUNT = 5;
    private int selection = 0;
    private JFrame frame = new JFrame("Text2Graph");
	private JRadioButton[] radioButton = new JRadioButton[SELECTION_COUNT];
    private JTextField textFieldLeft = new JTextField(10);
    private JTextField textFieldRight = new JTextField(10);
    private JTextArea textAreaIn = new JTextArea("Enter text here");
    private JTextArea textAreaOut = new JTextArea();
	private JScrollPane scrollPaneIn = new JScrollPane(textAreaIn);
	private JScrollPane scrollPaneOut = new JScrollPane(textAreaOut);
    private JPanel groupPanel = new JPanel();
    private JPanel imagePanel = new JPanel();
    private ImageViewer imageViewer = new ImageViewer();
    private ImageFrame imageFrame = null;
    private Text2Graph t2g = new Text2Graph();

	public void init() throws IOException {
		try {
			t2g.init();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}

        String[] selectionText = {"Bridge Words", "New Text", "Shortest Path", "Random Walk", "Show Graph"};
        assert(selectionText.length == SELECTION_COUNT);

        JPanel radioPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        JPanel textPanel = new JPanel();

		ButtonGroup radioGroup = new ButtonGroup();
		for(int i = 0; i < SELECTION_COUNT; i++) {
			radioButton[i] = new JRadioButton(selectionText[i]);
			radioButton[i].addItemListener(new radioItemListener());
			radioGroup.add(radioButton[i]);
			radioPanel.add(radioButton[i]);
		}
		radioButton[0].setSelected(true);

		frame.setLayout(new BorderLayout(1, 3));
		frame.add(radioPanel, BorderLayout.NORTH);

		JButton confirmButton = new JButton("OK");
		buttonPanel.add(textFieldLeft);
		buttonPanel.add(textFieldRight);
		buttonPanel.add(confirmButton);
		confirmButton.addActionListener(new buttonClickedListener());

		textAreaIn.setLineWrap(true);
		textAreaIn.setWrapStyleWord(true);
		textAreaIn.setFont(new Font("Serif", Font.PLAIN, 20));
		textAreaOut.setEditable(false);
		textAreaOut.setLineWrap(true);
		textAreaOut.setWrapStyleWord(true);
		textAreaOut.setFont(new Font("Serif", Font.PLAIN, 20));

		scrollPaneIn.setPreferredSize(new Dimension(400, 400));
		scrollPaneIn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneOut.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		textPanel.setLayout(new BorderLayout(1, 3));
		textPanel.add(scrollPaneIn, BorderLayout.WEST);
		textPanel.add(scrollPaneOut);

		groupPanel.setLayout(new BorderLayout(1, 3));
		groupPanel.add(buttonPanel, BorderLayout.NORTH);
		groupPanel.add(textPanel);

		t2g.outputGraph();
		imageViewer.setImage(Configuration.JpgImagePath);
        imagePanel.setLayout(new BorderLayout());
        imagePanel.add(imageViewer);
		imagePanel.setPreferredSize(new Dimension(800, 600));

		frame.add(groupPanel);

		frame.setSize(800, 400);
		frame.setPreferredSize(new Dimension(800, 400));
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public class buttonClickedListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String textOut = "";
			switch(selection) {
			case 0:
				textOut = t2g.queryBridgeWords(textFieldLeft.getText(), textFieldRight.getText());
				break;
			case 1:
				textOut = t2g.generateNewText(textAreaIn.getText());
				break;
			case 2:
				if(textFieldLeft.getText().equals("")) {
					textOut = "Source word should not be empty!";
					break;
				}

				textOut = t2g.calcShortestPath(textFieldLeft.getText(), textFieldRight.getText());
				t2g.outputGraph();
				if(imageFrame == null) imageFrame = new ImageFrame("Shortest Path");
				imageFrame.setImage(Configuration.JpgImagePath);
				imageFrame.setVisible(true);
				break;
			case 3:
				textOut = t2g.randomWalk();
				break;
			default:
				;
			}

			textAreaOut.setText(textOut);
		}
	}

	public class radioItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			JRadioButton item = (JRadioButton) e.getSource();
			textAreaOut.setText("");

			if(item != radioButton[4] && imagePanel.isShowing()){
				frame.remove(imagePanel);
				frame.add(groupPanel);
				frame.repaint();
			}

			if(item == radioButton[1] || item == radioButton[3]) {
				textFieldLeft.setVisible(false);
				textFieldRight.setVisible(false);
			} else {
				textFieldLeft.setVisible(true);
				textFieldRight.setVisible(true);
			}

			if(item == radioButton[0]) {
				selection = 0;
				scrollPaneIn.setVisible(false);
			} else if(item == radioButton[1]) {
				selection = 1;
				scrollPaneIn.setVisible(true);
			} else if(item == radioButton[2]) {
				selection = 2;
				scrollPaneIn.setVisible(false);
			} else if(item == radioButton[3]) {
				selection = 3;
				scrollPaneIn.setVisible(false);
			} else {
				selection = 4;
				frame.remove(groupPanel);
				frame.add(imagePanel);
				frame.repaint();
			}

			frame.pack();
		}
	}


	class ImageFrame extends JFrame {
		private ImageViewer imageViewer = new ImageViewer();
		ImageFrame(String title) {
			super(title);
			setImage(Configuration.JpgImagePath);
			getContentPane().add(imageViewer);
			setPreferredSize(new Dimension(600, 400));
			pack();
		}

		public void setImage(String path) {
			imageViewer.setImage(path);
		}
	}
}
