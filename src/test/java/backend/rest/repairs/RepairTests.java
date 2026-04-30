package backend.rest.repairs;

import static io.u2ware.common.docs.MockMvcRestDocs.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;

import backend.oauth2.Oauth2Docs;
import backend.rest.buses.BusDocs;
import io.u2ware.common.stomp.client.WebsocketStompClient;
import io.u2ware.common.stomp.client.config.WebsocketStompProperties;
import io.u2ware.common.stomp.client.handlers.StompJsonFrameHandler;

@SpringBootTest
@AutoConfigureMockMvc
public class RepairTests {

    protected Log logger = LogFactory.getLog(getClass());

    @Autowired
    private MockMvc mvc;

    @Autowired
    private Oauth2Docs od;

    @Autowired
    private BusDocs bd;

    @Autowired
    private WebsocketStompProperties properties;

    @Autowired
    private RepairDocs rd;

    @Test
    public void contextLoads() throws Exception {

        // 사전 설정
        CompletableFuture<Void> completed = new CompletableFuture<>();

        Jwt u = od.jose("adminUserForToken", "ROLE_ADMIN");

        Set<Object> busTypeValue = new HashSet<>();
        busTypeValue.add("간선");
        busTypeValue.add("지선");

        Map<String, Object> req = new HashMap<>();
        req.put("title", "entity1");
        req.put("busNumber", 11400);
        req.put("busType", busTypeValue);

        mvc.perform(post("/rest/buses").content(req).auth(u))
            .andExpect(is2xx())
            .andDo(result(bd::context, "entity1"));

        String uri = bd.context("entity1", "$._links.self.href");

        // Stomp 메시지 테스트
        rd.awaitSubscriberReady();
        WebsocketStompClient.withSockJS().connect(properties.getUrl()).whenComplete((c, e) -> {
            if (e != null) {
                completed.completeExceptionally(e);
                return;
            }
            c.subscribe("/topic/repairs", new StompJsonFrameHandler() {
                @Override
                public void handleFrame(StompHeaders headers, JsonNode payload) {
                    if (rd.isReceived(payload)) {
                        completed.complete(null);
                    }
                }
            }).whenComplete((s, se) -> {
                if (se != null) {
                    completed.completeExceptionally(se);
                    return;
                }
                c.send("/app/repairs", Map.of(
                    "busNumber", 11400,
                    "contents", "11400테스트 버스 수리완료"
                )).whenComplete((r, sendErr) -> {
                    if (sendErr != null) {
                        completed.completeExceptionally(sendErr);
                    }
                });
            });
        });
        completed.get(10, TimeUnit.SECONDS);

        mvc.perform(post(uri).auth(u)).andDo(print()).andExpect(is2xx()).andDo(result(bd::context, "entity2"));
        String checkMessage = bd.context("entity2", "$.busRepairHistory[0].busRepairState");
        assertTrue(checkMessage.equals("11400테스트 버스 수리완료"));
        
    }
}
