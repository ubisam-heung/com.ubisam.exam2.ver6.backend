package backend.rest.sessions;

import static io.u2ware.common.docs.MockMvcRestDocs.is2xx;
import static io.u2ware.common.docs.MockMvcRestDocs.is4xx;
import static io.u2ware.common.docs.MockMvcRestDocs.post;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import backend.oauth2.Oauth2Docs;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class SessionTests {
    
    protected Log logger = LogFactory.getLog(getClass());

    protected @Autowired MockMvc mvc;
    protected @Autowired SessionDocs td;
    protected @Autowired Oauth2Docs od;


    
    @Test
    public void contextLoads() throws Exception{

        Jwt u1 = od.jose("adminUserForToken", "ROLE_ADMIN");
        Jwt u2 = od.jose("u2");
        Jwt u3 = od.jose("u3");



        mvc.perform(post("/rest/sessions/search")).andExpect(is4xx());
        mvc.perform(post("/rest/sessions/search").auth(u1)).andExpect(is2xx());
        mvc.perform(post("/rest/sessions/search").auth(u2)).andExpect(is4xx());
        mvc.perform(post("/rest/sessions/search").auth(u3)).andExpect(is4xx());



    }
}
