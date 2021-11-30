package ClipKeyboard;



import core.ClipboardListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

public class ClipboardHistory extends JPanel {

	  // for the list of entries copied in clipboard
	  private JList<String> list;
	  private DefaultListModel<String> listModel;
	  private ListSelectionModel listSelectionModel;

	  public ClipboardHistory() {
	    super(new BorderLayout());
	    listModel = new DefaultListModel<String>();
	    list = new JList<String>(listModel);
	    listSelectionModel = list.getSelectionModel();
	    listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	    // we create a JScrollPane to embed our control pane
	    JScrollPane listPane = new JScrollPane(list);
	    JPanel controlPane = new JPanel();

	    // we add a button to let users copy old entries to the clipboard
	    final JButton button = new JButton("Copy");
	    button.addActionListener(new ActionListener() {
	      @Override
	      public void actionPerformed(ActionEvent e) {
	        String value = (String) list.getSelectedValue();
	        int index = list.getSelectedIndex();
	        // remove selected index to avoid duplicate in our list ...
	        listModel.remove(index);
	        // copy to clipboard
	        copyToClipboard(value);
	      }
	    });

	    // we add the button
	    controlPane.add(button);

	    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	    add(splitPane, BorderLayout.CENTER);

	    JPanel topHalf = new JPanel();
	    topHalf.setLayout(new BoxLayout(topHalf, BoxLayout.LINE_AXIS));
	    JPanel listContainer = new JPanel(new GridLayout(1,1));
	    listContainer.setBorder(BorderFactory.createTitledBorder("Entries"));
	    listContainer.add(listPane);

	    topHalf.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
	    topHalf.add(listContainer);
	    topHalf.setMinimumSize(new Dimension(100,50));
	    topHalf.setPreferredSize(new Dimension(100,250));
	    splitPane.add(topHalf);

	    JPanel bottomHalf = new JPanel(new BorderLayout());
	    bottomHalf.add(controlPane, BorderLayout.CENTER);
	    bottomHalf.setPreferredSize(new Dimension(450,30));
	    splitPane.add(bottomHalf);
	  }

	  public void copyToClipboard(String value) {
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    StringSelection data = new StringSelection(value);
	    clipboard.setContents(data, data);
	  }

	  public void createAndShowGUI() {
	    // We create a top JFrame
	    JFrame frame = new JFrame("Clipboard History");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    setOpaque(true);
	    frame.setContentPane(this);
	    frame.pack();
	    frame.setVisible(true); // we display on the screen

	    // we connect the Clipboard Listener to our UI
	    //ClipboardListener listener = new ClipboardListener();
	  }

	  public static void main(String[] args) {
	    SwingUtilities.invokeLater(new Runnable() {
	      @Override
	      public void run() {
	        new ClipboardHistory().createAndShowGUI();
	      }
	    });
	  }

	}