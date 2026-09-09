package org.example;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {

    public MainMenu() {

        setTitle("Online Reservation System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel title = new JLabel(
                "Online Reservation System",
                SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 20));

        JButton bookButton = new JButton("Book Ticket");
        JButton cancelButton = new JButton("Cancel Ticket");
        JButton exitButton = new JButton("Exit");

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));

        panel.setBorder(
                BorderFactory.createEmptyBorder(30, 50, 30, 50)
        );

        panel.add(bookButton);
        panel.add(cancelButton);
        panel.add(exitButton);

        add(title, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        // Book Ticket button action
        bookButton.addActionListener(e -> {
            new ReservationForm();
        });

        // Cancel Ticket button action
        cancelButton.addActionListener(e -> {
            new CancellationForm();
        });

        exitButton.addActionListener(e -> System.exit(0));

        setVisible(true);
    }
}