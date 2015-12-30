import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Borrowed from:
 * http://codereview.stackexchange.com/questions/110855/goldbachs-conjecture-using-sieve-of-eratosthenes
 */
public class GoldbachSums2 implements Iterable<Integer> {
  private final int sum;
  private final byte[] ineligible;

  private static final Path primesFilePath = new File("primes.dat").toPath();

  public GoldbachSums2(int sum) throws IOException {
    if (sum <= 2) {
      throw new IllegalArgumentException("Number must be greater than 2.");
    }
    if (sum % 2 > 0) {
      throw new IllegalArgumentException("Number must be even.");
    }
    this.sum = sum;
    this.ineligible = oddSieveOfEratosthenes(sum);
  }

  private static byte[] oddSieveOfEratosthenes(int max) throws IOException {
    boolean[] ineligible = new boolean[max];
    ineligible[1] = true;
    for (int i = 3; i * i < max; i += 2) {
      if (! ineligible[i]) {
        for (int j = i * i; j < max; j += i) {
          ineligible[j] = true;
        }
      }
    }

    byte[] primeBytes = new byte[max];
    for (int i = 0; i < max; i++) {
      primeBytes[i] = ineligible[i] ? Byte.MAX_VALUE : Byte.MIN_VALUE;
    }

    //System.out.println("writing file");
    if (Files.exists(primesFilePath)) Files.delete(primesFilePath);
    Files.write(primesFilePath, primeBytes, StandardOpenOption.APPEND, StandardOpenOption.CREATE);

    System.out.println(primeBytes);

    System.out.println(Files.readAllBytes(primesFilePath));

    return Files.readAllBytes(primesFilePath); //Files.readAllBytes(primesFilePath);
  }

  @Override
  public Iterator<Integer> iterator() {
    return new Iterator<Integer>() {
      // 4 is the only sum whose decomposition involves an even prime
      private Integer n = (sum == 4) ? 2 : computeNext(sum - 1);
      private final int halfSum = sum / 2;

      private Integer computeNext(int n) {
        for (int i = n; i >= halfSum; i -= 2) {
          if (ineligible[i] == Byte.MIN_VALUE && ineligible[sum - i] == Byte.MIN_VALUE) {
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
    };
  }

  public static void main(String[] args) throws IOException {
    try (Scanner scan = new Scanner(System.in)) {
      System.out.print("Please input an integer to decompose as the sum of two primes: ");
      int input = scan.nextInt();

      long startTime = System.nanoTime();
      GoldbachSums2 goldbach = new GoldbachSums2(input);
      long duration = System.nanoTime() - startTime;
      System.err.printf("\nSieve of Eratosthenes took: %d ns or %f seconds.\n",
          duration, duration * 1e-9);
      duration = System.nanoTime() - startTime;
      for (int addend : goldbach) {
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