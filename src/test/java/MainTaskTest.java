import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MainTaskTest {
  public void testTail() throws IOException {
    File f = new File("test_"+ UUID.randomUUID()+".txt");
    try {
      MainTask t = new MainTask(1l, 2l);
      try (PrintWriter out = new PrintWriter(f)) {
        out.println("hello test");
        out.print("goodbye test");
        out.flush();
      }

      String line = t.tail(f);

      assert "goodbye test".equals(line) : "Expected \"goodbye test\", but got: " + line;
    } finally {
      f.delete();
    }
  }
}
