import java.io.*;
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
      if (f.exists()) f.delete();
    }
  }

  public void testSmallRange() throws IOException {
    File f = new File("test_"+ UUID.randomUUID()+".txt");
    try {
      (new MainTask(6l, 8l, f)).work();

      if (f.exists()) {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
          String line = br.readLine();
          assert "6,3".equals(line) : "Unexpected: " + line;

          line = br.readLine();
          assert "8,3".equals(line) : "Unexpected: " + line;
        }
      } else {
        assert false : "File does not exist!";
      }
    } finally {
      if (f.exists()) f.delete();
    }
  }

  public void testResume() throws IOException {
    File f = new File("test_"+ UUID.randomUUID()+".txt");
    try {
      (new MainTask(6l, 12l, f)).work();

      MainTask t = new MainTask(6l, 24l, f);

      assert t.getRangeStart() == 14l;
    } finally {
      if (f.exists()) f.delete();
    }
  }
}
