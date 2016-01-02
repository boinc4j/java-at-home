import java.io.File;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

public class AssimilatorTest {

  public void testSimple() throws Exception {
    String jdbcUrl = "jdbc:h2:" + System.getProperty("user.dir") + "/target/test_" + UUID.randomUUID();
    File f = new File("target", "test_" + UUID.randomUUID() + ".txt");
    try {
      cleanDb(jdbcUrl);

      System.setProperty("boinc.jdbc.database.url", jdbcUrl);
      Assimilator a = new Assimilator();

      List<File> files = new ArrayList<>();

      try (PrintWriter out = new PrintWriter(f)) {
        out.println("6,3");
        out.print("8,3");
        out.flush();
      }

      files.add(f);

      a.assimilate("foobar", files);

      assertResults(jdbcUrl, new Long[]{6l, 3l}, new Long[]{8l, 3l});
    } finally {
      if (f.exists()) f.delete();
    }
  }

  public void testWithBadResults() throws Exception {
    String jdbcUrl = "jdbc:h2:"+ System.getProperty("user.dir")+"/target/test_" + UUID.randomUUID();
    File f = new File("target", "test_"+ UUID.randomUUID()+".txt");
    try {
      cleanDb(jdbcUrl);

      System.setProperty("boinc.jdbc.database.url", jdbcUrl);
      Assimilator a = new Assimilator();

      List<File> files = new ArrayList<>();

      try (PrintWriter out = new PrintWriter(f)) {
        out.println("6,3");
        out.println("7");
        out.println("8,3");
        out.flush();
      }

      files.add(f);

      a.assimilate("foobar", files);

      assertResults(jdbcUrl, new Long[]{6l, 3l}, new Long[]{8l, 3l});
    } finally {
      if (f.exists()) f.delete();
    }
  }

  private void cleanDb(String jdbcUrl) throws SQLException {
    try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("DROP TABLE goldbach IF EXISTS");
    }
  }

  private void assertResults(String jdbcUrl, Long[]... expectedResults) throws SQLException {
    try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT sum, addend FROM goldbach");

      Map<Long, Long> results = new HashMap<>();

      while (rs.next()) {
        Long sum = rs.getLong("sum");
        Long addend = rs.getLong("addend");
        results.put(sum, addend);
      }

      for (Long[] expectedResult : expectedResults) {
        Long sum = expectedResult[0];
        Long expectedAddend = expectedResult[1];
        Long actualAddend = results.get(sum);
        assert null != actualAddend : "Result for " + sum + " was null!";
        assert expectedAddend.equals(actualAddend) : "Result for " + sum + " was " + actualAddend;
      }

      assert expectedResults.length == results.size() : "Unexpected results in DB!";
    }
  }
}
