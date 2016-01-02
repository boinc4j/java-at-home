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
        if (parts.length == 2) {
          Long sum = Long.valueOf(parts[0]);
          Long addend = Long.valueOf(parts[1]);
          results.put(sum, addend);
        }
      }
    }

    String updateTemplate = UPDATE;
    for (int i=1; i < results.size(); i++) {
      updateTemplate += ",(?,?)";
    }

    try (Connection connection = getConnection()) {
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
    try (Connection connection = getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate(
          "CREATE TABLE IF NOT EXISTS " +
              "goldbach_errors (code VARCHAR(1028), work_unit_id code VARCHAR(1028), time timestamp)");
      PreparedStatement updateStmt = connection.prepareStatement(
          "INSERT INTO code, work_unit_id, time VALUES (?,?,now())");
      updateStmt.setString(1, errorCode);
      updateStmt.setString(2, workUnitId);
    }
  }

  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(getJdbcUrl());
  }

  public String getJdbcUrl() {
    return System.getProperty("boinc.jdbc.database.url", System.getenv("JDBC_DATABASE_URL"));
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
