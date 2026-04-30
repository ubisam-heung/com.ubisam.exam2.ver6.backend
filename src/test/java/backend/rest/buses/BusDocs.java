package backend.rest.buses;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import backend.domain.Bus;
import io.u2ware.common.docs.MockMvcRestDocs;

@Component
public class BusDocs extends MockMvcRestDocs{

  public Bus newEntity(String... entity){
    Bus body = new Bus();
    body.setBusNumber(entity.length > 0 ? Integer.parseInt(entity[0]) : super.randomInt());
    return body;
  }

  public Map<String, Object> setSearch(String keyword, String option){
    Map<String, Object> body = new HashMap<>();
    body.put("keyword", keyword);
    body.put("option", option);
    return body;
  }
  
}
