package backend.stomp.repairs;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class RepairStompProperties {
  // 아래 fieldKeys는 해당 도메인 필드명과 일치해야함.
  protected String[] fieldKeys = {"busType", "busNumber"};
  // 실제 DB에 저장되기 위해 contents 안에 포함되어야하는 메시지
  protected String payloadText = "수리";
  // Stomp 클라이언트 채널명
  protected String destination = "repairs";
  // 수리 히스토리에 저장되는 내용에 해당하는 Json Key 값 (해당 Key값을 가진 Value값 내용이 State에 저장)
  protected String messageKey = "contents";
  // 수리 완료 후 callback 할 메시지 Key값
  protected String receivedMessageKey = "Received Message";
  // 수리 완료 후 callback 할 메시지 Value값
  protected String completedMessage = "수리를 완료했어요";
}
