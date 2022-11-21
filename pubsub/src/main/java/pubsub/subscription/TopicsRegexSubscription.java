package pubsub.subscription;

import java.util.HashSet;
import java.util.Set;

import pubsub.notification.Notification;

public class TopicsRegexSubscription extends Subscription {

    private final Set<String> topicsRegex;

    public TopicsRegexSubscription(Set<String> topicsRegex) {
        this.topicsRegex = topicsRegex;
    }

    public TopicsRegexSubscription(Subscription subscription) {
        if(subscription instanceof TopicsRegexSubscription) {
            this.topicsRegex = ((TopicsRegexSubscription)subscription).getTopicsRegex();
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    public TopicsRegexSubscription() {
        this.topicsRegex = new HashSet<>();
    }

    public Set<String> getTopicsRegex() {
        return topicsRegex;
    }

    @Override
    public boolean matches(Notification notification) {
        String topic = notification.getTopic();
        for (String topicRegex : topicsRegex) {
            if (topic.matches(topicRegex)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void merge(Subscription subscription) {
        if(subscription instanceof TopicsRegexSubscription) {
            topicsRegex.addAll(((TopicsRegexSubscription) subscription).getTopicsRegex());
        }
    }
}
