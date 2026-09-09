package org.example;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CancellationForm extends JFrame {

    private JTextField pnrField;
    private JTextArea detailsArea;
    private JButton searchButton;
    private JButton cancelButton;

    private long currentPnr = 0;

    public CancellationForm() {

        setTitle("Cancel Ticket");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JPanel topPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        topPanel.add(new JLabel("Enter PNR:"));

        pnrField = new JTextField();
        topPanel.add(pnrField);

        searchButton = new JButton("Search");
        topPanel.add(searchButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);

        mainPanel.add(
                new JScrollPane(detailsArea),
                BorderLayout.CENTER
        );

        cancelButton = new JButton("Cancel Ticket");
        cancelButton.setEnabled(false);

        mainPanel.add(cancelButton, BorderLayout.SOUTH);

        add(mainPanel);

        searchButton.addActionListener(e -> searchTicket());

        cancelButton.addActionListener(e -> cancelTicket());

        setVisible(true);
    }

    private void searchTicket() {

        String pnrText = pnrField.getText().trim();

        if (pnrText.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter PNR number!"
            );
            return;
        }

        try {

            currentPnr = Long.parseLong(pnrText);

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "PNR number must be numeric!"
            );
            return;
        }

        String sql = """
                SELECT r.pnr,
                       r.passenger_name,
                       r.train_number,
                       t.train_name,
                       r.class_type,
                       r.journey_date,
                       r.source_station,
                       r.destination_station
                FROM reservations r
                JOIN trains t
                ON r.train_number = t.train_number
                WHERE r.pnr = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(1, currentPnr);

            ResultSet result = statement.executeQuery();

            if (result.next()) {

                detailsArea.setText(
                        "PNR Number: " + result.getLong("pnr")
                                + "\nPassenger Name: "
                                + result.getString("passenger_name")
                                + "\nTrain Number: "
                                + result.getInt("train_number")
                                + "\nTrain Name: "
                                + result.getString("train_name")
                                + "\nClass Type: "
                                + result.getString("class_type")
                                + "\nJourney Date: "
                                + result.getDate("journey_date")
                                + "\nSource: "
                                + result.getString("source_station")
                                + "\nDestination: "
                                + result.getString("destination_station")
                );

                cancelButton.setEnabled(true);

            } else {

                detailsArea.setText("");
                cancelButton.setEnabled(false);

                JOptionPane.showMessageDialog(
                        this,
                        "No ticket found for this PNR!"
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error while searching ticket!\n"
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    private void cancelTicket() {

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel this ticket?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM reservations WHERE pnr = ?";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(1, currentPnr);

            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Ticket cancelled successfully!"
                );

                pnrField.setText("");
                detailsArea.setText("");
                cancelButton.setEnabled(false);
                currentPnr = 0;

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Ticket cancellation failed!"
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error while cancelling ticket!\n"
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}