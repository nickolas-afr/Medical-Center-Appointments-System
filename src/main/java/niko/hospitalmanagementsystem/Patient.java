package niko.hospitalmanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import static niko.hospitalmanagementsystem.CenterText.centerText;

public class Patient {
    private Connection connection;
    private Scanner scanner;

    public Patient(Connection connection, Scanner scanner){
        this.connection = connection;
        this.scanner = scanner;
    }

    public void addPatient(){
        scanner.nextLine();
        System.out.println("Enter patient name: ");
        String name = scanner.nextLine();
        System.out.println("Enter patient age: ");
        int age = scanner.nextInt();
        System.out.println("Enter patient gender");
        String gender = scanner.next();

        try{

            String query = "INSERT INTO patients(name, age, gender) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, gender);

            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows > 0) {
                System.out.println("Patient added successfully.");
            }
            else{
                System.out.println("Failed to add patient.");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void viewPatients(){
        String query = "select * from patients";

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Patients list: ");
            System.out.println("|------------|----------------------|---------|----------|");
            System.out.println("| Patient ID |         Name         |   Age   |  Gender  |");
            System.out.println("|------------|----------------------|---------|----------|");

            while(resultSet.next()){
                int         id      = resultSet.getInt("id");
                String      name    = resultSet.getString("name");
                int         age     = resultSet.getInt("age");
                String      gender  = resultSet.getString("gender");

                //System.out.printf("|%-12s|%-22s|%-9s|%-10s|\n", id, name, age, gender);

                System.out.printf(
                        "|%s|%s|%s|%s|\n",
                        centerText(String.valueOf(id), 12),
                        centerText(name, 22),
                        centerText(String.valueOf(age), 9),
                        centerText(gender, 10)
                );

                System.out.println("|------------|----------------------|---------|----------|");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public boolean getPatientById(int id){
        String query = "select * from patients where id = ?";

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

}
