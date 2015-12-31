import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class BoolArrayTest {

  private File testFile = new File("target", "bool_array.dat");

  public void setUp() {
    try {
      Files.deleteIfExists(testFile.toPath());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void tearDown() {
    try {
      Files.deleteIfExists(testFile.toPath());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void testSimple() throws IOException {
    BoolArray a = new BoolArray(testFile);

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
    BoolArray a = new BoolArray(testFile, 7l);

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
