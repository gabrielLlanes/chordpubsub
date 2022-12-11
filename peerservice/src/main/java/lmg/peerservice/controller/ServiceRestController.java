package lmg.peerservice.controller;

import java.util.List;

import lmg.peerservice.service.PeerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pubsub.notification.Notification;
import pubsubschord.app.Node;

@RestController
public class ServiceRestController {

  private final PeerService peerService;

  public ServiceRestController(PeerService peerService) {
    this.peerService = peerService;
  }

  @GetMapping("/peer/recent")
  public List<Notification> mostRecentNotifications() {
    return null;
  }

}
