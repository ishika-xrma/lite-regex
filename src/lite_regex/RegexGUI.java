package lite_regex;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegexGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Regex Matcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel patternLabel = new JLabel("Regex Pattern:");
        patternLabel.setBounds(10, 20, 120, 25);
        panel.add(patternLabel);

        JTextField patternText = new JTextField(30);
        patternText.setBounds(140, 20, 200, 25);
        panel.add(patternText);

        JLabel inputLabel = new JLabel("Test String:");
        inputLabel.setBounds(10, 60, 120, 25);
        panel.add(inputLabel);

        JTextField inputText = new JTextField(30);
        inputText.setBounds(140, 60, 200, 25);
        panel.add(inputText);

        JButton checkButton = new JButton("Check Match");
        checkButton.setBounds(140, 100, 150, 30);
        panel.add(checkButton);

        JLabel resultLabel = new JLabel("");
        resultLabel.setBounds(140, 140, 250, 25);
        panel.add(resultLabel);

        checkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String pattern = patternText.getText();
                String input = inputText.getText();
                RegexEngine engine = new RegexEngine(pattern);
                
                boolean matches = engine.matches(input);
                resultLabel.setText("Match: " + matches);
            }
        });
    }
}
