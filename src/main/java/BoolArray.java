import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class BoolArray {

  public static final int BYTE_SIZE = Byte.SIZE - 1;

  protected Long maxSize;

  public abstract Boolean get(Long i) throws IOException;

  public abstract void set(Long i, Boolean value) throws IOException;

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
