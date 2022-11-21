package pubsub.notification;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Notifications in the publish-subscribe system take the form of arbitrarily
 * deeply nested JSON objects.
 */
public class Notification implements java.io.Serializable {

  private final String topic;

  private static final ObjectMapper objectMapperInstance = new ObjectMapper();

  private static final ObjectWriter objectWriterInstance = objectMapperInstance.writer();

  private final ObjectNode notificationJson;

  private final String notificationJsonString;

  static {
    objectMapperInstance.disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT);
    // objectMapperInstance.enable(DeserializationFeature.USE_LONG_FOR_INTS);
  }

  public static ObjectMapper getMapper() {
    return objectMapperInstance;
  }

  private Notification(String topic, ObjectNode notification) {
    this.topic = topic;
    this.notificationJson = notification;
    try {
      notificationJsonString = objectWriterInstance.writeValueAsString(notification);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Should not reach here");
    }
  }

  public Notification(String topic, String json) throws JsonProcessingException, ClassCastException {
    this.topic = topic;
    this.notificationJsonString = json;
    this.notificationJson = (ObjectNode) objectMapperInstance.readTree(json);
  }

  public String getTopic() {
    return topic;
  }

  public ObjectNode getNotificationJson() {
    return notificationJson;
  }

  public String notificationJsonString() {
    return notificationJsonString;
  }

  /*
   * Convenience class for building notifications. Each put operation puts a value
   * at a certain path in the notification.
   */
  public static class Builder {

    private static final ObjectMapper mapper = objectMapperInstance;

    private ObjectNode notificationJson = mapper.createObjectNode();

    private Set<String[]> pathsApplied = new HashSet<>();

    private final String topic;

    public Builder(String topic) {
      this.topic = topic;
    }

    public Builder _put(boolean b, String... path) {
      ensurePositivePathLength(path);
      ensurePathNotAlreadyApplied(path);
      _put(BooleanNode.valueOf(b), path);
      return this;
    }

    public Builder _put(int n, String... path) {
      ensurePositivePathLength(path);
      ensurePathNotAlreadyApplied(path);
      _put(IntNode.valueOf(n), path);
      return this;
    }

    public Builder _put(long n, String... path) {
      ensurePositivePathLength(path);
      ensurePathNotAlreadyApplied(path);
      _put(LongNode.valueOf(n), path);
      return this;
    }

    public Builder _put(float x, String... path) {
      ensurePositivePathLength(path);
      ensurePathNotAlreadyApplied(path);
      _put(FloatNode.valueOf(x), path);
      return this;
    }

    public Builder _put(double x, String... path) {
      ensurePositivePathLength(path);
      ensurePathNotAlreadyApplied(path);
      _put(DoubleNode.valueOf(x), path);
      return this;
    }

    public Builder _put(String s, String... path) {
      ensurePositivePathLength(path);
      ensurePathNotAlreadyApplied(path);
      _put(TextNode.valueOf(s), path);
      return this;
    }

    public Builder _put(ObjectNode objectNode, String... path) {
      ensurePositivePathLength(path);
      ensurePathNotAlreadyApplied(path);
      _put(objectNode, path);
      return this;
    }

    public Builder put(JsonNode jsonNode, String... path) {
      ensurePositivePathLength(path);
      ensurePathNotAlreadyApplied(path);
      _put(jsonNode, path);
      return this;
    }

    private void ensurePositivePathLength(String[] path) {
      if (path.length < 1) {
        throw new IllegalArgumentException("Specified path must not be empty");
      }
    }

    private void ensurePathNotAlreadyApplied(String[] path) {
      if (!pathsApplied.contains(path)) {
        pathsApplied.add(path);
      } else {
        throw new IllegalArgumentException("Can only apply a put operation at a specified path only once.");
      }
    }

    private void _put(JsonNode jsonNode, String[] path) {
      ObjectNode curr = notificationJson;
      int i;
      for (i = 0; i < path.length - 1; i++) {
        String pathCurrPos = path[i];
        if (!curr.has(pathCurrPos)) {
          curr.set(pathCurrPos, mapper.createObjectNode());
        }
        curr = (ObjectNode) curr.get(pathCurrPos);
      }
      curr.set(path[i], jsonNode);
    }

    public Notification build() {
      long timestamp = System.currentTimeMillis();
      _put(timestamp, "creationTimestamp");
      return new Notification(topic, notificationJson);
    }

  }
}
