package lmg.peerservice.httpapiclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class HttpApiClient implements Runnable {

    @Autowired
    private Environment env;

    @Value("${http.request.period}")
    private String _fixedPeriod;

    private int fixedPeriod;

    private String httpRequestString;

    @Value("${notification.name}")
    private String notificationName;

    private final ScheduledExecutorService httpRequestThread = Executors.newSingleThreadScheduledExecutor();

    public HttpApiClient() {
        System.out.println(env);
        System.out.println(_fixedPeriod);
        System.out.println(httpRequestString);
        System.out.println(notificationName);
        try {
            httpRequestString = System.getenv("HTTP_REQUEST_STRING");
            fixedPeriod = Integer.parseInt(_fixedPeriod);
            notificationName = System.getenv("NOTIFICATION_NAME");
        } catch(Exception e) {
            System.exit(1);
        }
    }

    @Override
    public void run() {
        //httpRequestThread.scheduleWithFixedDelay(()->)
    }
}
