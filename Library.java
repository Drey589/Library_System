package OOP5BookBorrower;

import OOP5BookBorrower.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Library {
    private HashMap<String, Book> books;
    private HashMap<String, Borrower> borrowers;

    public Library() {
        this.books = new HashMap<>();
        this.borrowers = new HashMap<>();
        initializeLibraryFromDatabase();
    }

    public void initializeLibraryFromDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/library_system";
            String username = "root";
            String password = "12345";
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                loadBooksFromDatabase(connection);
                loadBorrowersFromDatabase(connection);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBooksFromDatabase(Connection connection) throws SQLException {
        String sql = "SELECT * FROM books";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int stocks = resultSet.getInt("stocks");
                String subject = resultSet.getString("subject");

                Book book = new Book(title, author, stocks, subject);
                books.put(title, book);
            }
        }
    }

    private void loadBorrowersFromDatabase(Connection connection) throws SQLException {
        String sql = "SELECT * FROM borrowers";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String lastName = resultSet.getString("lastName");
                int age = resultSet.getInt("age");
                String address = resultSet.getString("address");
                String phoneNumber = resultSet.getString("phoneNumber");

                Borrower borrower = new Borrower(name, lastName, age, address, phoneNumber);
                borrowers.put(name.toLowerCase(), borrower);
            }
        }
    }

    public HashMap<String, Book> getBooks() {
        return books;
    }

    public ArrayList<Borrower> getBorrowerList() {
        return new ArrayList<>(borrowers.values());
    }

    public void addBooksToDatabase(String title, String author, int stocks, String subject) {
        if (title == null || author == null || title.isEmpty() || author.isEmpty() || stocks < 0 || subject.isEmpty()) {
            System.out.println("Invalid input. Please provide valid title, author, and stocks.");
            return;
        }

        try (Connection connection = DatabaseConnection.connect()) {
            // Check if the book already exists in the database
            if (isBookExistsInDatabase(connection, title)) {
                System.out.println("Book with title '" + title + "' already exists.");
                return;
            }

            // Insert the book into the database
            String sql = "INSERT INTO books (title, author, stocks, subject) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, author);
                preparedStatement.setInt(3, stocks);
                preparedStatement.setString(4, subject.toLowerCase());
                preparedStatement.executeUpdate();

                System.out.println("Added book to the database: " + title + " by " + author);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isBookExistsInDatabase(Connection connection, String title) throws SQLException {
        String sql = "SELECT COUNT(*) FROM books WHERE title = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, title);
            try (var resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        }
    }

    public void addBorrowerToDatabase(String name, String lastName, int age, String address, String phoneNumber) {

        try (Connection connection = DatabaseConnection.connect()) {
            // Check if the borrower already exists in the database
            if (isBorrowerExistsInDatabase(connection, name, lastName)) {
                System.out
                        .println("Borrower with name '" + name + "' and last name '" + lastName + "' already exists.");
                return;
            }

            // Insert the borrower into the database
            String sql = "INSERT INTO borrowers (name, lastName, age, address, phoneNumber) VALUES (?, ?,?,?,?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, lastName);
                preparedStatement.setInt(3, age);
                preparedStatement.setString(4, address);
                preparedStatement.setString(5, phoneNumber);
                preparedStatement.executeUpdate();

                System.out.println("Added borrower to the database: " + name + " " + lastName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isBorrowerExistsInDatabase(Connection connection, String name, String lastName)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM borrowers WHERE name = ? AND lastName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);
            try (var resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        }
    }

    public void removeBookFromDatabase(String title) {
        try (Connection connection = DatabaseConnection.connect()) {
            // Check if the book exists in the database
            if (!isBookExistsInDatabase(connection, title)) {
                System.out.println("Book with title '" + title + "' not found in the database.");
                return;
            }

            // Remove the book from the database
            String sql = "DELETE FROM books WHERE title = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, title);
                preparedStatement.executeUpdate();

                System.out.println("Removed book from the database: " + title);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printBorrowers(ArrayList<Borrower> borrowers) {
        int number = 1;
        System.out.println("");
        for (Borrower borrower : borrowers) {
            System.out.println(number + ". " + borrower.getName() + " " + borrower.getLastName());
            number++;
        }
        System.out.println("");
    }

    public void printAllBooks(ArrayList<Book> books) {
        System.out.println("\nLibrary Books:");
        for (Book book : books) {
            System.out.println("\n-------------------------------\n");
            System.out.println("Title: "+ book.getTitle());
            System.out.println("Author: "+ book.getAuthor());
            System.out.println("Subject: "+ book.getSubject());
            System.out.println("Available: "+ book.getNumStocks());
        }
        System.out.println("");
    }

    public ArrayList<Book> searchBySubject(String subject) {
        ArrayList<Book> subjectBooks = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getSubject().equalsIgnoreCase(subject)) {
                subjectBooks.add(book);
            }
        }
        return subjectBooks;
    }

    public Book searchBookByTitle(String title) {
        for (Book book : books.values()) {
            if (book.getTitle().equals(title)) {
                return book;
            }
        }
        return null;
    }

    public Borrower searchByName(String name) {
        if (borrowers.containsKey(name.toLowerCase())) {
            return borrowers.get(name.toLowerCase());
        } else {
            return null;
        }
    }

    public ArrayList<Borrower> searchBorrowersWhoBorrowed(String title) {
        ArrayList<Borrower> borrowersList = new ArrayList<>();
        for (Borrower borrower : borrowers.values()) {
            if (borrower.getBorrowedBooks().containsKey(title)) {
                borrowersList.add(borrower);
            }
        }
        return borrowersList;
    }

    public ArrayList<Borrower> searchBorrowerBylastName(String lastname) {
        ArrayList<Borrower> lastNames = new ArrayList<>();
        for (Borrower borrower : borrowers.values()) {
            if (borrower.getLastName().equalsIgnoreCase(lastname)) {
                lastNames.add(borrower);
            }
        }
        return lastNames;
    }
    public ArrayList<Borrower> borrowersWithBorrowedBooks(){
        ArrayList<Borrower> borrowersWithBooks = new ArrayList<>();
        for(Borrower borrower : borrowers.values()){
            if(!borrower.getBorrowedBooks().isEmpty()){
                borrowersWithBooks.add(borrower);
            }
        }
        return borrowersWithBooks;
    }

}
/* searchByName
 * 1.add last name to borrowers
 * 2. search by last name
 * 4. Check every functions we currently have
 * 5. add subject to books
 * 6. search by subjects
 * 7. print by subjects
 * 8. Typo in search
 * 9. database
 */

// package OOP5BookBorrower;

// import java.util.ArrayList;
// import java.util.HashMap;

// public class Library {
// private int idNumber = 1;
// private HashMap<String, Book> books;
// private HashMap<String, Borrower> borrowers;

// public Library() {
// this.books = new HashMap<>();
// this.borrowers = new HashMap<>();
// }

// public void addBooks(String title, String author, int stocks) {
// Book book = new Book(title, author, stocks);
// books.put(book.getAuthor(), book);
// }

// public void removeBook(String title) {
// if (books.containsKey(title)) {
// System.out.println("Removed book" + books.get(title));
// books.remove(title);
// } else {
// System.out.println("Book not found!");
// }
// }

// public void addBorrower(String name) {
// Borrower newBorrower = new Borrower(name, idNumber);
// idNumber++;
// borrowers.put(newBorrower.getName(), newBorrower);
// }

// // public List<Borrower> bookBorrowedBy(String bookTitle){
// // if()
// // }

// // search by title
// public Book searchBookByTitle(String title) {

// for (Book book : books.values()) {
// if (book.getTitle().equals(title)) {
// return book;
// }
// }
// return null;
// }

// // search borrowers by books borrowed
// public ArrayList<Borrower> searchBorrowersWhoBorrowed(String title) {
// ArrayList<Borrower> borrowers = new ArrayList<>();
// for (Borrower borrower : borrowers) {
// if (borrower.getBorrowedBooks().containsKey(title)) {
// borrowers.add(borrower);
// }
// }
// return borrowers;
// }

// public void printBorrowersOf(ArrayList<Borrower> borrowers, String title) {
// System.out.println("Borrowers of " + title);
// for (Borrower borrower : borrowers) {
// System.out.println("Name: " + borrower.getName());
// }

// }

// // print Borrowers
// public void printAllBorrowers(ArrayList<Borrower> borrowers) {
// for (Borrower borrower : borrowers) {
// System.out.println("Name: " + borrower.getName() + " borrowed books: \n");
// for (Book book : borrower.getBorrowedBooks().values()) {
// System.out.println("Title: " + book.getTitle());
// }
// }

// }
// public void borrowBook(String borrower, String Book) {
// if (borrowers.containsKey(borrower)) {
// Borrower person = borrowers.get(borrower);
// if (books.containsKey(Book)) {
// person.borrowBook(books.get(Book));
// }else{
// System.out.println("Bookd is not available");
// }
// }else{
// System.out.println("Borrower doesn't exist is not registered!");
// }
// }
// public void returnBook(String borrower, String Book) {
// if (borrowers.containsKey(borrower)) {
// Borrower person = borrowers.get(borrower);
// if (books.containsKey(Book)) {
// person.returnBook();(books.get(Book));
// }else{
// System.out.println("Bookd is not available");
// }
// }else{
// System.out.println("Borrower doesn't exist is not registered!");
// }

// public void printAllBooks() {
// System.out.println("Library Books:");
// for (Book book : books.values()) {
// System.out.println("- " + book.getTitle() + " by " + book.getAuthor());
// }
// }
// }
