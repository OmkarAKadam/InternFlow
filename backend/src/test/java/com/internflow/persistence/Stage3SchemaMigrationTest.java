package com.internflow.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

/**
 * Verifies the Stage 3.1 migration against a real PostgreSQL instance.
 *
 * <p>This test is opt-in because the repository's unit/context tests must run
 * without a database (see the Surefire configuration in {@code pom.xml}). Set
 * {@code INTERNFLOW_TEST_DB_URL}, {@code INTERNFLOW_TEST_DB_USERNAME} and
 * {@code INTERNFLOW_TEST_DB_PASSWORD} to a <strong>disposable</strong> database
 * to run it. The test creates an isolated, uniquely named schema, applies the
 * production migrations there, checks the expected tables and negative
 * constraints, and drops only that schema again.</p>
 *
 * <p>Verified constraints: duplicate identity, role value, duplicate active
 * cohort membership, duplicate active coordinator assignment, single active
 * primary coordinator, date ordering, single REGISTERED internship, and
 * RESTRICT foreign keys.</p>
 */
@EnabledIfEnvironmentVariable(named = "INTERNFLOW_TEST_DB_URL", matches = ".+")
class Stage3SchemaMigrationTest {

    private static final String SCHEMA = "stage3_1_" + System.currentTimeMillis();

    private static final List<String> EXPECTED_TABLES = List.of(
            "users",
            "cohorts",
            "cohort_memberships",
            "cohort_coordinator_assignments",
            "companies",
            "internships");

