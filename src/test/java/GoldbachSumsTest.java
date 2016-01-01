import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GoldbachSumsTest {
  public void test22() throws IOException {
    Set<Long> primes = new HashSet<>();
    primes.add(3l);
    primes.add(5l);
    primes.add(11l);

    GoldbachSums goldbach = new GoldbachSums(22l, 0l, 22l, 35);

    Set<Long> primesFound = new HashSet<>();
    for (Long addend : goldbach) {
      assert primes.contains(addend) : "Unexpected addend: " + addend;
      primesFound.add(addend);
    }

    assert primesFound.equals(primes) : "Missing primes: " + (primes.removeAll(primesFound));
  }
}
