package lite_regex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegexGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Regex Matcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350);

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
        patternText.setBounds(140, 20, 300, 25);
        panel.add(patternText);

        JLabel inputLabel = new JLabel("Test String:");
        inputLabel.setBounds(10, 60, 120, 25);
        panel.add(inputLabel);

        JTextField inputText = new JTextField(30);
        inputText.setBounds(140, 60, 300, 25);
        panel.add(inputText);

        JButton checkButton = new JButton("Check Match");
        checkButton.setBounds(140, 100, 150, 30);
        panel.add(checkButton);

        JButton explainButton = new JButton("Explain Pattern");
        explainButton.setBounds(300, 100, 150, 30);
        explainButton.setEnabled(false); // Initially disabled
        panel.add(explainButton);

        JLabel resultLabel = new JLabel("");
        resultLabel.setBounds(140, 140, 300, 25);
        panel.add(resultLabel);

        checkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String pattern = patternText.getText();
                String input = inputText.getText();

                try {
                    RegexEngine engine = new RegexEngine(pattern);
                    boolean matches = engine.matches(input);
                    resultLabel.setText("Match: " + matches);
                    resultLabel.setForeground(matches ? Color.GREEN : Color.RED);
                    explainButton.setEnabled(true);
                } catch (RegexException ex) {
                    resultLabel.setText("Error in pattern");
                    resultLabel.setForeground(Color.RED);
                    explainButton.setEnabled(false);

                    JTextArea errorArea = new JTextArea(ex.getMessage());
                    errorArea.setEditable(false);
                    errorArea.setLineWrap(true);
                    errorArea.setWrapStyleWord(true);
                    JScrollPane scrollPane = new JScrollPane(errorArea);
                    scrollPane.setPreferredSize(new Dimension(450, 150));
                    JOptionPane.showMessageDialog(panel,
                            scrollPane,
                            "Regex Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        explainButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String pattern = patternText.getText();
                try {
                    String explanation = PatternExplainer.explain(pattern);
                    JTextArea explanationArea = new JTextArea(explanation);
                    explanationArea.setEditable(false);
                    explanationArea.setLineWrap(true);
                    explanationArea.setWrapStyleWord(true);
                    JScrollPane scrollPane = new JScrollPane(explanationArea);
                    scrollPane.setPreferredSize(new Dimension(450, 200));
                    JOptionPane.showMessageDialog(panel,
                            scrollPane,
                            "Pattern Explanation",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel,
                            "Could not generate explanation: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