    @Test
    void migrationCreatesStage3TablesAndEnforcesConstraints() throws Exception {
        String url = requireEnv("INTERNFLOW_TEST_DB_URL");
        String username = requireEnv("INTERNFLOW_TEST_DB_USERNAME");
        String password = System.getenv().getOrDefault("INTERNFLOW_TEST_DB_PASSWORD", "");

        Flyway flyway = Flyway.configure()
                .dataSource(url, username, password)
                .locations("classpath:db/migration")
                .schemas(SCHEMA)
                .defaultSchema(SCHEMA)
                .createSchemas(true)
                .load();

        try {
            assertEquals(1, flyway.migrate().migrationsExecuted,
                    "the Stage 3.1 migration should apply exactly once to a fresh schema");

            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                connection.createStatement().execute("SET search_path TO " + SCHEMA);

                assertExpectedTables(connection);
                assertChecksAndUniqueness(connection);
                assertActiveMembershipAndCoordinatorRules(connection);
                assertInternshipRules(connection);
                assertNoCascadeDeletion(connection);
            }
        } finally {
            dropTestSchema(url, username, password);
        }
    }

    private static void assertExpectedTables(Connection connection) throws SQLException {
        try (var statement = connection.prepareStatement(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = ?")) {
            statement.setString(1, SCHEMA);
            try (ResultSet rows = statement.executeQuery()) {
                var found = new java.util.ArrayList<String>();
                while (rows.next()) {
                    found.add(rows.getString(1));
                }
                assertTrue(found.containsAll(EXPECTED_TABLES),
                        "expected Stage 3.1 tables, found " + found);
            }
        }
    }

    private static void assertChecksAndUniqueness(Connection connection) throws SQLException {
        insertUser(connection, "admin@example.test", "Admin", "COLLEGE_ADMINISTRATOR");
        insertUser(connection, "student.one@example.test", "Student One", "STUDENT");
        insertUser(connection, "student.two@example.test", "Student Two", "STUDENT");
        insertUser(connection, "faculty.one@example.test", "Faculty One", "FACULTY_COORDINATOR");
        insertUser(connection, "faculty.two@example.test", "Faculty Two", "FACULTY_COORDINATOR");

        assertSqlState(connection, "23505",
                "INSERT INTO users (identity, display_name, role) "
                        + "VALUES ('student.one@example.test', 'Duplicate', 'STUDENT')");
        assertSqlState(connection, "23514",
                "INSERT INTO users (identity, display_name, role) "
                        + "VALUES ('UPPER@example.test', 'Upper', 'STUDENT')");
        assertSqlState(connection, "23514",
                "INSERT INTO users (identity, display_name, role) "
                        + "VALUES ('mentor@example.test', 'Mentor', 'COMPANY_MENTOR')");
        assertSqlState(connection, "23514",
                "INSERT INTO users (identity, display_name, role) VALUES "
                        + "('long.name@example.test', '" + "x".repeat(201) + "', 'STUDENT')");

        insertCohort(connection, "C-2026-A", "Cohort A");
        insertCohort(connection, "C-2026-B", "Cohort B");
        assertSqlState(connection, "23505",
                "INSERT INTO cohorts (cohort_code, name) VALUES ('C-2026-A', 'Duplicate')");
        assertSqlState(connection, "23514",
                "INSERT INTO cohorts (cohort_code, name, period_start, period_end) "
                        + "VALUES ('C-BAD', 'Bad period', DATE '2026-02-01', DATE '2026-01-01')");
        assertSqlState(connection, "23514",
                "INSERT INTO cohorts (cohort_code, name) VALUES ('C-LONG', '"
                        + "x".repeat(201) + "')");

        insertCompany(connection, "Acme Labs");
        assertSqlState(connection, "23505",
                "INSERT INTO companies (name) VALUES ('acme labs')");
    }

    private static void assertActiveMembershipAndCoordinatorRules(Connection connection)
            throws SQLException {
        long studentOne = userId(connection, "student.one@example.test");
        long cohortA = cohortId(connection, "C-2026-A");
        long cohortB = cohortId(connection, "C-2026-B");
        long facultyOne = userId(connection, "faculty.one@example.test");
        long facultyTwo = userId(connection, "faculty.two@example.test");

        insertMembership(connection, studentOne, cohortA);
        assertSqlState(connection, "23505",
                "INSERT INTO cohort_memberships (student_user_id, cohort_id) VALUES ("
                        + studentOne + ", " + cohortB + ")");

        insertCoordinator(connection, cohortA, facultyOne, true);
        assertSqlState(connection, "23505",
                "INSERT INTO cohort_coordinator_assignments (cohort_id, faculty_user_id, is_primary) "
                        + "VALUES (" + cohortA + ", " + facultyTwo + ", TRUE)");
        assertSqlState(connection, "23505",
                "INSERT INTO cohort_coordinator_assignments (cohort_id, faculty_user_id, is_primary) "
                        + "VALUES (" + cohortA + ", " + facultyOne + ", FALSE)");
        // Additional non-primary coordinators are permitted (FR-COHORT-003).
        insertCoordinator(connection, cohortA, facultyTwo, false);
    }

    private static void assertInternshipRules(Connection connection) throws SQLException {
        long studentOne = userId(connection, "student.one@example.test");
        long cohortA = cohortId(connection, "C-2026-A");
        long company = companyId(connection, "Acme Labs");

        insertInternship(connection, studentOne, cohortA, company,
                "2026-01-01", "2026-06-30");
        assertSqlState(connection, "23505",
                "INSERT INTO internships (student_user_id, cohort_id, start_date, end_date) "
                        + "VALUES (" + studentOne + ", " + cohortA + ", DATE '2026-07-01', DATE '2026-12-31')");
        assertSqlState(connection, "23514",
                "INSERT INTO internships (student_user_id, cohort_id, start_date, end_date) "
                        + "VALUES (" + userId(connection, "student.two@example.test") + ", "
                        + cohortA + ", DATE '2026-06-30', DATE '2026-01-01')");
    }

    private static void assertNoCascadeDeletion(Connection connection) throws SQLException {
        long cohortA = cohortId(connection, "C-2026-A");
        // PostgreSQL 18 reports ON DELETE RESTRICT as 23001 (restrict_violation);
        // earlier versions report the same condition as 23503 (foreign_key_violation).
        assertSqlState(connection, "23001", "23503", "DELETE FROM cohorts WHERE id = " + cohortA);
    }

    private static void insertUser(Connection connection, String identity, String displayName,
            String role) throws SQLException {
        try (var statement = connection.prepareStatement(
                "INSERT INTO users (identity, display_name, role) VALUES (?, ?, ?)")) {
            statement.setString(1, identity);
            statement.setString(2, displayName);
            statement.setString(3, role);
            statement.executeUpdate();
        }
    }

    private static void insertCohort(Connection connection, String code, String name)
            throws SQLException {
        try (var statement = connection.prepareStatement(
                "INSERT INTO cohorts (cohort_code, name) VALUES (?, ?)")) {
            statement.setString(1, code);
            statement.setString(2, name);
            statement.executeUpdate();
        }
    }

    private static void insertCompany(Connection connection, String name) throws SQLException {
        try (var statement = connection.prepareStatement(
                "INSERT INTO companies (name, created_by) "
                        + "VALUES (?, (SELECT id FROM users WHERE role = 'COLLEGE_ADMINISTRATOR' LIMIT 1))")) {
            statement.setString(1, name);
            statement.executeUpdate();
        }
    }

    private static void insertMembership(Connection connection, long studentId, long cohortId)
            throws SQLException {
        try (var statement = connection.prepareStatement(
                "INSERT INTO cohort_memberships (student_user_id, cohort_id) VALUES (?, ?)")) {
            statement.setLong(1, studentId);
            statement.setLong(2, cohortId);
            statement.executeUpdate();
        }
    }

    private static void insertCoordinator(Connection connection, long cohortId, long facultyId,
            boolean primary) throws SQLException {
        try (var statement = connection.prepareStatement(
                "INSERT INTO cohort_coordinator_assignments "
                        + "(cohort_id, faculty_user_id, is_primary) VALUES (?, ?, ?)")) {
            statement.setLong(1, cohortId);
            statement.setLong(2, facultyId);
            statement.setBoolean(3, primary);
            statement.executeUpdate();
        }
    }

    private static void insertInternship(Connection connection, long studentId, long cohortId,
            long companyId, String startDate, String endDate) throws SQLException {
        try (var statement = connection.prepareStatement(
                "INSERT INTO internships "
                        + "(student_user_id, cohort_id, company_id, start_date, end_date, title) "
                        + "VALUES (?, ?, ?, ?::date, ?::date, 'Software Engineering Intern')")) {
            statement.setLong(1, studentId);
            statement.setLong(2, cohortId);
            statement.setLong(3, companyId);
            statement.setString(4, startDate);
            statement.setString(5, endDate);
            statement.executeUpdate();
        }
    }

    private static long userId(Connection connection, String identity) throws SQLException {
        return scalarLong(connection, "SELECT id FROM users WHERE identity = '" + identity + "'");
    }

    private static long cohortId(Connection connection, String code) throws SQLException {
        return scalarLong(connection, "SELECT id FROM cohorts WHERE cohort_code = '" + code + "'");
    }

    private static long companyId(Connection connection, String name) throws SQLException {
        return scalarLong(connection, "SELECT id FROM companies WHERE lower(name) = lower('" + name + "')");
    }

    private static long scalarLong(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement();
                ResultSet rows = statement.executeQuery(sql)) {
            assertTrue(rows.next(), "expected a row for: " + sql);
            return rows.getLong(1);
        }
    }

    private static void assertSqlState(Connection connection, String expectedState, String sql)
            throws SQLException {
        assertSqlState(connection, expectedState, null, sql);
    }

    private static void assertSqlState(Connection connection, String expectedState,
            String alternateState, String sql) throws SQLException {
        SQLException exception = assertThrows(SQLException.class, () -> {
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        }, "expected SQLState " + expectedState + " for: " + sql);
        String actualState = exception.getSQLState();
        boolean accepted = expectedState.equals(actualState)
                || (alternateState != null && alternateState.equals(actualState));
        assertTrue(accepted, "unexpected SQLState for: " + sql
                + " (expected " + expectedState
                + (alternateState == null ? "" : " or " + alternateState)
                + ", got " + actualState + ": " + exception.getMessage() + ")");
    }

    private static String requireEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " must be set for the migration test");
        }
        return value;
    }

    private static void dropTestSchema(String url, String username, String password) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement()) {
            statement.execute("DROP SCHEMA IF EXISTS " + SCHEMA + " CASCADE");
        } catch (SQLException ignored) {
            // The test result already reflects the migration assertions; a cleanup
            // failure is reported by the disposable database operator, not hidden
            // as a product failure.
        }
    }
}
