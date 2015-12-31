import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BoolArray {

  private static final int BYTE_SIZE = Byte.SIZE - 1;

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

    Long offset = Math.floorDiv(i, BYTE_SIZE);
    Long index = i % BYTE_SIZE;

    if (offset >= file.length()) return false;
    file.seek(offset);
    Byte b = file.readByte();

    return decodeValue(b, index.intValue());
  }

  public void set(Long i, Boolean value) throws IOException {
    if (i >= this.maxSize) throw new ArrayIndexOutOfBoundsException(i.toString());

    Long offset = Math.floorDiv(i, BYTE_SIZE);
    Long index = i % BYTE_SIZE;

    file.seek(offset);
    Byte oldByte = (offset < file.length()) ? file.readByte() : 0;

    Byte newByte = encodeValue(oldByte, index.intValue(), value);
    file.writeByte(newByte);
  }

  public Boolean decodeValue(byte b, int i) {
    return (b & (0x01 << i)) == (int)(Math.pow(2, i));
  }

  public Byte encodeValue(Byte b, int i, boolean val) {
    boolean cur = ((b & (0x01 << i)) != 0);
    if (val && !cur) {
      return Integer.valueOf(b.intValue() + (int)(Math.pow(2, i))).byteValue();
    } else if (!val && cur) {
      return Integer.valueOf(b.intValue() - (int)(Math.pow(2, i))).byteValue();
    }
    return b;
  }
}
