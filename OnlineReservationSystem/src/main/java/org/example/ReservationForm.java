package org.example;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class ReservationForm extends JFrame {

    private JTextField passengerNameField;
    private JComboBox<String> trainComboBox;
    private JTextField journeyDateField;
    private JTextField sourceField;
    private JTextField destinationField;

    private JComboBox<String> classTypeComboBox;

    public ReservationForm() {

        setTitle("Book Ticket");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));

        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        // Passenger Name
        panel.add(new JLabel("Passenger Name:"));

        passengerNameField = new JTextField();
        panel.add(passengerNameField);

        // Select Train
        panel.add(new JLabel("Select Train:"));

        trainComboBox = new JComboBox<>();
        loadTrains();

        panel.add(trainComboBox);

        // Class Type
        panel.add(new JLabel("Class Type:"));

        String[] classes = {
                "AC First Class",
                "AC 2 Tier",
                "AC 3 Tier",
                "Sleeper",
                "Second Sitting"
        };

        classTypeComboBox = new JComboBox<>(classes);
        panel.add(classTypeComboBox);

        // Journey Date
        panel.add(new JLabel("Journey Date:"));

        journeyDateField = new JTextField();
        journeyDateField.setToolTipText("Format: yyyy-MM-dd");
        panel.add(journeyDateField);

        // Source
        panel.add(new JLabel("Source Station:"));

        sourceField = new JTextField();
        panel.add(sourceField);

        // Destination
        panel.add(new JLabel("Destination Station:"));

        destinationField = new JTextField();
        panel.add(destinationField);

        // Book Button
        JButton bookButton = new JButton("Book Ticket");

        panel.add(new JLabel());
        panel.add(bookButton);

        add(panel);

        bookButton.addActionListener(e -> bookTicket());

        setVisible(true);
    }

    private void loadTrains() {

        String sql = "SELECT train_number, train_name FROM trains";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {

            while (result.next()) {

                int trainNumber = result.getInt("train_number");
                String trainName = result.getString("train_name");

                trainComboBox.addItem(
                        trainNumber + " - " + trainName
                );
            }

            if (trainComboBox.getItemCount() == 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "No trains available in database!"
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load trains!\n" + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    private void bookTicket() {

        String passengerName =
                passengerNameField.getText().trim();

        String journeyDateText =
                journeyDateField.getText().trim();

        String source =
                sourceField.getText().trim();

        String destination =
                destinationField.getText().trim();

        // Check train selection
        if (trainComboBox.getSelectedItem() == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a train!"
            );

            return;
        }

        String selectedTrain =
                trainComboBox.getSelectedItem().toString();

        String[] trainDetails =
                selectedTrain.split(" - ", 2);

        int trainNumber =
                Integer.parseInt(trainDetails[0]);

        String trainName =
                trainDetails[1];

        String classType =
                classTypeComboBox.getSelectedItem().toString();

        // Basic validation
        if (
                passengerName.isEmpty()
                        || journeyDateText.isEmpty()
                        || source.isEmpty()
                        || destination.isEmpty()
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields correctly!"
            );

            return;
        }

        Date journeyDate;

        try {

            LocalDate date =
                    LocalDate.parse(journeyDateText);

            journeyDate =
                    Date.valueOf(date);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid date! Use yyyy-MM-dd format."
            );

            return;
        }

        long pnr = System.currentTimeMillis();

        String sql = """
                INSERT INTO reservations
                (
                    pnr,
                    passenger_name,
                    train_number,
                    class_type,
                    journey_date,
                    source_station,
                    destination_station
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(1, pnr);
            statement.setString(2, passengerName);
            statement.setInt(3, trainNumber);
            statement.setString(4, classType);
            statement.setDate(5, journeyDate);
            statement.setString(6, source);
            statement.setString(7, destination);

            int rowsInserted =
                    statement.executeUpdate();

            if (rowsInserted > 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Ticket Booked Successfully!\n"
                                + "PNR Number: " + pnr
                                + "\nPassenger: " + passengerName
                                + "\nTrain: " + trainName
                );

                clearFields();
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ticket booking failed!\n"
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    private void clearFields() {

        passengerNameField.setText("");
        journeyDateField.setText("");
        sourceField.setText("");
        destinationField.setText("");

        if (trainComboBox.getItemCount() > 0) {
            trainComboBox.setSelectedIndex(0);
        }

        classTypeComboBox.setSelectedIndex(0);
    }
}