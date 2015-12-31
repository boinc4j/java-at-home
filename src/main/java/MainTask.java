import java.io.PrintWriter;

public class MainTask {

  public static void main(String[] args) throws Exception {
    try (PrintWriter out = new PrintWriter("primes.txt")) {
      // figure out the number and range
      Long input = 0l;
      Long rangeStart = 0l;
      Long rangeEnd = 0l;

      GoldbachSums4 goldbach = new GoldbachSums4(input, rangeStart, rangeEnd);

      for (Long addend : goldbach) {
        out.println(addend.toString());
      }
    }
  }
}
