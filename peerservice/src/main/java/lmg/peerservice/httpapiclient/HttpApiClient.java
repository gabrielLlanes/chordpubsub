package lmg.peerservice.httpapiclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lmg.peerservice.repository.PeerServiceDBRepository;
import lmg.peerservice.service.PeerService;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import pubsub.notification.Notification;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

@Service
public class HttpApiClient {

    private final PeerService peerService;

    private final PeerServiceDBRepository peerServiceDBRepository;

    private final String notificationName = System.getenv("NOTIFICATION_NAME");


    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final HttpRequest httpRequest;

    private final String httpRequestString;

    private final ObjectMapper objectMapper = new ObjectMapper();


    public HttpApiClient(PeerService peerService, PeerServiceDBRepository peerServiceDBRepository, TaskScheduler taskScheduler) throws IOException, URISyntaxException {
        this.peerService = peerService;
        this.peerServiceDBRepository = peerServiceDBRepository;
        String httpRequestStringFileLocation = System.getenv("HTTP_REQUEST_FILE_LOCATION");
        long taskPeriod = Long.parseLong(System.getenv("HTTP_REQUEST_PERIOD"));
        if(httpRequestStringFileLocation != null) {
            httpRequestString = new String(Files.readAllBytes(Path.of(httpRequestStringFileLocation)));
        } else {
            httpRequestString = null;
        }
        httpRequest = HttpRequest.newBuilder(new URI(httpRequestString))
                .GET()
                .build();
        taskScheduler.scheduleWithFixedDelay(this::request, Duration.ofMillis(taskPeriod));
    }

    private void request() {
        if(httpRequestString == null) {
            return;
        }
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                System.out.println(String.format("Received 200 response with body:\n%s\n", response.body()));
                Notification notification = new Notification.Builder(notificationName)
                        ._put(notificationName, "notificationName")
                        .put(objectMapper.readTree(response.body()), "data")
                        .build();
                if(peerService != null) {
                    peerService.accept(notification);
                }
            }
        } catch(IOException | InterruptedException e) {
        }
    }
}
