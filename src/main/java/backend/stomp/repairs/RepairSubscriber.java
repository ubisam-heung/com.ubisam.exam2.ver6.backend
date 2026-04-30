package backend.stomp.repairs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import backend.domain.Bus;
import backend.domain.Repair;
import backend.domain.properties.JsonNodeValueExtractor;
import backend.rest.buses.BusRepository;
import backend.rest.repairs.RepairRepository;
import io.u2ware.common.stomp.client.WebsocketStompClient;
import io.u2ware.common.stomp.client.WebsocketStompClientHandler;
import io.u2ware.common.stomp.client.config.WebsocketStompProperties;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import backend.domain.properties.EntityFieldValueFinder;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import org.springframework.transaction.annotation.Transactional;

@Component
public class RepairSubscriber implements WebsocketStompClientHandler {

    private static final Log logger = LogFactory.getLog(RepairSubscriber.class);

    @Autowired
    private BusRepository busRepository;

    private EntityFieldValueFinder<Bus, UUID> busFinder;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RepairRepository nodeRepository;

    @Autowired
    private RepairStompProperties repairStompProperties;

    @Autowired
    private WebsocketStompProperties websocketStompProperties;

    @Override
    public String getDestination() {
        return websocketStompProperties.getSubscriptions().get(repairStompProperties.getDestination());
    }

    @Override
    @Transactional
    public void handleFrame(WebsocketStompClient client, JsonNode message) {
        logger.info("Received: " + message);

        JsonNode payloadNode = message.path("payload");
        if (payloadNode.isMissingNode() || payloadNode.isNull()) {
            return;
        }
        String receivedMessageKey = repairStompProperties.getReceivedMessageKey();
        if (receivedMessageKey != null && payloadNode.has(receivedMessageKey)) {
            return;
        }

        String payloadFilter = repairStompProperties.getPayloadText();
        String payloadText = payloadNode.toString();

        if (payloadFilter != null && !payloadFilter.trim().isEmpty()
                && !payloadNode.has(payloadFilter)
                && !payloadText.contains(payloadFilter)) {
            return;
        }
        Long timestamp = message.path("timestamp").asLong();
        String principal = message.path("principal").asText();

        Map<String, Object> configuredFieldValues = JsonNodeValueExtractor.extractConfiguredValues(
            payloadNode,
            Arrays.asList(repairStompProperties.getFieldKeys())
        );

        Repair e = new Repair();
        e.setPrincipal(principal);
        e.setTimestamp(timestamp);
        e.setState(payloadText);
        nodeRepository.save(e);

        String repairMessage = null;
        String messageKey = repairStompProperties.getMessageKey();
        if (messageKey != null && payloadNode.hasNonNull(messageKey)) {
            repairMessage = payloadNode.get(messageKey).asText();
        } else {
            return;
        }

        if (busFinder == null) {
            busFinder = new EntityFieldValueFinder<>(
                busRepository,
                repairStompProperties.getFieldKeys()
            );
        }
        List<Bus> targetBuses = busFinder.find(configuredFieldValues);
        if (targetBuses.isEmpty()) {
            return;
        }

        for (Bus bus : targetBuses) {
            if (bus.getBusRepairHistory() == null) {
                bus.setBusRepairHistory(new HashSet<>());
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formatted = sdf.format(new Date(timestamp));
            logger.info("timestamp: " + timestamp);
            logger.info("formatted: " + formatted);
            bus.getBusRepairHistory().add(
                Bus.BusRepairHistory.builder()
                    .busRepairName(principal)
                    .busRepairState(repairMessage)
                    .busRepairTimestamp(formatted)
                    .build()
            );
        }
        busRepository.saveAll(targetBuses);

        ObjectNode send = mapper.createObjectNode();
        if (receivedMessageKey != null) {
            send.put(receivedMessageKey, repairStompProperties.getCompletedMessage());
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    client.send("/app/" + repairStompProperties.getDestination(), send);
                }
            });
        } else {
            client.send("/app/" + repairStompProperties.getDestination(), send);
        }
    }
}