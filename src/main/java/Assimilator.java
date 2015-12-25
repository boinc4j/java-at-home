import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Assimilator {

  public void assimilate(String workUnitId, List<File> files) throws Exception {
    // todo
    try (Connection connection =
             DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"))) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

      while (rs.next()) {
        System.out.println("Read from DB: " + rs.getTimestamp("tick"));
      }
    }
  }

  public void assimilateError(String errorCode, String workUnitId) throws Exception {
    // todo
    try (Connection connection =
             DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"))) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
      stmt.executeUpdate("INSERT INTO ticks VALUES (TIMESTAMP('2000-01-01 00:00:00','00:00:00'))");
      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

      while (rs.next()) {
        System.out.println( "Read from DB: " + rs.getTimestamp("tick"));
      }
    }
  }

  public static void main(String[] args) throws Exception {
    if ("--error".equals(args[0])) {
      String code = args[1];
      String wuid = args[2];
      (new Assimilator()).assimilateError(code, wuid);
    } else {
      String wuid = args[0];
      List<File> files = new ArrayList<>();
      for (int i = 1; i < args.length; i++) {
        File f = new File(args[i]);
        files.add(f);
      }
      (new Assimilator()).assimilate(wuid, files);

    }
  }
}
