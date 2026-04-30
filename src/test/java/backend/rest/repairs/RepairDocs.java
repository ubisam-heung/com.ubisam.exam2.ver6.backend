package backend.rest.repairs;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import io.u2ware.common.docs.MockMvcRestDocs;
import io.u2ware.common.stomp.client.WebsocketStompClient;

@Component
public class RepairDocs extends MockMvcRestDocs {

    @Autowired
    private WebsocketStompClient wsc;

    public boolean isReceived(JsonNode payload) {
        if (payload == null) {
            return false;
        }
        JsonNode value = payload.path("Received Message");
        if (value.isMissingNode()) {
            value = payload.path("payload").path("Received Message");
        }
        return !value.isMissingNode() && "수리를 완료했어요".equals(value.asText(""));
    }

    public void awaitSubscriberReady() throws InterruptedException {
        long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10);
        while (System.currentTimeMillis() < deadline) {
            if (wsc.isConnected()) {
                return;
            }
            Thread.sleep(100);
        }
        throw new IllegalStateException("Stomp 연결이 안됐어요");
    }
}
