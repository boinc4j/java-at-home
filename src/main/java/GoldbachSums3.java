import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Borrowed from:
 * http://codereview.stackexchange.com/questions/110855/goldbachs-conjecture-using-sieve-of-eratosthenes
 */
public class GoldbachSums3 implements Iterable<Long> {

  private final Long sum;

  private final BoolArray ineligible;

  private static final File primesFile = new File("primes.dat");

  public GoldbachSums3(Long sum) throws IOException {
    if (sum <= 2) {
      throw new IllegalArgumentException("Number must be greater than 2.");
    }
    if (sum % 2 > 0) {
      throw new IllegalArgumentException("Number must be even.");
    }
    this.sum = sum;
    this.ineligible = oddSieveOfEratosthenes(sum);
  }

  private static BoolArray oddSieveOfEratosthenes(Long max) throws IOException {
    BoolArray ineligible = new BoolArray(primesFile, max);
    ineligible.set(1l, true);
    for (long i = 3; i * i < max; i += 2) {
      if (!ineligible.get(i)) {
        for (long j = i * i; j < max; j += i) {
          ineligible.set(j, true);
        }
      }
    }
    return ineligible;
  }

  @Override
  public Iterator<Long> iterator() {
    return new Iterator<Long>() {
      // 4 is the only sum whose decomposition involves an even prime
      private final Long halfSum = sum / 2;
      private Long n = (sum == 4l) ? 2l : computeNext(sum - 1l);

      private Long computeNext(Long n) {
        for (long i = n; i >= halfSum; i -= 2) {
          try {
            if (!ineligible.get(i) && !ineligible.get(sum - i)) {
              return sum - i;
            }
          } catch (IOException e) {
            throw new RuntimeException("Failed to read ineligible for i="+i, e);
          }
        }
        return null;
      }

      @Override
      public boolean hasNext() {
        return n != null;
      }

      @Override
      public Long next() {
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

  public static void main(String[] args) throws IOException {
    try (Scanner scan = new Scanner(System.in)) {
      System.out.print("Please input an integer to decompose as the sum of two primes: ");
      Long input = scan.nextLong();

      long startTime = System.nanoTime();
      GoldbachSums3 goldbach = new GoldbachSums3(input);
      long duration = System.nanoTime() - startTime;
      System.err.printf("\nSieve of Eratosthenes took: %d ns or %f seconds.\n",
          duration, duration * 1e-9);
      duration = System.nanoTime() - startTime;
      for (long addend : goldbach) {
        System.out.printf("%d + %d = %d\n", input - addend, addend, input);
      }
      ;
      System.err.printf("\nThis program took: %d ns or %f seconds.\n",
          duration, duration * 1e-9);
    } catch (IllegalArgumentException badInput) {
      System.out.println(badInput.getMessage());
    }
  }
}