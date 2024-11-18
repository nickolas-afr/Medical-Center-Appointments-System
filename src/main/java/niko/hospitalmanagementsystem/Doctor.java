package niko.hospitalmanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import static niko.hospitalmanagementsystem.CenterText.centerText;

public class Doctor {
    private Connection connection;

    public Doctor(Connection connection){
        this.connection = connection;
    }

    public void viewDoctors(){
        String query = "select * from doctors";

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Doctors list: ");
            System.out.println("|-----------|----------------------|------------------------|");
            System.out.println("| Doctor ID |         Name         |     Specialization     |");
            System.out.println("|-----------|----------------------|------------------------|");

            while(resultSet.next()){
                int         id              = resultSet.getInt("id");
                String      name            = resultSet.getString("name");
                String      specialization  = resultSet.getString("specialization");

                System.out.printf(
                        "|%s|%s|%s|\n",
                        centerText(String.valueOf(id), 11),
                        centerText(name, 22),
                        centerText(specialization, 24)
                );
                System.out.println("|-----------|----------------------|------------------------|");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public boolean getDoctorById(int id){
        String query = "select * from doctors where id = ?";

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
