package lmg.peerservice.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Repository;
import pubsub.notification.Notification;
import pubsubschord.nodeproxy.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Repository
public class PeerServiceDBRepository {

    private final JdbcTemplate jdbcTemplate;

    private final TaskScheduler taskScheduler;

    private final String tableName;

    private Node node;

    public PeerServiceDBRepository(JdbcTemplate jdbcTemplate, TaskScheduler taskScheduler) throws IOException {
        try {
            node = Node.getInstance();
        } catch (RemoteException e) {
            throw new RuntimeException("Initialization of node failed.", e);
        }
        this.jdbcTemplate = jdbcTemplate;
        this.taskScheduler = taskScheduler;
        String dbTableFileLocation = System.getenv("DB_TABLE_FILE_LOCATION");
        if(dbTableFileLocation == null) {
            throw new IllegalStateException("Database table information was not provided.");
        }
        try {
            tableName = new String(Files.readAllBytes(Path.of(dbTableFileLocation)));
        } catch (IOException e) {
            throw new IOException("Failed to read database table information", e);
        }
        new Thread(this::storing).start();
    }

    private void storing() {
        BlockingQueue<Notification> queue = node.getNotificationQueue();
        while(true) {
            try {
                Notification notification = queue.poll(1, TimeUnit.SECONDS);
                if(notification == null) {
                    continue;
                }
                String storeNotificationSql = String.format("insert into %s(time_received, topic, data) values (?, ?, cast(? as json));", tableName);
                try {
                    LocalDateTime time = LocalDateTime.now();
                    System.out.println(time);
                    System.out.println(notification.getNotificationJson().get("notificationName").asText());
                    System.out.println(notification.notificationJsonString());
                    jdbcTemplate.update(storeNotificationSql,
                            time,
                            notification.getNotificationJson().get("notificationName").asText(),
                            notification.notificationJsonString());
                    System.out.println(String.format("Updated table at time %s with data: %s\n",
                            time, notification.notificationJsonString()));
                } catch (DataAccessException e) {
                    System.err.println(String.format("Error updating table: %s\n", e));
                    System.err.println(Arrays.toString(e.getStackTrace()));
                }
            } catch (InterruptedException e) {
            } catch (Exception e) {
                System.err.println(String.format("Generic exception occurred: %s", e));
            }
        }
    }
}
