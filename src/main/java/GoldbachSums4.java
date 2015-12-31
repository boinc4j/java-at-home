import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class GoldbachSums4 implements Iterable<Long> {

  private final Long sum;

  private final Long rangeStart;

  private final Long rangeEnd;

  public GoldbachSums4(Long sum, Long rangeStart, Long rangeEnd) throws IOException {
    if (sum <= 2) {
      throw new IllegalArgumentException("Number must be greater than 2.");
    }
    if (sum % 2 > 0) {
      throw new IllegalArgumentException("Number must be even.");
    }
    this.sum = sum;
    this.rangeStart = rangeStart;
    this.rangeEnd = rangeEnd;
  }

  @Override
  public Iterator<Long> iterator() {
    return new Iterator<Long>() {
      private Long halfSum = sum / 2;

      private Long lowerLimit = rangeStart < halfSum ? halfSum : rangeStart;

      private Long n = computeNext(rangeEnd-1);

      private Long computeNext(Long n) {
        for (long i = n; i >= lowerLimit; i -= 2) {
          if (BigInteger.valueOf(i).isProbablePrime(1) &&
              BigInteger.valueOf(sum - i).isProbablePrime(1)) {
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
      public Long next() {
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
//      System.out.print("Please input an integer to decompose as the sum of two primes: ");
//      Long input = scan.nextLong();

      BigInteger ten = BigInteger.valueOf(10l);
      BigInteger four = BigInteger.valueOf(4l);
      BigInteger base = ten.pow(18);
      BigInteger biInput = base.multiply(four);
      Long input = biInput.longValue();

      long startTime = System.nanoTime();
      GoldbachSums4 goldbach = new GoldbachSums4(input, input-100000, input);
      long duration = System.nanoTime() - startTime;
      System.err.printf("\nSieve of Eratosthenes took: %d ns or %f seconds.\n",
          duration, duration * 1e-9);
      duration = System.nanoTime() - startTime;
      for (long addend : goldbach) {
        System.out.printf("%d + %d = %d\n", input - addend, addend, input);
      }

      System.err.printf("\nThis program took: %d ns or %f seconds.\n",
          duration, duration * 1e-9);
    } catch (IllegalArgumentException badInput) {
      System.out.println(badInput.getMessage());
    }
  }
}