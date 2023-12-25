import java.awt.*;
import javax.swing.*;

public class gui extends JFrame {
    public gui() {
        // Set frame properties
        setTitle("My GUI Application");
        setSize(720, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a JPanel to hold components
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));

        Box box = Box.createHorizontalBox();

        panel.setBackground(new Color(36, 36, 36));
        // Add space at the top
        JLabel spaceLabel = new JLabel(" ");
        box.add(spaceLabel);

        // Add search box for user input:
        JTextField searchBox = new JTextField(20);
        box.add(searchBox);

        String userIn = "";

        //Get user input search terms:
        String userQuery = searchBox.getText();

        userIn += userQuery;

        // Add a JComboBox for search modes selection:
        //NOTE: There are 4 modes. Lyrics mode, Songs mode, Albums mode and Joker mode (and one of these is not like the
        //others. Joke mode is a "universal searcher" that looks for the search terms in EVERY category and not just
        //a specified one.
        String[] searchModes = {"Lyrics", "Songs", "Albums", "Joker"};
        JComboBox<String> modeSelector = new JComboBox<>(searchModes);

        box.add(modeSelector);

        //Get user input search mode:
        String selectedMode = (String) modeSelector.getSelectedItem();
        userIn += ";" + selectedMode;

        //lastly add GO button:
        JButton searchButton = new JButton("Go!");
        searchButton.setBackground(new Color(16, 136, 26, 255));
        box.add(searchButton);

        panel.add(box);

        //------------------BOTTOM--SECTION------------------

        // Add a line separator
        addSeparator(panel,100);

        // Add a JTextArea for displaying results
        JTextArea resultsArea = new JTextArea(20, 50);
        resultsArea.setEditable(false); // Make it read-only
        JScrollPane scrollPane = new JScrollPane(resultsArea);

        resultsArea.setBackground(new Color(79, 79, 79));

        panel.add(scrollPane);

        // Add the panel to the frame
        add(panel);
    }

    private void addSeparator(JPanel panel, int height) {
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        Dimension size = new Dimension(separator.getPreferredSize().width, height);
        separator.setPreferredSize(size);
        panel.add(separator);
    }

    public static void displayGUI() {
        // Create and show the GUI
        javax.swing.SwingUtilities.invokeLater(() -> {
            gui gui = new gui();
            gui.setVisible(true);
        });
    }

    public static void printQueryResults(String results){
        resultsArea.setText(results);
    }

}