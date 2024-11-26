package niko.hospitalmanagementsystem;

//import com.fasterxml.jackson.core.PrettyPrinter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import javax.xml.transform.Result;
import java.sql.*;
//import java.time.DateTimeException;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import static niko.hospitalmanagementsystem.CenterText.centerText;

@SpringBootApplication
public class HospitalManagementSystemApplication {
    private static final String url = "jdbc:postgresql://localhost:5432/Hospital";
    private static final String username = "postgres";
    private static final String password = "admin";

    public static void main(String[] args) {
        SpringApplication.run(HospitalManagementSystemApplication.class, args);

        Scanner scanner = new Scanner(System.in);

        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            while (true) {
                System.out.println("|*********************************|");
                System.out.println("|" + centerText("1-Add patient.", 33) + "|");
                System.out.println("|" + centerText("2-View patients.", 33) + "|");
                System.out.println("|" + centerText("3-View doctors.", 33) + "|");
                System.out.println("|" + centerText("4-Book appointment.", 33) + "|");
                System.out.println("|" + centerText("5-View appointments by date.", 33) + "|");
                System.out.println("|" + centerText("6-Exit.", 33) + "|");
                System.out.println("|*********************************|");
                System.out.print(">> Enter your choice: ");
                int choice = scanner.nextInt();

                switch(choice){
                    case 1: //Add patient
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2: //View patients
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3: //View doctors
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4: //Book appointment
                        bookAppointment(patient, doctor, connection, scanner);
                        System.out.println();
                        break;
                    case 5: //View appointments by date
                        viewAppointmentsByDate(connection, scanner);
                        break;
                    case 6: //Exit
                        return;
                    default:
                        System.out.println("Invalid input. Try again!");
                        break;
                }
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static void viewAppointmentsByDate(Connection connection, Scanner scanner) {
        System.out.println("Enter date: (YYYY-MM-DD)");
        Date appointment_date = Date.valueOf(scanner.next());

        String queryDate = "select * from appointments where appointment_date = ?";
        String queryPatient = "select * from patients where id = ?";
        String queryDoctor = "select * from doctors where id = ?";

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(queryDate);
            preparedStatement.setDate(1, appointment_date);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            System.out.println("Appointments list: ");
            System.out.println("|----------------------|-----------------------|------------|");
            System.out.println("|     Patient name     |      Doctor name      |    Date    |");
            System.out.println("|----------------------|-----------------------|------------|");
            do{
                PreparedStatement psPatient = connection.prepareStatement(queryPatient);
                psPatient.setInt(1, resultSet.getInt("patient_id"));
                ResultSet patientName = psPatient.executeQuery();
                patientName.next();

                PreparedStatement psDoctor = connection.prepareStatement(queryDoctor);
                psDoctor.setInt(1, resultSet.getInt("doctor_id"));
                ResultSet doctorName = psDoctor.executeQuery();
                doctorName.next();

                Date appDate = resultSet.getDate("appointment_date");

                System.out.printf(
                        "|%s|%s|%s|\n",
                        centerText(patientName.getString("name"), 22),
                        centerText(doctorName.getString("name"), 23),
                        centerText(String.valueOf(appDate), 12)
                );
                System.out.println("|----------------------|-----------------------|------------|");
            }while(resultSet.next());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner){
        System.out.println("Enter patient ID: ");
        int patientID = scanner.nextInt();

        System.out.println("Enter doctor ID: ");
        int doctorID = scanner.nextInt();

        System.out.println("Enter appointment date (YYYY-MM-DD): ");
        Date date = Date.valueOf(scanner.next());
        /*String date = scanner.next();
        LocalDate appointmentDate;
        try{
            appointmentDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        }catch(DateTimeException e){
            System.out.println("Invalid date format. Please use YYYY-MM-DD");
        }*/

        if(patient.getPatientById(patientID) && doctor.getDoctorById(doctorID)){
            if(checkDoctorAvailability(doctorID, date, connection)){
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";
                try{
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientID);
                    preparedStatement.setInt(2, doctorID);
                    preparedStatement.setDate(3, date);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected > 0){
                        System.out.println("Appointment booked.");
                    }else{
                        System.out.println("Failed to book appointment.");
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }else{
                System.out.println("Doctor not available on this date.");
            }
        }else{
            System.out.println("Invalid ID for either patient or doctor!");
        }
    }

    private static boolean checkDoctorAvailability(int doctorID, Date date, Connection connection) {
        String query = "select count(*) from appointments where doctor_id = ? and appointment_date = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorID);
            preparedStatement.setDate(2, date);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                int count = resultSet.getInt(1);
                return count == 0;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }



}
