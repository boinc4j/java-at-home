import java.io.FileNotFoundException;

public class InMemBoolArray extends BoolArray {

  private Long rangeStart;

  private Long rangeEnd;

  private byte[] internalArray;

  public InMemBoolArray(Long rangeStart, Long rangeEnd) throws FileNotFoundException {
    this.rangeEnd = rangeEnd;
    this.rangeStart = rangeStart;
    Long internalLogicalMaxSize = rangeEnd - rangeStart;

    Long internalPhysicalMaxSize = Math.floorDiv(internalLogicalMaxSize, BYTE_SIZE) + 1;
    if (internalPhysicalMaxSize > Integer.MAX_VALUE) {
      throw new ArrayIndexOutOfBoundsException("Initial size is to big: " + maxSize);
    }

    this.internalArray = new byte[internalPhysicalMaxSize.intValue()];
  }

  public Boolean get(Long i) {
    validateIndex(i);

    Long internalLogicalIndex = i - rangeStart;
    Integer internalPhysicalIndex = (new Long(Math.floorDiv(internalLogicalIndex, BYTE_SIZE))).intValue();
    Integer byteIndex = (new Long(internalLogicalIndex % BYTE_SIZE)).intValue();

    return decodeValue(internalArray[internalPhysicalIndex], byteIndex);
  }

  public void set(Long i, Boolean value) {
    validateIndex(i);

    Long internalLogicalIndex = i - rangeStart;
    Integer internalPhysicalIndex = (new Long(Math.floorDiv(internalLogicalIndex, BYTE_SIZE))).intValue();
    Integer byteIndex = (new Long(internalLogicalIndex % BYTE_SIZE)).intValue();

    byte oldByte = internalArray[internalPhysicalIndex];
    internalArray[internalPhysicalIndex] = encodeValue(oldByte, byteIndex, value);
  }

  public void validateIndex(Long i) {
    if (i >= this.rangeEnd) throw new ArrayIndexOutOfBoundsException(i.toString());
    if (i < this.rangeStart) throw new ArrayIndexOutOfBoundsException(i.toString());
  }
}
