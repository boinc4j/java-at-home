import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class AssimilatorTest {

  public void testSimple() throws Exception {
    String jdbcUrl = "jdbc:h2:"+ System.getProperty("user.dir")+"/target/test_" + UUID.randomUUID();
    File f = new File("target", "test_"+ UUID.randomUUID()+".txt");
    try {
      try (Connection connection =
               DriverManager.getConnection(jdbcUrl)) {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DROP TABLE goldbach IF EXISTS");
      }

      System.setProperty("jdbc.database.url", jdbcUrl);

      Assimilator a = new Assimilator();

      List<File> files = new ArrayList<>();

      try (PrintWriter out = new PrintWriter(f)) {
        out.println("6,3");
        out.print("8,3");
        out.flush();
      }

      files.add(f);

      a.assimilate("foobar", files);

      try (Connection connection =
               DriverManager.getConnection(jdbcUrl)) {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT sum, addend FROM goldbach");

        Map<Long,Long> results = new HashMap<>();

        while (rs.next()) {
          Long sum = rs.getLong("sum");
          Long addend = rs.getLong("addend");
          results.put(sum, addend);
        }

        Long three = 3l;
        Long sixResult = results.get(6l);
        Long eightResult = results.get(8l);

        assert null != sixResult : "Result for 6 was null!";
        assert three.equals(sixResult) : "Result for 6 was not 3";
        assert null != eightResult : "Result for 8 was null!";
        assert three.equals(eightResult) : "Result for 8 was not 3";
      }
    } finally {
      if (f.exists()) f.delete();
    }
  }
}
