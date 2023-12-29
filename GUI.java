import org.apache.lucene.queryparser.classic.ParseException;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;

public class GUI extends JFrame {
    private static SearchCallback searchCallback;
    private static String selectedMode;
    private static String userQuery;
    private static JLabel info;
    private static JTextArea resultsArea;

    public GUI() {
        // Set frame properties
        setTitle("My GUI Application");
        setSize(720, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a JPanel to hold components
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));

        Box box = Box.createHorizontalBox();

        panel.setBackground(new Color(235, 0, 255));
        // Add space at the top
        JLabel spaceLabel = new JLabel(" ");
        box.add(spaceLabel);

        // Add search box for user input:
        JTextField searchBox = new JTextField(20);
        box.add(searchBox);

        // Add a JComboBox for search modes selection:
        String[] searchModes = {"lyrics", "songs", "albums"};
        JComboBox<String> modeSelector = new JComboBox<>(searchModes);
        box.add(modeSelector);

        // lastly add GO button:
        JButton searchButton = new JButton("Go!");
        searchButton.setBackground(new Color(255, 255, 255, 255));
        box.add(searchButton);
        searchButton.addActionListener(e -> {
            // Get user input search mode:
            selectedMode = (String) modeSelector.getSelectedItem();
            userQuery = searchBox.getText();  // Update userQuery with the actual user input
            searchCallback.onSearch(selectedMode);
        });

        panel.add(box);

        // Add a line separator
        addSeparator(panel, 100);

        // Add a JTextArea for displaying results
        resultsArea = new JTextArea(20, 50);
        resultsArea.setEditable(false); // Make it read-only
        JScrollPane scrollPane = new JScrollPane(resultsArea);

        resultsArea.setBackground(new Color(75, 255, 0, 255));

        panel.add(scrollPane);
        // Add the panel to the frame
        add(panel);
    }

    public static void appendResultTextArea(String res) {
        resultsArea.append(res + "\n");
    }

    public static void appendInfoText(String inf) {
        info.setText(inf + "\n");
    }

    private void addSeparator(JPanel panel, int height) {
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        Dimension size = new Dimension(separator.getPreferredSize().width, height);
        separator.setPreferredSize(size);
        panel.add(separator);
    }

    public static void renderGUI(SearchCallback callback) {
        searchCallback = callback;
        // Create and show the GUI
        javax.swing.SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI();
            gui.setVisible(true);
        });
    }

    public static String getUserQuery() {
        return userQuery;
    }

    public static String getSelectedMode() {
        return selectedMode;
    }

    public interface SearchCallback {
        void onSearch(String selectedMode);
    }
}
