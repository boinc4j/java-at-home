import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assimilator {

  private static final String UPDATE = "INSERT INTO goldbach (sum, addend) VALUES (?,?)";
  private static final String CREATE = "CREATE TABLE IF NOT EXISTS goldbach (sum BIGINT, addend BIGINT)";

  public void assimilate(String workUnitId, List<File> files) throws Exception {
    File resultsFile = files.get(0);
    Map<Long,Long> results = new HashMap<>();
    try (BufferedReader br = new BufferedReader(new FileReader(resultsFile))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] parts = line.split(MainTask.DELIM);
        Long sum = Long.valueOf(parts[0]);
        Long addend = Long.valueOf(parts[1]);
        results.put(sum, addend);
      }
    }

    String updateTemplate = UPDATE;
    for (int i=1; i < results.size(); i++) {
      updateTemplate += ",(?,?)";
    }

    try (Connection connection =
             DriverManager.getConnection(
                 System.getProperty("jdbc.database.url", System.getenv("JDBC_DATABASE_URL")))) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate(CREATE);

      PreparedStatement updateStmt = connection.prepareStatement(updateTemplate);
      int i = 1;
      for (Long sum : results.keySet()) {
        updateStmt.setLong(i++, sum);
        updateStmt.setLong(i++, results.get(sum));
      }
      updateStmt.execute();
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
