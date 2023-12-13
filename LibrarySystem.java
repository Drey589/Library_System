package OOP5BookBorrower;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LibrarySystem {

    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);
        mainMenu(library, scanner);
    }

    private static void mainMenu(Library library, Scanner scanner) {
        int choice;

        do {
            System.out.println("\nLibrary Management System Menu:\n");
            System.out.println("\t1. Add Book");
            System.out.println("\t2. Remove Book");
            System.out.println("\t3. Print All Books");
            System.out.println("\t4. Print All Borrowers");
            System.out.println("\t5. Borrowers");
            System.out.println("\t6. Exit\n");
            System.out.print("Enter your choice: ");

            choice = validChoice(scanner, 1, 6);
            scanner.nextLine();

            switch (choice) {
                case 1:
                    boolean stop = false;
                    int response;
                    System.out.println("\n**Add Book Section** \n");
                    do {
                        System.out.print("Enter book title: ");
                        String title = scanner.nextLine();
                        System.out.print("Enter book author: ");
                        String author = scanner.nextLine();
                        System.out.print("Enter number of book subject: ");
                        String subject = scanner.nextLine();
                        System.out.print("Enter number of stocks: ");
                        int stocks = validNumber(scanner);
                        System.out.println("");
                        scanner.nextLine();
                        library.addBooksToDatabase(title, author, stocks, subject);
                        System.out.print("Do you want to add another book [1] Yes [2] back to Menu: ");
                        response = validChoice(scanner, 1,2);
                        if (response == 2) {
                            stop = true;
                        } 
                        System.out.println("");
                        scanner.nextLine();
                    } while (!stop);
                    library.initializeLibraryFromDatabase();
                    break;
                case 2:
                    System.out.print("Enter the title of the book to remove: ");
                    String bookToRemove = scanner.nextLine();
                    library.removeBookFromDatabase(bookToRemove);
                    break;

                case 3:
                    ArrayList<Book> books = new ArrayList<>(library.getBooks().values());
                    library.printAllBooks(books);
                    break;

                case 4:
                    library.printBorrowers(library.getBorrowerList());
                    break;
                    case 5:
                    handleBorrowersMenu(library, scanner);
                    library.initializeLibraryFromDatabase();
                    break;
                case 6:
                    System.out.println("\nExiting Library System. Thank you!\n");
                    break;

                default:
                    System.out.println("\nInvalid choice. Please enter a valid option.");
                    break;
            }

        } while (choice != 6);
        scanner.close();
    }

    /*
     * TODO
     * 1. ADD PRINT BORROWERS PROFILE
     * 2. ADD MAIN MENU
     * 3. BETTER SELECTION OF BORROWERS
     * 4.
     */

    public static int validNumber(Scanner scan) {
        boolean valid = false;
        int input = 0;

        do {
            if (scan.hasNextInt()) {
                input = scan.nextInt();
                if (input >= 1) {
                    valid = true;
                } else {
                    scan.nextLine();
                    System.out.println("Invalid Input!");
                    System.out.print("Input number of stocks you have of the book to add: ");

                }
            } else {
                scan.nextLine();
                System.out.println("Invalid Input!");
                System.out.print("Input number of stocks you have of the book to add: ");

            }

        } while (!valid);
        return input;
    }

    public static int validChoice(Scanner scan, int min, int max) {
        boolean valid = false;
        int input = 0;

        do {
            if (scan.hasNextInt()) {
                input = scan.nextInt();
                if (input <= max && input >= min) {
                    valid = true;
                } else {
                    scan.nextLine();
                    System.out.println("Invalid Input!");
                    System.out.print("Enter your choice: ");

                }
            } else {
                scan.nextLine();
                System.out.println("Invalid Input!");
                System.out.print("Enter your choice: ");

            }

        } while (!valid);
        return input;
    }

    private static void handleBorrowersMenu(Library library, Scanner scanner) {

        int borrowerChoice;

        System.out.println("\n**Borrowers Section**");
        System.out.println("\nBorrowers with borrowed books: ");
        library.printBorrowers(library.borrowersWithBorrowedBooks());
        do {

            System.out.println("\n1. Print All the borrowers");
            System.out.println("2. Search Borrowers by Last Name");
            System.out.println("3. Search Borrowers Who Borrowed");
            System.out.println("4. Select Borrower");
            System.out.println("5. Add Borrower");
            System.out.println("6. Back\n");
            System.out.print("Enter your choice: ");

            borrowerChoice = validChoice(scanner, 1, 6);
            scanner.nextLine(); // Consume the newline character

            switch (borrowerChoice) {
                case 1:
                    library.printBorrowers(library.getBorrowerList());
                    break;

                case 2:
                    System.out.println("");
                    System.out.print("Search borrowers by Last Name: ");
                    String searchLastName = scanner.nextLine();
                    ArrayList<Borrower> foundBorrowers = library.searchBorrowerBylastName(searchLastName);
                    if (foundBorrowers.isEmpty()) {
                        System.out.println("No borrowers found!");
                    } else {
                        library.printBorrowers(foundBorrowers);
                    }
                    break;

                case 3:
                    System.out.println("");
                    System.out.print("Enter book title to search borrowers: ");
                    String searchTitle = scanner.nextLine();

                    ArrayList<Borrower> borrowersList = library.searchBorrowersWhoBorrowed(searchTitle);
                    if (borrowersList.isEmpty()) {
                        System.out.println("No borrowers found!");
                    } else {

                        library.printBorrowers(borrowersList);
                    }
                    break;
                case 4:
                    System.out.println("");
                    System.out.print("Enter first name to select: ");
                    String searchName = scanner.nextLine();
                    Borrower foundBorrower = library.searchByName(searchName);
                    if (foundBorrower != null) {
                        handleBorrowerOptions(library, scanner, foundBorrower);
                    } else {
                        System.out.println("Not found!");
                    }
                    break;

                case 5:
                    System.out.println("");
                    System.out.print("Enter borrower name: ");
                    String borrowerName = scanner.nextLine();
                    System.out.print("Enter borrower last name: ");
                    String borrowerLastName = scanner.nextLine();
                    System.out.print("Enter age: ");
                    int age = validChoice(scanner, 0, Integer.MAX_VALUE);
                    System.out.print("Enter address: ");
                    String address = scanner.nextLine();
                    System.out.print("Enter phone number:  ");
                    String phoneNumber = scanner.nextLine();
                    library.addBorrowerToDatabase(borrowerName, borrowerLastName, age, address, phoneNumber);
                    library.initializeLibraryFromDatabase();
                    break;

                case 6:
                    break;
            }

        } while (borrowerChoice != 6);
    }

    private static void handleBorrowerOptions(Library library, Scanner scanner, Borrower borrower) {
        int borrowerOption;

        System.out.println("\nBorrower Options for " + borrower.getName() + " " + borrower.getLastName() + ":");
        do {
            System.out.println("\n1. Add borrowed books");
            System.out.println("2. Return Borrowed Book");
            System.out.println("3. Print borrowed Books");
            System.out.println("4. Print Profile and Contact Informations");
            System.out.println("5. Back");
            System.out.println("6. Back to the main menu");
            System.out.print("\nEnter your choice: ");

            borrowerOption = validChoice(scanner, 1, 6);
            scanner.nextLine();

            switch (borrowerOption) {

                case 1:
                    handleBorrowingOptions(library, scanner, borrower);
                    break;
                case 2:
                    boolean returnExit = false;
                    do {
                        System.out.println("\nBorrowed Books:");
                        int index = 1;
                        for (Book book : borrower.getBorrowedBooks().values()) {
                            System.out.println(index + ". " + book.getTitle() + " by " + book.getAuthor());
                            index++;
                        }

                        System.out.print("\nEnter the number of the book to return (or 0 to exit): ");
                        int returnChoice = validChoice(scanner, 0, Integer.MAX_VALUE);

                        if (returnChoice == 0) {
                            returnExit = true;
                        } else if (returnChoice > 0 && returnChoice <= borrower.getBorrowedBooks().size()) {
                            List<Book> borrowedBooks = new ArrayList<>(borrower.getBorrowedBooks().values());
                            Book selectedBook = borrowedBooks.get(returnChoice - 1);
                            borrower.returnBookToDatabase(selectedBook);
                        } else {
                            System.out.println("Invalid choice. Please enter a valid option.");
                        }
                    } while (!returnExit);
                    library.initializeLibraryFromDatabase();
                    break;

                case 3:
                    System.out.println("");
                    ArrayList<Book> borrowedBooks = borrower.getBorrowedBooksList();
                    if (borrowedBooks.isEmpty()) {
                        System.out.println("No borrowed books!");
                    } else {
                        for (Book book : borrowedBooks) {
                            System.out.println(book.getTitle() + " by " + book.getAuthor());
                        }
                    }
                    break;
                    case 4: 
                        System.out.println("\n-----------------------------------\n");
                        System.out.println("Name: "+borrower.getName()+" "+borrower.getLastName());
                        System.out.println("age: "+borrower.getAge());
                        System.out.println("Address: "+borrower.getAddress());
                        System.out.println("Phone number: "+borrower.getPhoneNumber());
                        System.out.println("\n-----------------------------------\n");
                case 5:
                    break;
                case 6:
                    mainMenu(library, scanner);
                    break;

                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
                    break;
            }

        } while (borrowerOption != 5);
    }

    private static void handleBorrowingOptions(Library library, Scanner scanner, Borrower borrower) {
        int choice;
        do {
            System.out.println("\n1. Print All Books");
            System.out.println("2. Print books by subject");
            System.out.println("3. Add borrowed books");
            System.out.println("4.back\n");
            System.out.print("Enter your choice: ");
            choice = validChoice(scanner, 1, 4);
            scanner.nextLine();
            switch (choice) {
                case 1: 
                    ArrayList<Book> books = new ArrayList<>(library.getBooks().values());
                    library.printAllBooks(books);
                    break;
                    
                case 2:
                    String subject;
                    System.out.print("\nEnter subject: ");
                    subject = scanner.nextLine();
                    ArrayList<Book> foundBooks = library.searchBySubject(subject);
                    if (foundBooks.isEmpty()) {
                        System.out.println("\nNo books found!\n");
                    } else {
                        library.printAllBooks(foundBooks);
                    }
                    break;
                case 3:
                    boolean exit = false;
                    do {
                        System.out.print("\nEnter book to borrow: ");
                        String book = scanner.nextLine();
                        if (library.getBooks().containsKey(book)) {
                            borrower.borrowBookFromDatabase(library.getBooks().get(book));
                        } else {
                            System.out.println("Book is not register!");
                        }
                        System.out.print("\nEnter another booK? [1] yes [2] no: ");
                        int response = validChoice(scanner, 1, 2);
                        if (response == 2) {
                            exit = true;
                        }
                        scanner.nextLine();
                    } while (!exit);
                case 4:
                    break;
                default:
                    System.out.println("Invalid Choice!");
            }
        } while (choice != 4);
    }
/*  ADD BORROWERS WITH BORROWED BOOKS 
 *  Print Books by authors
 * Borrower found
*/


}
