import java.io.*;
import java.math.BigInteger;
import java.text.NumberFormat;

public class MainTask {

  private static final File outputFile = new File("addends.txt");

  public static void main(String[] args) throws Exception {


    BigInteger ten = BigInteger.valueOf(10l);
    BigInteger four = BigInteger.valueOf(4l);
    BigInteger base = ten.pow(18);
    BigInteger biInput = base.multiply(four);

//      System.out.println(NumberFormat.getInstance().format(biInput));

    Long input = biInput.longValue();

    Long rangeStart;
    Long rangeEnd = input;

    if (outputFile.exists()) {
      rangeStart = Long.valueOf(tail(outputFile));
    } else {
      rangeStart = input-40_000_000_000_000l;
    }

    long startTime = System.nanoTime();

    try (
        FileWriter fw = new FileWriter(outputFile, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw)) {

      GoldbachSums4 goldbach = new GoldbachSums4(input, rangeStart, rangeEnd);

      int i = 0;
      for (Long addend : goldbach) {
        out.println(addend.toString());
        out.flush();
        i++;
        if (i > (Integer.MAX_VALUE / 100000)) {
          i = 0;
          System.out.println("working: " + addend);
        }
      }
    } finally {
      long duration = System.nanoTime() - startTime;
      System.err.printf("\nProcess too: %d ns or %f seconds.\n",
          duration, duration * 1e-9);
    }
  }

  public static String tail(File file) throws IOException {
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
