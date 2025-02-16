import java.sql.*;

public class DatabaseHandler {
    private static final String URL = "jdbc:mysql://localhost:3306/TrainBookingDB";
    private static final String USER = "root";  
    private static final String PASSWORD = "Mytech@2025";  

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD); //invoke mysql jdbc driver
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println(" Database connection successful!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
