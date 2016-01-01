import java.io.*;
import java.math.BigInteger;

public class MainTask {

  private static final String DELIM = ",";

  private final File outputFile = new File("addends.txt");

  public static void main(String[] args) throws Exception {
    BigInteger ten = BigInteger.valueOf(10l);
    BigInteger four = BigInteger.valueOf(4l);
    BigInteger five = BigInteger.valueOf(5l);
    BigInteger base = ten.pow(18);

    BigInteger start = base.multiply(four);
    BigInteger end = base.multiply(five);

    (new MainTask(start.longValue(), end.longValue())).work();
  }

  private Long rangeStart, rangeEnd;

  public MainTask(Long start, Long end) throws IOException {
    if (outputFile.exists()) {
      String line = tail(outputFile);
      String[] lineParts = line.split(DELIM);
      this.rangeStart = Long.valueOf(lineParts[0]);
    } else {
      this.rangeStart = start;
    }
    this.rangeEnd = end;
  }

  public void work() throws IOException {
    long startTime = System.nanoTime();
    for (Long i = rangeStart; i <= rangeEnd; i += 2) {
      work(i);
    }
    mark(startTime);
  }

  public void work(Long input) throws IOException {
    try (FileWriter fw = new FileWriter(outputFile, true);
         BufferedWriter bw = new BufferedWriter(fw);
         PrintWriter out = new PrintWriter(bw)) {

      GoldbachSums goldbach = new GoldbachSums(input);

      Long addend = goldbach.iterator().next();
      if (addend != null) {
        out.printf("%d%s%d\n", input, DELIM, addend);
      } else {
        out.printf("%d%sNULL\n", input, DELIM);
      }
      out.flush();
    }
  }

  public void mark(Long startTime) {
    long duration = System.nanoTime() - startTime;
    System.err.printf("Time(%d,%d): %f seconds.\n", rangeStart, rangeEnd, duration * 1e-9);
  }

  public String tail(File file) throws IOException {
    try (RandomAccessFile fileHandler = new RandomAccessFile( file, "r" )) {
      long fileLength = fileHandler.length() - 1;
      StringBuilder sb = new StringBuilder();

      for(long filePointer = fileLength; filePointer != -1; filePointer--){
        fileHandler.seek( filePointer );
        int readByte = fileHandler.readByte();

        if( readByte == 0xA ) {
          if( filePointer == fileLength ) {
            continue;
          }
          break;

        } else if( readByte == 0xD ) {
          if( filePointer == fileLength - 1 ) {
            continue;
          }
          break;
        }

        sb.append( ( char ) readByte );
      }

      return sb.reverse().toString();
    }
  }
}
