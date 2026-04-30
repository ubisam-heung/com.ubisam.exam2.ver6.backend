package backend.rest.accounts;


import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.u2ware.common.docs.MockMvcRestDocs;


@Component
public class AccountDocs extends MockMvcRestDocs {


    public Map<String,Object> newEntity(String username){
        Map<String,Object> r = new HashMap<>();
        r.put("username", username);
        // r.put("password", "aaa")
        // r.put("roles", Arrays.asList("ROLE_ADMIN"));
        return r;
    }


    public Map<String,Object> resetEntity(){
        Map<String,Object> r = new HashMap<>();
        r.put("password", randomText("Password"));
        return r;
    }



    public Map<String,Object> searchEntity(){

        Map<String,Object> r = new HashMap<>();

        r.put("age", 111);
        return r;
    }
    
}
