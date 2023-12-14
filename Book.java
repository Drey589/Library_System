package OOP5BookBorrower;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import OOP5BookBorrower.database.DatabaseConnection;

public class Book {
    private String title;
    private String author;
    private int stocks;
    private int id;
    
    private String subject;

    public Book(String title, String author, int stocks, String subject) {
        this.title = title;
        this.author = author;
        this.stocks = stocks;
        this.subject = subject;
        this.id = retrieveBookIdFromDatabase(title, author);
    }

    public String getSubject() {
        return subject;
    }

    public String getTitle() {
        return title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public int getNumStocks() {
        return stocks;
    }



    public void incrementStocks() {

            stocks++;

            try (Connection connection = DatabaseConnection.connect()) {
                String sql = "UPDATE books SET stocks = ? WHERE id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, stocks);
                    preparedStatement.setInt(2, id);
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

    }

    public void decrementStocks() {
        if (stocks > 0) {
            stocks--;

            try (Connection connection = DatabaseConnection.connect()) {
                String sql = "UPDATE books SET stocks = ? WHERE id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, stocks);
                    preparedStatement.setInt(2, id);
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No available stocks to decrement.");
        }
    }

    public int getId() {
        return id;
    }

    private int retrieveBookIdFromDatabase(String title, String author) {
        int retrievedId = 0;

        try (Connection connection = DatabaseConnection.connect()) {
            String sql = "SELECT id FROM books WHERE title = ? AND author = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, author);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        retrievedId = resultSet.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return retrievedId;
    }

}
