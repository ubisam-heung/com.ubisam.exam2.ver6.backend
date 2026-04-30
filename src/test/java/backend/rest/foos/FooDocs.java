package backend.rest.foos;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.u2ware.common.docs.MockMvcRestDocs;

@Component
public class FooDocs extends MockMvcRestDocs {

    public Map<String,Object> newEntity(String id){

        Map<String,Object> r = new HashMap<>();
        r.put("id", id);
        r.put("name", super.randomText("Foo-"));
        r.put("age", super.randomInt());
        return r;
    }

    public Map<String,Object> resetEntity(Map<String,Object> r){

        r.put("name", super.randomText("Foo2-"));
        r.put("age", super.randomInt());
        return r;
    }
}
