import java.sql.*;
//import java.io.OutputStream;

public class BookingSystem {
    // 1️ BOOK TICKET
   public static String bookTicket(int trainNumber, String passengerName, int passengerAge) {
    try (Connection conn = DatabaseHandler.getConnection()) {
        // Check available seats
        String seatQuery = "SELECT available_seats FROM Train WHERE train_number=?"; 
        PreparedStatement seatStmt = conn.prepareStatement(seatQuery); //preparedStatement is used to avoid sql injection attack
        seatStmt.setInt(1, trainNumber);
        ResultSet seatRs = seatStmt.executeQuery();

        if (seatRs.next()) {
            int availableSeats = seatRs.getInt("available_seats"); //fetch available_seats value
            System.out.println(availableSeats);

            if (availableSeats > 0) {
                // Book the ticket
                String bookQuery = "INSERT INTO Ticket (train_number, passenger_name, age, status) VALUES (?, ?, ?, 'Confirmed')";
                PreparedStatement bookStmt = conn.prepareStatement(bookQuery, Statement.RETURN_GENERATED_KEYS);
                bookStmt.setInt(1, trainNumber);
                bookStmt.setString(2, passengerName);
                bookStmt.setInt(3, passengerAge);
                bookStmt.executeUpdate();

                // Get generated PNR
                ResultSet generatedKeys = bookStmt.getGeneratedKeys();
                int pnr = (generatedKeys.next()) ? generatedKeys.getInt(1) : -1;

                // Reduce available seats
                String updateSeats = "UPDATE Train SET available_seats = available_seats - 1 WHERE train_number=?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSeats);
                updateStmt.setInt(1, trainNumber);
                updateStmt.executeUpdate();

                return "{ \"PNR\": " + pnr + ", \"Train\": " + trainNumber + 
                       ", \"Passenger\": \"" + passengerName + "\", \"Status\": \"Confirmed\" }";
            } else {
                return "{ \"error\": \"No seats available\" }";
            }
        } else {
            return "{ \"error\": \"Train not found\" }";
        }
    } catch (SQLException e) {
        return "{ \"error\": \"Error processing booking\" }";
    }
}


    // 2️ CHECK PNR STATUS
  public static String checkPNR(int pnr) {
    try (Connection conn = DatabaseHandler.getConnection()) {
        String query = "SELECT train_number, passenger_name, status FROM Ticket WHERE pnr=?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, pnr);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return "{ \"PNR\": " + pnr + 
                   ", \"Train\": " + rs.getInt("train_number") + 
                   ", \"Passenger\": \"" + rs.getString("passenger_name") + 
                   "\", \"Status\": \"" + rs.getString("status") + "\" }";
        } else {
            return "{ \"error\": \"PNR Not Found\" }";
        }
    } catch (SQLException e) {
        return "{ \"error\": \"Error fetching PNR\" }";
    }
}
}
