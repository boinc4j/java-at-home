import com.github.jkutner.boinc.BoincAssimilator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assimilator extends BoincAssimilator {

  private static final String UPDATE = "INSERT INTO goldbach (sum, addend) VALUES (?,?)";
  private static final String CREATE = "CREATE TABLE IF NOT EXISTS goldbach (sum BIGINT, addend BIGINT)";

  private static final String CREATE_ERROR = "CREATE TABLE IF NOT EXISTS " +
      "goldbach_errors (code VARCHAR(1028), work_unit_id code VARCHAR(1028), time timestamp)";
  private static final String INSERT_ERROR = "INSERT INTO code, work_unit_id, time VALUES (?,?,now())";

  public void assimilate(String workUnitId, List<File> files) throws Exception {
    File resultsFile = files.get(0);
    Map<Long,Long> results = readResults(resultsFile);

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
        Long addend = results.get(sum);
        updateStmt.setLong(i++, sum);
        updateStmt.setLong(i++, results.get(sum));

        if (addend == 0) {
          recordMiracle(connection, sum, addend);
        }
      }
      updateStmt.execute();
    }
  }

  public void assimilateError(String errorCode, String workUnitId) throws Exception {
    try (Connection connection = getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate(CREATE_ERROR);
      PreparedStatement updateStmt = connection.prepareStatement(INSERT_ERROR);
      updateStmt.setString(1, errorCode);
      updateStmt.setString(2, workUnitId);
    }
  }

  private Map<Long,Long> readResults(File resultsFile) throws IOException {
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
    return results;
  }

  private void recordMiracle(Connection connection, Long sum, Long addend) throws SQLException {
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS miracle (sum BIGINT, addend BIGINT)");
    PreparedStatement updateStmt = connection.prepareStatement("INSERT INTO miracle (sum, addend) VALUES (?,?)");
    updateStmt.setLong(1, sum);
    updateStmt.setLong(2, addend);
  }
}
