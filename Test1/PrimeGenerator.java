import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class PrimeGenerator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a positive integer: ");
        if (scanner.hasNextInt()) {
            int limit = scanner.nextInt();
            if (limit <= 0) {
                 System.out.println("Please enter a positive integer.");
            } else {
                List<Integer> primes = getPrimesLessThan(limit);
                System.out.println("Prime numbers less than " + limit + ": " + primes);
            }
        } else {
            System.out.println("Invalid input. Please enter an integer.");
        }
        scanner.close();
    }

    public static List<Integer> getPrimesLessThan(int n) {
        List<Integer> primes = new ArrayList<>();
        if (n <= 2) {
            return primes;
        }
        
        for (int i = 2; i < n; i++) {
            if (isPrime(i)) {
                primes.add(i);
            }
        }
        return primes;
    }

    public static boolean isPrime(int num) {
        if (num <= 1) return false;
        if (num == 2) return true;
        if (num % 2 == 0) return false;
        for (int i = 3; i <= Math.sqrt(num); i += 2) {
            if (num % i == 0) return false;
        }
        return true;
    }
}
