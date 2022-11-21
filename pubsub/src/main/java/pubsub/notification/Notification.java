package pubsub.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Notifications in the publish-subscribe system will take the form of JSON
 * objects.
 */
public class Notification {

  private final ObjectNode notification;

  public Notification(ObjectNode notification) {
    this.notification = notification;
  }

  public Notification(String json) throws JsonProcessingException {
    this.notification = (ObjectNode) new ObjectMapper().readTree(json);
  }

  public ObjectNode getNotificationJson() {
    return notification;
  }
}
