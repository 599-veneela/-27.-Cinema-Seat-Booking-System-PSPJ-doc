
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

// CUSTOM EXCEPTION
class SeatBookingException extends Exception {
    public SeatBookingException(String message) {
        super(message);
    }
}

// INTERFACE
interface Bookable {
    void bookSeat(String seatNumber, Customer customer)
            throws SeatBookingException;

    void cancelSeat(String seatNumber)
            throws SeatBookingException;
}

// SEAT CLASS
class Seat {
    private String seatNumber;
    private boolean booked;

    public Seat(String seatNumber) {
        this.seatNumber = seatNumber;
        this.booked = false;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    @Override
    public String toString() {
        return seatNumber + " - "
                + (booked ? "Booked" : "Available");
    }
}

// CUSTOMER CLASS
class Customer {
    private String name;
    private String phone;

    public Customer(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return name + " (" + phone + ")";
    }
}

// MOVIE CLASS
class Movie implements Comparable<Movie> {
    private int movieId;
    private String title;
    private String language;
    private double ticketPrice;

    public Movie(int movieId, String title,
                 String language, double ticketPrice) {
        this.movieId = movieId;
        this.title = title;
        this.language = language;
        this.ticketPrice = ticketPrice;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getLanguage() {
        return language;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    @Override
    public String toString() {
        return movieId + " | " + title + " | "
                + language + " | ₹" + ticketPrice;
    }

    // Sort movies by title
    @Override
    public int compareTo(Movie other) {
        return this.title.compareToIgnoreCase(other.title);
    }
}

// SCREEN CLASS
class Screen implements Bookable {
    private int screenNumber;
    private Movie movie;
    private List<Seat> seats;

    public Screen(int screenNumber, Movie movie,
                  int numberOfSeats) {
        this.screenNumber = screenNumber;
        this.movie = movie;
        this.seats = new ArrayList<>();

        for (int i = 1; i <= numberOfSeats; i++) {
            seats.add(new Seat("S" + i));
        }
    }

    public int getScreenNumber() {
        return screenNumber;
    }

    public Movie getMovie() {
        return movie;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    // Find a seat
    private Seat findSeat(String seatNumber)
            throws SeatBookingException {
        for (Seat seat : seats) {
            if (seat.getSeatNumber()
                    .equalsIgnoreCase(seatNumber)) {
                return seat;
            }
        }

        throw new SeatBookingException(
                "Seat " + seatNumber + " does not exist."
        );
    }

    @Override
    public void bookSeat(String seatNumber, Customer customer)
            throws SeatBookingException {
        Seat seat = findSeat(seatNumber);

        if (seat.isBooked()) {
            throw new SeatBookingException(
                    "Seat " + seatNumber + " is already booked."
            );
        }

        seat.setBooked(true);

        System.out.println("\nSeat booked successfully!");
        System.out.println("Customer : " + customer.getName());
        System.out.println("Movie    : " + movie.getTitle());
        System.out.println("Seat     : " + seatNumber);
        System.out.println("Price    : ₹" + movie.getTicketPrice());
    }

    @Override
    public void cancelSeat(String seatNumber)
            throws SeatBookingException {
        Seat seat = findSeat(seatNumber);

        if (!seat.isBooked()) {
            throw new SeatBookingException(
                    "Seat " + seatNumber + " is not booked."
            );
        }

        seat.setBooked(false);
        System.out.println("Booking cancelled successfully.");
    }

    public void displaySeats() {
        System.out.println("\n--- Seat Status ---");

        for (Seat seat : seats) {
            System.out.println(seat);
        }
    }

    @Override
    public String toString() {
        return "Screen " + screenNumber
                + " | Movie: " + movie.getTitle();
    }
}

// BOOKING CLASS
class Booking {
    private static int nextBookingId = 1001;

    private int bookingId;
    private Customer customer;
    private Movie movie;
    private String seatNumber;

    public Booking(Customer customer, Movie movie,
                   String seatNumber) {
        this.bookingId = nextBookingId++;
        this.customer = customer;
        this.movie = movie;
        this.seatNumber = seatNumber;
    }

    // Constructor overloading
    public Booking(int bookingId, Customer customer,
                   Movie movie, String seatNumber) {
        this.bookingId = bookingId;
        this.customer = customer;
        this.movie = movie;
        this.seatNumber = seatNumber;
    }

    public int getBookingId() {
        return bookingId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Movie getMovie() {
        return movie;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    @Override
    public String toString() {
        return bookingId + " | "
                + customer.getName() + " | "
                + movie.getTitle() + " | "
                + seatNumber;
    }

    // Convert booking into file format
    public String toFileString() {
        return bookingId + ","
                + customer.getName() + ","
                + customer.getPhone() + ","
                + movie.getMovieId() + ","
                + movie.getTitle() + ","
                + seatNumber;
    }
}

// FILE MANAGER
class FileManager {
    private static final Path FILE =
            Path.of("bookings.txt");

    // Save booking by appending to file
    public static void saveBooking(Booking booking)
            throws IOException {
        try (BufferedWriter writer =
                     Files.newBufferedWriter(
                             FILE,
                             StandardOpenOption.CREATE,
                             StandardOpenOption.APPEND)) {
            writer.write(booking.toFileString());
            writer.newLine();
        }
    }

    // Read all bookings
    public static List<String> loadBookings()
            throws IOException {
        List<String> records = new ArrayList<>();

        if (!Files.exists(FILE)) {
            return records;
        }

        try (BufferedReader reader =
                     Files.newBufferedReader(FILE)) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    records.add(line);
                }
            }
        }

        return records;
    }

    // Display stored bookings
    public static void displayBookings()
            throws IOException {
        List<String> records = loadBookings();

        System.out.println("\n--- Saved Bookings ---");

        if (records.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (String record : records) {
            System.out.println(record);
        }
    }
}

// MAIN CLASS
public class CinemaBookingSystem {
    private static final Scanner scanner =
            new Scanner(System.in);

    private static final List<Movie> movies =
            new ArrayList<>();

    private static final List<Booking> bookings =
            new ArrayList<>();

    private static Screen screen;

    // MAIN METHOD
    public static void main(String[] args) {
        initializeMovies();

        // Create screen using first movie
        screen = new Screen(
                1,
                movies.get(0),
                20
        );

        boolean running = true;

        while (running) {
            displayMenu();

            try {
                int choice = Integer.parseInt(
                        scanner.nextLine()
                );

                switch (choice) {
                    case 1:
                        displayMovies();
                        break;

                    case 2:
                        displaySeats();
                        break;

                    case 3:
                        bookTicket();
                        break;

                    case 4:
                        cancelTicket();
                        break;

                    case 5:
                        displayBookings();
                        break;

                    case 6:
                        sortMovies();
                        break;

                    case 7:
                        searchMovie();
                        break;

                    case 8:
                        running = false;
                        System.out.println(
                                "Thank you for using Cinema Booking System!"
                        );
                        break;

                    default:
                        System.out.println("Invalid menu choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println(
                        "Please enter a valid number."
                );
            } catch (IOException e) {
                System.out.println(
                        "File error: " + e.getMessage()
                );
            }
        }

        scanner.close();
    }

    // MENU
    private static void displayMenu() {
        System.out.println("\n================================");
        System.out.println("      CINEMA SEAT BOOKING");
        System.out.println("================================");
        System.out.println("1. View Movies");
        System.out.println("2. View Seats");
        System.out.println("3. Book Ticket");
        System.out.println("4. Cancel Ticket");
        System.out.println("5. View Saved Bookings");
        System.out.println("6. Sort Movies");
        System.out.println("7. Search Movie");
        System.out.println("8. Exit");
        System.out.println("================================");
        System.out.print("Enter choice: ");
    }

    // INITIALIZE MOVIES
    private static void initializeMovies() {
        movies.add(
                new Movie(1, "Avengers", "English", 250)
        );

        movies.add(
                new Movie(2, "RRR", "Telugu", 200)
        );

        movies.add(
                new Movie(3, "Pushpa", "Telugu", 220)
        );

        movies.add(
                new Movie(4, "Interstellar", "English", 300)
        );
    }

    // DISPLAY MOVIES
    private static void displayMovies() {
        System.out.println("\n--- Movies ---");

        for (Movie movie : movies) {
            System.out.println(movie);
        }
    }

    // DISPLAY SEATS
    private static void displaySeats() {
        screen.displaySeats();
    }

    // BOOK TICKET
    private static void bookTicket() {
        try {
            System.out.print("Enter customer name: ");
            String name = scanner.nextLine().trim();

            if (name.isEmpty()) {
                throw new IllegalArgumentException(
                        "Customer name cannot be empty."
                );
            }

            System.out.print("Enter phone number: ");
            String phone = scanner.nextLine().trim();

            if (phone.isEmpty()) {
                throw new IllegalArgumentException(
                        "Phone number cannot be empty."
                );
            }

            System.out.print("Enter seat number: ");
            String seatNumber = scanner.nextLine().trim();

            Customer customer = new Customer(name, phone);

            // Book seat
            screen.bookSeat(seatNumber, customer);

            // Create booking object
            Booking booking = new Booking(
                    customer,
                    screen.getMovie(),
                    seatNumber
            );

            bookings.add(booking);

            // Persist booking
            FileManager.saveBooking(booking);

            System.out.println(
                    "Booking ID: " + booking.getBookingId()
            );

        } catch (
                SeatBookingException |
                IllegalArgumentException |
                IOException e
        ) {
            System.out.println(
                    "Booking failed: " + e.getMessage()
            );
        }
    }

    // CANCEL TICKET
    private static void cancelTicket() {
        try {
            System.out.print("Enter seat number to cancel: ");
            String seatNumber = scanner.nextLine().trim();

            screen.cancelSeat(seatNumber);

        } catch (SeatBookingException e) {
            System.out.println(
                    "Cancellation failed: " + e.getMessage()
            );
        }
    }

    // DISPLAY SAVED BOOKINGS
    private static void displayBookings()
            throws IOException {
        FileManager.displayBookings();
    }

    // SORT MOVIES
    private static void sortMovies() {
        // Sort alphabetically using Comparable
        Collections.sort(movies);

        System.out.println(
                "\nMovies sorted alphabetically:"
        );
        displayMovies();

        // Sort by ticket price using Comparator
        movies.sort(
                Comparator.comparingDouble(
                        Movie::getTicketPrice
                )
        );

        System.out.println(
                "\nMovies sorted by ticket price:"
        );
        displayMovies();
    }

    // SEARCH MOVIE
    private static void searchMovie() {
        System.out.print("Enter movie title to search: ");

        String search = scanner.nextLine().trim();
        boolean found = false;

        for (Movie movie : movies) {
            if (movie.getTitle()
                    .equalsIgnoreCase(search)) {
                System.out.println("Movie found: " + movie);
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("Movie not found.");
        }
    }
}
