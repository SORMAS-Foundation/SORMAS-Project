package de.symeda.sormas.backend.person.notifier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.Query;

import org.h2.tools.TriggerAdapter;

import de.symeda.sormas.backend.AbstractBeanTest;

public abstract class NotifierTestBase extends AbstractBeanTest {
    
    public void init() {
        super.init();
        executeInTransaction(em -> {
            Query nativeQuery = em.createNativeQuery(
                "CREATE TABLE notifier_history (" + "id bigint GENERATED ALWAYS AS IDENTITY, " + "uuid varchar(36) not null, "
                    + "changedate timestamp not null, " + "creationdate timestamp not null, " + "change_user_id bigint, "
                    + "registrationnumber varchar(255), " + "firstname varchar(255) not null, " + "lastname varchar(255) not null, "
                    + "address text, " + "email varchar(255), " + "phone varchar(255), " + "agentfirstname varchar(255), "
                        + "agentlastname varchar(255), "+"sys_period varchar(255))");
            nativeQuery.executeUpdate();

            nativeQuery = em.createNativeQuery(
                "CREATE TRIGGER IF NOT EXISTS notifier_insert_history AFTER INSERT ON notifier FOR EACH ROW "
                    + "CALL \"de.symeda.sormas.backend.person.notifier.NotifierTestBase$NotifierHistoryCopy\"");
            nativeQuery.executeUpdate();

            nativeQuery = em.createNativeQuery(
                "CREATE TRIGGER IF NOT EXISTS notifier_update_history AFTER UPDATE ON notifier FOR EACH ROW "
                    + "CALL \"de.symeda.sormas.backend.person.notifier.NotifierTestBase$NotifierHistoryCopy\"");
            nativeQuery.executeUpdate();
        });
    }

    public static class NotifierHistoryCopy extends TriggerAdapter {

        @Override
        public void fire(Connection conn, ResultSet oldRow, ResultSet newRow) throws SQLException {
            String sql = "INSERT INTO notifier_history ("
                + "uuid, changedate, creationdate, change_user_id, registrationnumber, firstname, lastname, address, email, phone "
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            String uuid = newRow.getString("uuid");
            java.sql.Timestamp changeDate = newRow.getTimestamp("changedate");
            java.sql.Timestamp creationDate = newRow.getTimestamp("creationdate");
            int changeUserId = 0; // Default value
            String registrationNumber = newRow.getString("registrationnumber");
            String firstName = newRow.getString("firstname");
            String lastName = newRow.getString("lastname");
            String address = newRow.getString("address");
            String email = newRow.getString("email");
            String phone = newRow.getString("phone");

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, uuid);
                stmt.setTimestamp(2, changeDate);
                stmt.setTimestamp(3, creationDate);
                stmt.setInt(4, changeUserId);
                stmt.setString(5, registrationNumber);
                stmt.setString(6, firstName);
                stmt.setString(7, lastName);
                stmt.setString(8, address);
                stmt.setString(9, email);
                stmt.setString(10, phone);

                stmt.executeUpdate();
            }
        }
    }
}
