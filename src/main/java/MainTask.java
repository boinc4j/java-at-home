import java.io.*;

public class MainTask {

  public static final String DELIM = ",";

  public static final File OUTPUT_FILE = new File("addends.txt");

  public static void main(String[] args) throws Exception {
    try {
      if (args.length < 2) {
        throw new IllegalArgumentException("Must provide 2 arguments (start, end)");
      }

      Long start = Long.valueOf(args[0]);

      if (start < 6) {
        throw new IllegalArgumentException("Start value must be 6 or greater. Found " + start);
      }

      Long chunkSize = Long.valueOf(args[1]);
      Long end = start + chunkSize - 1;

      (new MainTask(start.longValue(), end.longValue())).work();
    } catch (Exception e) {
      try (FileWriter fw = new FileWriter(OUTPUT_FILE, true);
           BufferedWriter bw = new BufferedWriter(fw);
           PrintWriter out = new PrintWriter(bw)) {

        out.printf("ERROR: %s\n%s", e.getMessage());
        e.printStackTrace(out);
        out.flush();
      }
      Thread.sleep(120000);
    }
  }

  private Long rangeStart, rangeEnd;

  private File outputFile;

  public MainTask(Long start, Long end) throws IOException {
    this(start, end, OUTPUT_FILE);
  }

  public MainTask(Long start, Long end, File outputFile) throws IOException {
    if (outputFile.exists()) {
      String line = tail(outputFile);
      String[] lineParts = line.split(DELIM);
      this.rangeStart = Long.valueOf(lineParts[0]) + 2;
    } else {
      this.rangeStart = start;
    }
    this.rangeEnd = end;
    this.outputFile = outputFile;
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

      GoldbachSums goldbach = new GoldbachSums(input, 35);

      Long addend = goldbach.iterator().next();
      if (addend != null) {
        out.printf("%d%s%d\n", input, DELIM, addend);
      } else {
        out.printf("%d%s0\n", input, DELIM);
      }
      out.flush();
    }
  }

  public Long getRangeStart() {
    return this.rangeStart;
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
