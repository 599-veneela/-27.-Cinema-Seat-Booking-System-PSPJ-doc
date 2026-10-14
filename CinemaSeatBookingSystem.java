import java.util.Scanner;

public class CinemaSeatBookingSystem {

    static boolean[] seats = new boolean[20];

    // Display all seats
    static void displaySeats() {
        System.out.println("\n========== CINEMA SEATS ==========");

        for (int i = 0; i < seats.length; i++) {

            if (seats[i]) {
                System.out.print("[X] ");
            } else {
                System.out.print("[" + (i + 1) + "] ");
            }

            if ((i + 1) % 5 == 0) {
                System.out.println();
            }
        }

        System.out.println("X = Booked");
        System.out.println("==================================");
    }

    // Book a seat
    static void bookSeat(Scanner sc) {

        System.out.print("\nEnter customer name: ");
        sc.nextLine();
        String customerName = sc.nextLine();

        System.out.print("Enter seat number (1-20): ");
        int seatNumber = sc.nextInt();

        // Check whether seat number is valid
        if (seatNumber < 1 || seatNumber > 20) {
            System.out.println("Invalid seat number!");
            return;
        }

        // Check whether seat is already booked
        if (seats[seatNumber - 1]) {
            System.out.println("Sorry! Seat " + seatNumber + " is already booked.");
        } else {

            // Book the seat
            seats[seatNumber - 1] = true;

            System.out.println("\n========== BOOKING CONFIRMATION ==========");
            System.out.println("Customer Name : " + customerName);
            System.out.println("Seat Number   : " + seatNumber);
            System.out.println("Booking Status: CONFIRMED");
            System.out.println("==========================================");
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int choice;

        do {
            System.out.println("\n=================================");
            System.out.println("   CINEMA SEAT BOOKING SYSTEM");
            System.out.println("=================================");
            System.out.println("1. View Seats");
            System.out.println("2. Book Seat");
            System.out.println("3. Exit");
            System.out.println("=================================");

            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    displaySeats();
                    break;

                case 2:
                    displaySeats();
                    bookSeat(sc);
                    break;

                case 3:
                    System.out.println("\nThank you for using Cinema Seat Booking System!");
                    break;

                default:
                    System.out.println("Invalid choice! Please try again.");
            }

        } while (choice != 3);

        sc.close();
    }
}