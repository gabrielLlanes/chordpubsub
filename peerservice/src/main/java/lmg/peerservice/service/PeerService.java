package lmg.peerservice.service;

import org.springframework.stereotype.Service;
import pubsub.notification.Notification;
import pubsub.subscription.SingleSubscription;
import pubsub.subscription.Subscription;
import pubsub.subscription.predicate.string.equality.StringEqualityPredicate;
import pubsubschord.app.Node;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class PeerService {

    private static final int TOP = 5;

    private Node node;

    public PeerService() {
        try {
            node = Node.getInstance();
        } catch (RemoteException e) {
            System.exit(1);
        }
    }

    public void accept(Notification notification) {
        node.publish(notification);
    }

    public List<Notification> mostRecentNotifications() {
        ConcurrentLinkedQueue<Notification> queue = node.getNotificatonQueue();
        List<Notification> l = new ArrayList<>();
        for(int i = 0; i < TOP && !queue.isEmpty(); i++) {
            l.add(queue.poll());
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
