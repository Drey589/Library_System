package OOP5BookBorrower;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import OOP5BookBorrower.database.DatabaseConnection;

public class Borrower {
    private int id;
    private String name;
    private String lastName;
    private int age;
    private String address;
    private String phoneNumber;
    private ArrayList<Book> borrowedBooks;

    public Borrower(String name, String lastName, int age, String address, String phoneNumber) {
        this.name = name;
        this.lastName = lastName;
        this.id = retrieveBorrowerIdFromDatabase(name, lastName);
        this.age = age;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.borrowedBooks = new ArrayList<>();
        loadBorrowedBooksFromDatabase();
    }

/*
asdwer
String search = "wer"
 * SELECT * FRO< db LIKE "%%search%%"
 * 
 */

 public Book getBorrowedBookByTitle(String title){
    for(Book book : borrowedBooks){
        if(book.getTitle().equals(title)){
            return book;
        }
    }
    return null;
 }
    public void loadBorrowedBooksFromDatabase() {
        try (Connection connection = DatabaseConnection.connect()) {
            String sql = "SELECT book_id FROM borrowed_books WHERE borrower_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int bookId = resultSet.getInt("book_id");
                        Book borrowedBook = retrieveBookFromDatabase(bookId);
                        
                        if (borrowedBook != null) {
                            borrowedBooks.add(borrowedBook);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
    }
    public int getAge(){
        return age;
    }
    public String getAddress(){
        return address;
    }
    public String getPhoneNumber(){
        return phoneNumber;
    }

    private Book retrieveBookFromDatabase(int bookId) {
        Book book = null;

        try (Connection connection = DatabaseConnection.connect()) {
            String sql = "SELECT title, author, stocks, subject FROM books WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, bookId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String title = resultSet.getString("title");
                        String author = resultSet.getString("author");
                        int stocks = resultSet.getInt("stocks");
                        String subject = resultSet.getString("subject");

                        book = new Book(title, author, stocks,subject);
                        book.setId(bookId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }

        return book;
    }
    private int retrieveBorrowerIdFromDatabase(String name, String lastName) {
    int retrievedId = 0;

    try (Connection connection = DatabaseConnection.connect()) {
        String sql = "SELECT id FROM borrowers WHERE name = ? AND lastName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);

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

    public String getName() {
        return name;
    }


    public String getLastName(){
        return lastName;
    }

    public ArrayList<Book> getBorrowedBooks() {
        return borrowedBooks;
    }


    public void borrowBook(Book book) {
        if (book.getNumStocks() != 0) {
            borrowedBooks.add(book);
            book.decrementStocks();
            System.out.println(name + " borrowed the book: " + book.getTitle());

            try (Connection connection = DatabaseConnection.connect()) {
                String sql = "INSERT INTO borrowed_books (borrower_id, book_id) VALUES (?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    
                    preparedStatement.setInt(1, id); 
                    preparedStatement.setInt(2, book.getId()); 
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No Available Stocks!");
        }
    }

    public void returnBook(Book book,int index) {
        borrowedBooks.remove(index);
        System.out.println(name + " returned the book: " + book.getTitle());
        book.incrementStocks();

        try (Connection connection = DatabaseConnection.connect()) {
            String sql = "DELETE FROM borrowed_books WHERE borrower_id = ? AND book_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                preparedStatement.setInt(2, book.getId()); 
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
/*TO DO
 * 
 * 1. Connect this HashMap to database
 * 2. Fix current codes
 * 3. Should return near searches
 * 4. Add Subjects
 */