package pubsubschord.nodeproxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.jupiter.api.Test;

import chord.node.consistentfinger.ConsistentFingerPubSubChordNodeImpl;
import pubsub.notification.Notification;
import pubsub.subscription.Subscription;
import pubsub.subscription.TopicsRegexSubscription;

public class NodeOnlyIntegrationTest {

  ConsistentFingerPubSubChordNodeImpl n1;
  ConsistentFingerPubSubChordNodeImpl n2;
  ConsistentFingerPubSubChordNodeImpl n3;
  ConsistentFingerPubSubChordNodeImpl n4;
  ConsistentFingerPubSubChordNodeImpl n5;
  ConsistentFingerPubSubChordNodeImpl n6;
  ConsistentFingerPubSubChordNodeImpl n7;
  ConsistentFingerPubSubChordNodeImpl n8;

  @Test
  public void integrationTestEightNodes() throws Exception {

    ExecutorService pool = Executors.newFixedThreadPool(32);

    ReentrantLock lock = new ReentrantLock();

    Condition n1JoinedCondition = lock.newCondition();
    Condition n2JoinedCondition = lock.newCondition();
    Condition n3JoinedCondition = lock.newCondition();
    Condition n4JoinedCondition = lock.newCondition();
    Condition n5JoinedCondition = lock.newCondition();
    Condition n6JoinedCondition = lock.newCondition();
    Condition n7JoinedCondition = lock.newCondition();
    Condition n8JoinedCondition = lock.newCondition();
    Condition allNodesJoinedCondition = lock.newCondition();

    AtomicBoolean n1Joined = new AtomicBoolean(false);
    AtomicBoolean n2Joined = new AtomicBoolean(false);
    AtomicBoolean n3Joined = new AtomicBoolean(false);
    AtomicBoolean n4Joined = new AtomicBoolean(false);
    AtomicBoolean n5Joined = new AtomicBoolean(false);
    AtomicBoolean n6Joined = new AtomicBoolean(false);
    AtomicBoolean n7Joined = new AtomicBoolean(false);
    AtomicBoolean n8Joined = new AtomicBoolean(false);

    AtomicInteger nodesJoined = new AtomicInteger(0);

    Set<String> topics = new HashSet<>();
    topics.add("testtopic");

    Subscription subscription = new TopicsRegexSubscription(topics);

    pool.execute(() -> {
      try {
        n1 = new ConsistentFingerPubSubChordNodeImpl(10, 3, true);
        lock.lock();
        n1Joined.set(true);
        nodesJoined.incrementAndGet();
        n1JoinedCondition.signalAll();
        lock.unlock();
      } catch(Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    });
    pool.execute(() -> {
      try {
        n2 = new ConsistentFingerPubSubChordNodeImpl(10, 47, false);
        lock.lock();
        while (!n1Joined.get()) {
          n1JoinedCondition.await();
        }
        n2.join(n1);
        n2.subscribe(subscription);
        n2Joined.set(true);
        nodesJoined.incrementAndGet();
        n2JoinedCondition.signalAll();
        lock.unlock();
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    });
    pool.execute(() -> {
      try {
        n3 = new ConsistentFingerPubSubChordNodeImpl(10, 98, false);
        lock.lock();
        while (!n2Joined.get()) {
          n2JoinedCondition.await();
        }
        n3.join(n2);
        n3.subscribe(subscription);
        n3Joined.set(true);
        nodesJoined.incrementAndGet();
        n3JoinedCondition.signalAll();
        lock.unlock();
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    });
    pool.execute(() -> {
      try {
        n4 = new ConsistentFingerPubSubChordNodeImpl(10, 298, false);
        lock.lock();
        while (!n1Joined.get()) {
          n1JoinedCondition.await();
        }
        n4.join(n1);
        n4.subscribe(subscription);
        n4Joined.set(true);
        nodesJoined.incrementAndGet();
        n4JoinedCondition.signalAll();
        lock.unlock();
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    });
    pool.execute(() -> {
      try {
        n5 = new ConsistentFingerPubSubChordNodeImpl(10, 343, false);
        lock.lock();
        while (!n3Joined.get()) {
          n3JoinedCondition.await();
        }
        n5.join(n3);
        n5.subscribe(subscription);
        n5Joined.set(true);
        nodesJoined.incrementAndGet();
        n5JoinedCondition.signalAll();
        lock.unlock();
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    });
    pool.execute(() -> {
      try {
        n6 = new ConsistentFingerPubSubChordNodeImpl(10, 467, false);
        lock.lock();
        while (!n4Joined.get()) {
          n4JoinedCondition.await();
        }
        n6.join(n4);
        n6.subscribe(subscription);
        n6Joined.set(true);
        n6JoinedCondition.signalAll();
        if (nodesJoined.incrementAndGet() == 8) {
          allNodesJoinedCondition.signal();
        }
        lock.unlock();
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    });
    pool.execute(() -> {
      try {
        n7 = new ConsistentFingerPubSubChordNodeImpl(10, 557, false);
        lock.lock();
        while (!n5Joined.get()) {
          n5JoinedCondition.await();
        }
        n7.join(n5);
        n7.subscribe(subscription);
        n7Joined.set(true);
        nodesJoined.incrementAndGet();
        n7JoinedCondition.signalAll();
        if (nodesJoined.incrementAndGet() == 8) {
          allNodesJoinedCondition.signal();
        }
        lock.unlock();
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    });
    pool.execute(() -> {
      try {
        n8 = new ConsistentFingerPubSubChordNodeImpl(10, 876, false);
        lock.lock();
        while (!n5Joined.get()) {
          n5JoinedCondition.await();
        }
        n8.join(n5);
        n8.subscribe(subscription);
        n8Joined.set(true);
        nodesJoined.incrementAndGet();
        n8JoinedCondition.signalAll();
        if (nodesJoined.incrementAndGet() == 8) {
          allNodesJoinedCondition.signal();
        }
        lock.unlock();
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    });

    Thread.sleep(1000);

    Notification notification = new Notification.Builder("testtopic")
        ._put("fdsasecondfdsa", "a", "aa")
        ._put(-499, "b", "ba", "baa")
        ._put(false, "c", "cb", "us")
        .build();

    lock.lock();
    while (nodesJoined.get() < 8) {
      try {
        allNodesJoinedCondition.await();
      } catch (InterruptedException e) {
      }
    }
    lock.unlock();

    ReentrantLock publish = new ReentrantLock();
    Condition allPublishedCondition = publish.newCondition();
    AtomicInteger publishedCount = new AtomicInteger(0);

    ConsistentFingerPubSubChordNodeImpl[] nodes = {
        n1, n2, n3, n4, n5, n6, n7, n8
    };

    int multiplier = 100;

    int notificationsToPublish = 8 * multiplier;

    for (AtomicInteger i = new AtomicInteger(0); i.get() < nodes.length; i.incrementAndGet()) {
      int nodeIndex = i.get();
      for (int j = 0; j < notificationsToPublish / 8; j++) {
        pool.execute(() -> {
          try {
            nodes[nodeIndex].publish(notification);
            publish.lock();
            int instantPublishedCount = publishedCount.incrementAndGet();
            if (instantPublishedCount == notificationsToPublish) {
              allPublishedCondition.signal();
            }
            publish.unlock();
          } catch (RemoteException e) {
            fail();
            e.printStackTrace();
            System.exit(1);
          }
        });
      }
    }

    publish.lock();
    while (publishedCount.get() != notificationsToPublish) {
      try {
        allPublishedCondition.await();
      } catch (InterruptedException e) {
      }
    }
    publish.unlock();

    LinkedBlockingQueue<Notification> q2 = n2.getNotificationQueue();
    LinkedBlockingQueue<Notification> q3 = n3.getNotificationQueue();
    LinkedBlockingQueue<Notification> q4 = n4.getNotificationQueue();
    LinkedBlockingQueue<Notification> q5 = n5.getNotificationQueue();
    LinkedBlockingQueue<Notification> q6 = n6.getNotificationQueue();
    LinkedBlockingQueue<Notification> q7 = n7.getNotificationQueue();
    LinkedBlockingQueue<Notification> q8 = n8.getNotificationQueue();

    assertEquals(7 * multiplier, q2.size());
    assertEquals(7 * multiplier, q3.size());
    assertEquals(7 * multiplier, q4.size());
    assertEquals(7 * multiplier, q5.size());
    assertEquals(7 * multiplier, q6.size());
    assertEquals(7 * multiplier, q7.size());
    assertEquals(7 * multiplier, q8.size());
  }
}
