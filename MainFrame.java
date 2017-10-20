
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MainFrame {
  private final int selectionCount = 5;
  private int selection = 0;
  private JFrame frame = new JFrame("Text2Graph");
  private JRadioButton[] radioButton = new JRadioButton[selectionCount];
  private JTextField textFieldLeft = new JTextField(10);
  private JTextField textFieldRight = new JTextField(10);
  private JTextArea textAreaIn = new JTextArea(
      "Enter text here");
  private JTextArea textAreaOut = new JTextArea();
  private JScrollPane scrollPaneIn = new JScrollPane(
      textAreaIn);
  private final JScrollPane scrollPaneOut = new JScrollPane(
      textAreaOut);
  private JPanel groupPanel = new JPanel();
  private JPanel imagePanel = new JPanel();
  private ImageViewer imageViewer = new ImageViewer();
  private ImageFrame imageFrame = null;
  private Text2Graph t2g = new Text2Graph();

  /**
   * javadoc×¢ÊÍÄÚÈÝ
   * 
   * @since 1.0
   * @version 1.1
   * @author xxx
   */
  public void init() throws IOException {
    try {
      t2g.init();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    String[] selectionText = { "Bridge Words", "New Text",
        "Shortest Path", "Random Walk", "Show Graph" };
    assert (selectionText.length == selectionCount);

    JPanel radioPanel = new JPanel();

    ButtonGroup radioGroup = new ButtonGroup();
    for (int i = 0; i < selectionCount; i++) {
      radioButton[i] = new JRadioButton(selectionText[i]);
      radioButton[i]
          .addItemListener(new RadioItemListener());
      radioGroup.add(radioButton[i]);
      radioPanel.add(radioButton[i]);
    }
    radioButton[0].setSelected(true);

    frame.setLayout(new BorderLayout(1, 3));
    frame.add(radioPanel, BorderLayout.NORTH);

    JButton confirmButton = new JButton("OK");
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(textFieldLeft);
    buttonPanel.add(textFieldRight);
    buttonPanel.add(confirmButton);
    confirmButton
        .addActionListener(new ButtonClickedListener());

    textAreaIn.setLineWrap(true);
    textAreaIn.setWrapStyleWord(true);
    textAreaIn.setFont(new Font("Serif", Font.PLAIN, 20));
    textAreaOut.setEditable(false);
    textAreaOut.setLineWrap(true);
    textAreaOut.setWrapStyleWord(true);
    textAreaOut.setFont(new Font("Serif", Font.PLAIN, 20));

    scrollPaneIn.setPreferredSize(new Dimension(400, 400));
    scrollPaneIn.setVerticalScrollBarPolicy(
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPaneOut.setVerticalScrollBarPolicy(
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    JPanel textPanel = new JPanel();
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

  public class ButtonClickedListener
      implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      String textOut = "";
      switch (selection) {
        case 0:
          textOut = t2g.queryBridgeWords(
              textFieldLeft.getText().toLowerCase(),
              textFieldRight.getText().toLowerCase());
          break;
        case 1:
          textOut = t2g.generateNewText(
              textAreaIn.getText().toLowerCase());
          break;
        case 2:
          if (textFieldLeft.getText().equals("")) {
            textOut = "Source word should not be empty!";
            break;
          }

          textOut = t2g.calcShortestPath(
              textFieldLeft.getText().toLowerCase(),
              textFieldRight.getText().toLowerCase());
          t2g.outputGraph();
          if (imageFrame == null) {
            imageFrame = new ImageFrame("Shortest Path");
          }
          imageFrame.setImage(Configuration.JpgImagePath);
          imageFrame.setVisible(true);
          break;
        case 3:
          textOut = t2g.randomWalk();
          break;
        default:
          break;
      }

      textAreaOut.setText(textOut);
    }
  }

  public class RadioItemListener implements ItemListener {
    /**
     * javadoc×¢ÊÍÄÚÈÝ
     * 
     * @since 1.0
     * @version 1.1
     * @author xxx
     */
    public void itemStateChanged(ItemEvent e) {
      JRadioButton item = (JRadioButton) e.getSource();
      textAreaOut.setText("");

      if (item != radioButton[4]
          && imagePanel.isShowing()) {
        frame.remove(imagePanel);
        frame.add(groupPanel);
        frame.repaint();
      }

      if (item == radioButton[1]
          || item == radioButton[3]) {
        textFieldLeft.setVisible(false);
        textFieldRight.setVisible(false);
      } else {
        textFieldLeft.setVisible(true);
        textFieldRight.setVisible(true);
      }

      if (item == radioButton[0]) {
        selection = 0;
        scrollPaneIn.setVisible(false);
      } else if (item == radioButton[1]) {
        selection = 1;
        scrollPaneIn.setVisible(true);
      } else if (item == radioButton[2]) {
        selection = 2;
        scrollPaneIn.setVisible(false);
      } else if (item == radioButton[3]) {
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
      imageViewer.setImage(Configuration.JpgImagePath);

      getContentPane().add(imageViewer);
      setPreferredSize(new Dimension(600, 400));
      pack();
    }

    public void setImage(String path) {
      imageViewer.setImage(path);
    }
  }
}
