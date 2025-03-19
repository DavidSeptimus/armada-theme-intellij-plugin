import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimpleWindow {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Simple Window");
            frame.setSize(400, 300);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            JLabel label = new JLabel("Press the button to change background color", SwingConstants.CENTER);
            frame.add(label, BorderLayout.NORTH);

            JButton button = new JButton("Change Color");
            frame.add(button, BorderLayout.SOUTH);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.getContentPane().setBackground(new Color((int)(Math.random() * 0x1000000)));
                }
            });

            frame.setVisible(true);
        });
    }
}