import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class GoldbachSums implements Iterable<Long> {

  private final Long sum;

  private final Long rangeStart;

  private final Long rangeEnd;

  private Integer certainty;

  public GoldbachSums(Long sum, Integer certainty) throws IOException {
    this(sum, 0l, sum, certainty);
  }

  public GoldbachSums(Long sum, Long rangeStart, Long rangeEnd, Integer certainty) throws IOException {
    if (sum <= 2) {
      throw new IllegalArgumentException("Number must be greater than 2.");
    }
    if (sum % 2 > 0) {
      throw new IllegalArgumentException("Number must be even.");
    }
    this.sum = sum;
    this.rangeStart = rangeStart;
    this.rangeEnd = rangeEnd;
    this.certainty = certainty;
  }

  @Override
  public Iterator<Long> iterator() {
    return new Iterator<Long>() {
      private Long halfSum = sum / 2;

      private Long lowerLimit = rangeStart < halfSum ? halfSum : rangeStart;

      private Long n = computeNext(rangeEnd-1);

      private Long computeNext(Long n) {
        for (long i = n; i >= lowerLimit; i -= 2) {
          if (BigInteger.valueOf(i).isProbablePrime(certainty) &&
              BigInteger.valueOf(sum - i).isProbablePrime(certainty)) {
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
}