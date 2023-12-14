package database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection connect() {
        Connection connection = null;
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");


            String url = "jdbc:mysql://localhost:3306/library_system";
            String username = "root";
            String password = "12345";    
            connection = DriverManager.getConnection(url, username, password);

            // System.out.println("Connected to the database!");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                // System.out.println("Connection closed.
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
