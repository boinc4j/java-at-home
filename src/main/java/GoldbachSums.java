import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Borrowed from:
 * http://codereview.stackexchange.com/questions/110855/goldbachs-conjecture-using-sieve-of-eratosthenes
 */
public class GoldbachSums implements Iterable<Integer> {
    private final int sum;
    private final boolean[] ineligible;

    public GoldbachSums(int sum) {
        if (sum <= 2) {
            throw new IllegalArgumentException("Number must be greater than 2.");
        }
        if (sum % 2 > 0) {
            throw new IllegalArgumentException("Number must be even.");
        }
        this.sum = sum;
        this.ineligible = oddSieveOfEratosthenes(sum);
    }

    private static boolean[] oddSieveOfEratosthenes(int max) {
        boolean[] ineligible = new boolean[max];
        ineligible[1] = true;
        for (int i = 3; i * i < max; i += 2) {
            if (! ineligible[i]) {
                for (int j = i * i; j < max; j += i) {
                    ineligible[j] = true;
                }
            }
        }
        return ineligible;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            // 4 is the only sum whose decomposition involves an even prime
            private Integer n = (sum == 4) ? 2 : computeNext(sum - 1);
            private final int halfSum = sum / 2;

            private Integer computeNext(int n) {
                for (int i = n; i >= halfSum; i -= 2) {
                    if (!ineligible[i] && !ineligible[sum - i]) {
                        return sum - i;
                    }
                }
                return null;
            }

            @Override
            public boolean hasNext() {
                return n != null;
            }

            @Override
            public Integer next() {
                if (n == null) {
                    throw new NoSuchElementException();
                } else try {
                    return n;
                } finally {
                    n = computeNext(sum - n - 2);
                }
            }

            // For compatibility with Java < 8
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static void main(String[] args) {
        try (Scanner scan = new Scanner(System.in)) {
            System.out.print("Please input an integer to decompose as the sum of two primes: ");
            int input = scan.nextInt();

            long startTime = System.nanoTime();
            GoldbachSums goldbach = new GoldbachSums(input);
            long duration = System.nanoTime() - startTime;
            System.err.printf("\nSieve of Eratosthenes took: %d ns or %f seconds.\n",
                    duration, duration * 1e-9);
            duration = System.nanoTime() - startTime;
            for (int addend : goldbach) {
                System.out.printf("%d + %d = %d\n", input - addend, addend, input);
            };
            System.err.printf("\nThis program took: %d ns or %f seconds.\n",
                    duration, duration * 1e-9);
        } catch (IllegalArgumentException badInput) {
            System.out.println(badInput.getMessage());
        }
    }
}