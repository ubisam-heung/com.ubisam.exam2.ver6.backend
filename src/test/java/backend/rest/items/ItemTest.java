package backend.rest.items;

import static io.u2ware.common.docs.MockMvcRestDocs.delete;
import static io.u2ware.common.docs.MockMvcRestDocs.is2xx;
import static io.u2ware.common.docs.MockMvcRestDocs.post;
import static io.u2ware.common.docs.MockMvcRestDocs.print;
import static io.u2ware.common.docs.MockMvcRestDocs.put;
import static io.u2ware.common.docs.MockMvcRestDocs.result;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import backend.domain.Item.Child;
import backend.oauth2.Oauth2Docs;
import backend.rest.bars.BarDocs;
import backend.rest.foos.FooDocs;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class ItemTest {
    
    protected Log logger = LogFactory.getLog(getClass());

    protected @Autowired MockMvc mvc;
    
	protected @Autowired Oauth2Docs od;	
	protected @Autowired FooDocs fd;
	protected @Autowired BarDocs bd;

	protected @Autowired ItemDocs id;

 
    @Test
    public void contextLoads() throws Exception{

        Jwt u = od.jose("itemUser1");

        //////////////////////////////
        // Ready
        //////////////////////////////
   		mvc.perform(post("/rest/foos").auth(u).content(fd::newEntity, "foo1"))
            .andDo(result(fd::context, "itemFoo1"))
            .andExpect(is2xx());
        Map<String,Object> fooBody1 = fd.context("itemFoo1", "$");
        String fooLink1 = fd.context("itemFoo1", "$._links.self.href");


   		mvc.perform(post("/rest/foos").auth(u).content(fd::newEntity, "foo2"))
            .andDo(result(fd::context, "itemFoo2"))
            .andExpect(is2xx());
        Map<String,Object> fooBody2 = fd.context("itemFoo2", "$");
        String fooLink2 = fd.context("itemFoo2", "$._links.self.href");


   		mvc.perform(post("/rest/bars").auth(u).content(bd::newEntity))
            .andDo(result(fd::context, "itemBar1"))
            .andExpect(is2xx());
        Map<String,Object> barBody1 = fd.context("itemBar1", "$");
        String barLink1 = fd.context("itemBar1", "$._links.self.href");


   		mvc.perform(post("/rest/bars").auth(u).content(bd::newEntity))
            .andDo(result(fd::context, "itemBar2"))
            .andExpect(is2xx());
        Map<String,Object> barBody2 = fd.context("itemBar2", "$");
        String barLink2 = fd.context("itemBar2", "$._links.self.href");


   		mvc.perform(post("/rest/bars").auth(u).content(bd::newEntity))
            .andDo(result(fd::context, "itemBar3"))
            .andExpect(is2xx());
        Map<String,Object> barBody3 = fd.context("itemBar3", "$");
        String barLink3 = fd.context("itemBar3", "$._links.self.href");


        logger.info("fooBody1: "+fooBody1);
        logger.info("fooLink1: "+fooLink1);
        logger.info("fooBody2: "+fooBody2);
        logger.info("fooLink2: "+fooLink2);
        logger.info("barBody1: "+barBody1);
        logger.info("barLink1: "+barLink1);
        logger.info("barBody2: "+barBody2);
        logger.info("barLink2: "+barLink2);
        logger.info("barBody3: "+barBody3);
        logger.info("barLink3: "+barLink3);


        Child c1 = new Child("c1",1);
        Child c2 = new Child("c2",2);
        Child c3 = new Child("c3",3);
        Child c4 = new Child("c4",4);


        Map<String,Object> jsonValue = new HashMap<>();
        jsonValue.put("key1", "value1");


        Set<Object> arrayValue = new HashSet<>();
        arrayValue.add("hello");
        arrayValue.add(jsonValue);


        //////////////////////////////
        // Create
        //////////////////////////////
        Map<String,Object> req = new HashMap<>();
        req.put("title", "item1");
        req.put("fooLink", fooLink1);
        req.put("barsLinks", new Object[]{barBody1});
        req.put("childs", new Object[]{c1, c2});
        req.put("jsonValue", jsonValue);
        req.put("arrayValue", arrayValue);
        req.put("cryptoValue", "helloworld");

        mvc.perform(post("/rest/items").auth(u).content(req))
            .andDo(result(id::context, "item1"))
            .andDo(print())
            .andExpect(is2xx());
        String link1 = id.context("item1", "$._links.self.href");
        req = id.context("item1", "$");
        logger.info(req);
        logger.info(link1);

      
        //////////////////////////////
        // Update
        //////////////////////////////       
        req.put("fooLink", fooBody2);
        req.put("barsLinks", new Object[]{barBody2, barLink3});
        req.put("childs", new Object[]{c3, c4});

        mvc.perform(put(link1).auth(u).content(req))
            .andDo(print())
            .andExpect(is2xx());

        //////////////////////////////
        // Read
        //////////////////////////////
   		mvc.perform(post(link1).auth(u))
            .andDo(print())
            .andExpect(is2xx());

        //////////////////////////////
        // Delete
        //////////////////////////////
   		mvc.perform(delete(link1).auth(u))
            .andDo(print())
            .andExpect(is2xx());
    }
}
