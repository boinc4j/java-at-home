import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BoolArray {

  private RandomAccessFile file;

  private Long maxSize;

  public BoolArray(File f, Long maxSize) throws FileNotFoundException {
    this.maxSize = maxSize;
    this.file = new RandomAccessFile(f, "rws");
  }

  public BoolArray(File f) throws FileNotFoundException {
    this(f, Long.MAX_VALUE);
  }

  public Boolean get(Long i) throws IOException {
    if (i >= this.maxSize) throw new ArrayIndexOutOfBoundsException(i.toString());
    if (i >= file.length()) return false;
    file.seek(i);
    return file.readBoolean();
  }

  public void set(Long i, Boolean value) throws IOException {
    if (i >= this.maxSize) throw new ArrayIndexOutOfBoundsException(i.toString());
    file.seek(i);
    file.writeBoolean(value);
  }
}
