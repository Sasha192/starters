package org.wpstarters.jwtauthprovider.service.impl;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ScheduledNonceCleanupRunnable implements Runnable {

    private static final String COUNT_NONCES_QUERY =
            "SELECT COUNT(nrt.id) FROM nonce_request_token WHERE nrt.expired < ?";

    private static final String DELETE_QUERY_WITH_LIMIT_AND_EXPIRED =
            "DELETE FROM nonce_request_token nrt WHERE (SELECT * FROM nonce_request_token WHERE nrt.expired < ? LIMIT ? ORDER BY nrt.expired ASC)";

    private static final long TIME_LIMIT_IN_MS = 1_000 * 30;
    private static final int DELETE_LIMIT_NONCE = 50;

    private final DataSource dataSource;

    public ScheduledNonceCleanupRunnable(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run() {

        long threshold = System.currentTimeMillis() - 2 * ScheduledNonceCleanupConfiguration.NONCE_TTL;

        try (Connection connection = dataSource.getConnection()) {

            int expiredNonceRemaining = executeCountWithInstant(connection, COUNT_NONCES_QUERY, threshold);;
            long start = System.currentTimeMillis();

            connection.setAutoCommit(false);

            if (expiredNonceRemaining <= 50) {

                return;

            }

            int deletedNonces;

            do {

                deletedNonces = executeUpdateWithInstantAndLimit(connection, DELETE_QUERY_WITH_LIMIT_AND_EXPIRED, threshold, DELETE_LIMIT_NONCE);

            } while (!timeIsUp(start, TIME_LIMIT_IN_MS) && deletedNonces != 0);

        } catch (Exception e) {
            //
        }

    }

    private static int executeUpdateWithInstantAndLimit(Connection con, String query, long nonceInstant, int nonceLimit)
            throws SQLException {
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setLong(1, nonceInstant);
            st.setInt(2, nonceLimit);
            return st.executeUpdate();
        }
    }

    private static int executeCountWithInstant(Connection con, String query, long nonceInstant)
            throws SQLException {
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setLong(1, nonceInstant);
            try (ResultSet set = st.executeQuery()) {
                if (set.next()) {
                    return set.getInt(1);
                } else {
                    return 0;
                }
            }
        }
    }

    private boolean timeIsUp(long start, long maxExecutionTimeInMs) {
        return (System.currentTimeMillis() - start) > maxExecutionTimeInMs;
    }
}
