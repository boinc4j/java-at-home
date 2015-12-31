import java.io.*;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MainTask {

  private static final int ADDEND_BUFFER_SIZE = 100;

  private final File outputFile = new File("addends.txt");

  public static void main(String[] args) throws Exception {
    (new MainTask()).work();
  }

  private Long input;
  private Long rangeStart;
  private Long rangeEnd;

  public MainTask() throws IOException {
    BigInteger ten = BigInteger.valueOf(10l);
    BigInteger four = BigInteger.valueOf(4l);
    BigInteger base = ten.pow(18);
    BigInteger biInput = base.multiply(four);

//      System.out.println(NumberFormat.getInstance().format(biInput));

    this.input = biInput.longValue();

    if (outputFile.exists()) {
      this.rangeStart = Long.valueOf(tail(outputFile));
    } else {
      this.rangeStart = input-40_000_000_000_000l;
    }

    this.rangeEnd = input;
  }

  public void work() throws IOException {
    long startTime = System.nanoTime();
    try (FileWriter fw = new FileWriter(outputFile, true);
         BufferedWriter bw = new BufferedWriter(fw);
         PrintWriter out = new PrintWriter(bw)) {

      GoldbachSums4 goldbach = new GoldbachSums4(input, rangeStart, rangeEnd);

      long i = 0;
      List<Long> buffer = new ArrayList<>(ADDEND_BUFFER_SIZE);
      for (Long addend : goldbach) {
        buffer.add(addend);
        if (buffer.size() == ADDEND_BUFFER_SIZE) {
          out.print(join(buffer, "\n"));
          out.flush();
          buffer = new ArrayList<>(ADDEND_BUFFER_SIZE);
        }

        if (i > 200000) { mark(startTime, i); i = 0;} else {i++;}
      }
    } finally {
      mark(startTime, rangeEnd);
    }
  }

  public void mark(Long startTime, Long i) {
    long duration = System.nanoTime() - startTime;
    System.err.printf("Time(%d): %f seconds.\n", i, duration * 1e-9);
  }

  public String join(List<Long> values, String delim) {
    String s = "";
    for (Long v : values) {
      s += v + delim;
    }
    return s;
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
