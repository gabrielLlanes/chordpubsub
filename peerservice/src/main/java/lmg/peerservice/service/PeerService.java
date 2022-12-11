package lmg.peerservice.service;

import org.springframework.data.relational.core.sql.Not;
import org.springframework.stereotype.Service;
import pubsub.notification.Notification;
import pubsubschord.app.Node;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class PeerService {

    private static final int TOP = 5;

    private Node node;

    public PeerService() {
//        try {
//            node = Node.getInstance();
//        } catch (RemoteException e) {
//            System.exit(1);
//        }
    }

    public List<Notification> mostRecentNotifications() {
        ConcurrentLinkedQueue<Notification> queue = node.getNotificatonQueue();
        List<Notification> l = new ArrayList<>();
        for(int i = 0; i < TOP; i++) {
            if(!queue.isEmpty()) {
                l.add(queue.poll());
            }
        }
        return l;
    }
}
