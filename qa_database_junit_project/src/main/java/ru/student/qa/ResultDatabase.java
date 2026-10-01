package ru.student.qa;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public final class ResultDatabase {
    private static final Path DIR = Path.of("qa-results");
    private static final String URL = "jdbc:sqlite:qa-results/qa-tests.db";
    private ResultDatabase() {}

    public static synchronized void initialize() {
        try { Files.createDirectories(DIR); } catch (IOException e) { throw new RuntimeException(e); }
        try (Connection c = DriverManager.getConnection(URL); Statement s = c.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS test_cases (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, test_set TEXT NOT NULL, " +
                    "input TEXT NOT NULL, expected REAL NOT NULL)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS test_runs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, run_date TEXT NOT NULL, " +
                    "test_set TEXT NOT NULL, total_tests INTEGER NOT NULL, " +
                    "passed_tests INTEGER NOT NULL, failed_tests INTEGER NOT NULL, " +
                    "execution_ms INTEGER NOT NULL, coverage_percent REAL, complexity REAL)");
            try (ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM test_cases")) {
                rs.next();
                if (rs.getInt(1) == 0) insertCases(c);
            }
        } catch (SQLException e) { throw new RuntimeException("SQLite error", e); }
    }

    private static void insertCases(Connection c) throws SQLException {
        String sql = "INSERT INTO test_cases(test_set,input,expected) VALUES(?,?,?)";
        try (PreparedStatement p = c.prepareStatement(sql)) {
            Object[][] rows = {
                    {"chess-counting", "0", 0.0}, {"chess-counting", "1", 1.0},
                    {"chess-counting", "2", 4.0}, {"chess-counting", "3", 9.0},
                    {"chess-counting", "4", 16.0}, {"chess-counting", "5", 25.0},
                    {"chess-counting", "10", 100.0}
            };
            for (Object[] r : rows) { p.setString(1,(String)r[0]); p.setString(2,(String)r[1]); p.setDouble(3,(Double)r[2]); p.addBatch(); }
            p.executeBatch();
        }
    }

    public static List<TestCase> loadTestCases(String set) {
        initialize();
        List<TestCase> out = new ArrayList<>();
        String sql = "SELECT input, expected FROM test_cases WHERE test_set=? ORDER BY id";
        try (Connection c=DriverManager.getConnection(URL); PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1,set);
            try(ResultSet rs=p.executeQuery()) { while(rs.next()) out.add(new TestCase(rs.getString(1),rs.getDouble(2))); }
        } catch(SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    public static long insertRun(String set, int total, int passed, int failed, long ms) {
        initialize();
        String sql="INSERT INTO test_runs(run_date,test_set,total_tests,passed_tests,failed_tests,execution_ms,coverage_percent,complexity) VALUES(?,?,?,?,?,?,NULL,NULL)";
        try(Connection c=DriverManager.getConnection(URL); PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, OffsetDateTime.now().toString()); p.setString(2,set); p.setInt(3,total); p.setInt(4,passed); p.setInt(5,failed); p.setLong(6,ms); p.executeUpdate();
            try(ResultSet k=p.getGeneratedKeys()){ k.next(); return k.getLong(1); }
        } catch(SQLException e){ throw new RuntimeException(e); }
    }

    public static void updateCoverage(double percent) {
        String sql="UPDATE test_runs SET coverage_percent=? WHERE id=(SELECT MAX(id) FROM test_runs)";
        try(Connection c=DriverManager.getConnection(URL); PreparedStatement p=c.prepareStatement(sql)){ p.setDouble(1,percent); p.executeUpdate(); }
        catch(SQLException e){ throw new RuntimeException(e); }
    }
}
