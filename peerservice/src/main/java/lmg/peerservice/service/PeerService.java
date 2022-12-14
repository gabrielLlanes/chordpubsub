package lmg.peerservice.service;

import org.springframework.stereotype.Service;
import pubsub.notification.Notification;
import pubsub.subscription.SingleSubscription;
import pubsub.subscription.Subscription;
import pubsub.subscription.predicate.string.equality.StringEqualityPredicate;
import pubsubschord.nodeproxy.Node;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class PeerService {

    private static final int TOP = 5;

    private Node node;

    public PeerService() {
        try {
            node = Node.getInstance();
        } catch (RemoteException e) {
            throw new RuntimeException("Initialization of node failed.", e);
        }
    }

    public void accept(Notification notification) {
        node.publish(notification);
    }

    public List<String> mostRecentNotifications() {
        LinkedBlockingQueue<Notification> queue = node.getNotificationQueue();
        List<String> l = new ArrayList<>();
        for(int i = 0; i < TOP && !queue.isEmpty(); i++) {
            l.add(queue.poll().notificationJsonString());
        }
        return l;
    }

    public boolean topicSubscription(String topic) {
        Subscription subscription = new SingleSubscription.Builder()
                .withPredicate(new StringEqualityPredicate(topic), "notificationName")
                .build();
        Future<Boolean> success = node.subscribe(subscription);
        try {
            return success.get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

}
