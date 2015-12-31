import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileBackedBoolArray extends BoolArray {

  private RandomAccessFile file;

  public FileBackedBoolArray(File f, Long maxSize) throws FileNotFoundException {
    this.maxSize = maxSize;
    this.file = new RandomAccessFile(f, "rws");
  }

  public FileBackedBoolArray(File f) throws FileNotFoundException {
    this(f, Long.MAX_VALUE);
  }

  @Override
  public Boolean get(Long i) throws IOException {
    if (i >= this.maxSize) throw new ArrayIndexOutOfBoundsException(i.toString());

    Long offset = Math.floorDiv(i, BYTE_SIZE);
    Long index = i % BYTE_SIZE;

    if (offset >= file.length()) return false;
    file.seek(offset);
    Byte b = file.readByte();

    return decodeValue(b, index.intValue());
  }

  @Override
  public void set(Long i, Boolean value) throws IOException {
    if (i >= this.maxSize) throw new ArrayIndexOutOfBoundsException(i.toString());

    Long offset = Math.floorDiv(i, BYTE_SIZE);
    Long index = i % BYTE_SIZE;

    file.seek(offset);
    Byte oldByte = (offset < file.length()) ? file.readByte() : 0;

    Byte newByte = encodeValue(oldByte, index.intValue(), value);
    file.writeByte(newByte);
  }
}
