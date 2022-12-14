package lmg.peerservice.controller;

import java.util.List;

import lmg.peerservice.service.PeerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pubsub.notification.Notification;

@RestController
public class ServiceRestController {

  private final PeerService peerService;

  public ServiceRestController(PeerService peerService) {
    this.peerService = peerService;
  }

  @GetMapping("/peer/recent")
  public List<String> mostRecentNotifications() {
    return peerService.mostRecentNotifications();
  }

  @PostMapping("/peer/subscription/topic")
  public String subscribe(@RequestParam("topic") String topic) {
    boolean success = peerService.topicSubscription(topic);
    if(success) {
      return "Topic subscription processed successfully";
    }
    return "Topic subscription failed.";
  }



}
