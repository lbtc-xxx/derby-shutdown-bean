package org.nailedtothex.derby;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DerbyShutdownSpringBean implements DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DerbyShutdownSpringBean.class);
    private static final String URL_FOR_SHUTDOWN = "jdbc:derby:;shutdown=true";
    private static final String SQL_STATE_INDICATES_SUCCESSFUL_SHUTDOWN = "XJ015";

    public DerbyShutdownSpringBean() {
        log.info("DerbyShutdownSpringBean instantiated.");
    }

    @Override
    public void destroy() throws Exception {
        log.debug("Shutdown Derby...");
        shutdown();
    }

    private void shutdown() {
        Connection cn = null;
        try {
            cn = DriverManager.getConnection(URL_FOR_SHUTDOWN);
            log.error("Derby shutdown failed (no exception occurred).");
        } catch (SQLException e) {
            if (SQL_STATE_INDICATES_SUCCESSFUL_SHUTDOWN.equals(e.getSQLState())) {
                log.info("Derby shutdown succeeded. SQLState={}, message={}", e.getSQLState(), e.getMessage());
                log.debug("Derby shutdown exception", e);
                return;
            }
            log.error("Derby shutdown failed", e);
        } finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (Exception e) {
                    log.warn("Database closing error", e);
                }
            }
        }
    }

}
