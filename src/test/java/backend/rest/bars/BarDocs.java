package backend.rest.bars;

import io.u2ware.common.docs.MockMvcRestDocs;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class BarDocs extends MockMvcRestDocs {

    public Map<String,Object> newEntity(){

        Map<String,Object> r = new HashMap<>();
        r.put("name", super.randomText("Bar-"));
        r.put("age", super.randomInt());
        return r;
    }


    public Map<String,Object> resetEntity(Map<String,Object> r){

        r.put("name", super.randomText("Bar2-"));
        r.put("age", super.randomInt());
        return r;
    }

}
