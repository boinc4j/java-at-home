import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedBoolArrayTest {

  private File testFile = new File("target", "bool_array.dat");

  public void setUp() throws IOException {
    Files.deleteIfExists(testFile.toPath());
  }

  public void tearDown() throws IOException {
    Files.deleteIfExists(testFile.toPath());
  }

  public void testInit() throws IOException {
    setUp();
    BoolArray a = new FileBackedBoolArray(testFile);

    assert !a.get(0l);
    assert !a.get(1l);
    assert !a.get(2l);
    assert !a.get(3l);
    assert !a.get(4l);
    assert !a.get(5l);
    assert !a.get(6l);
    assert !a.get(7l);
    assert !a.get(8l);
  }

  public void testSimple() throws IOException {
    setUp();
    BoolArray a = new FileBackedBoolArray(testFile);

    a.set(2l, true);
    a.set(1000l, true);

    assert !a.get(0l);
    assert a.get(2l);
    assert a.get(1000l);
    assert !a.get(1001l);
    assert !a.get(1002l);
    assert !a.get(99999l);
  }

  public void testSize() throws IOException {
    setUp();
    BoolArray a = new FileBackedBoolArray(testFile, 7l);

    a.set(2l, true);

    assert !a.get(0l);
    assert a.get(2l);
    assert !a.get(3l);

    assertIndexOutOfBoundsOnSet(a, 7l, true);
    assertIndexOutOfBoundsOnSet(a, 8l, true);
    assertIndexOutOfBoundsOnSet(a, 99999l, true);

    assertIndexOutOfBoundsOnGet(a, 7l);
    assertIndexOutOfBoundsOnGet(a, 8l);
  }

//  public void testMassive() throws IOException {
//    setUp();
//
//    BigInteger ten = BigInteger.valueOf(10l);
//    BigInteger base = ten.pow(9);
//    Long largeNumber = base.longValue();
//    BoolArray a = new BoolArray(testFile, largeNumber+1);
//
//    a.set(2l, true);
//
//    assert !a.get(0l);
//    assert a.get(2l);
//
//    assert !a.get(largeNumber);
//
//    a.set(largeNumber, true);
//    assert a.get(largeNumber);
//    assert !a.get(largeNumber-1);
//  }

  private void assertIndexOutOfBoundsOnSet(BoolArray a, Long i, Boolean b) throws IOException {
    try {
      a.set(i, b);
      assert false;
    } catch (ArrayIndexOutOfBoundsException e) {
      assert true;
    }
  }

  private void assertIndexOutOfBoundsOnGet(BoolArray a, Long i) throws IOException {
    try {
      a.get(i);
      assert false;
    } catch (ArrayIndexOutOfBoundsException e) {
      assert true;
    }
  }
}
