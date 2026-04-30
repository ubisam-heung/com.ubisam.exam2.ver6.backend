package backend.rest.bars;

import static io.u2ware.common.docs.MockMvcRestDocs.delete;
import static io.u2ware.common.docs.MockMvcRestDocs.get;
import static io.u2ware.common.docs.MockMvcRestDocs.is2xx;
import static io.u2ware.common.docs.MockMvcRestDocs.is4xx;
import static io.u2ware.common.docs.MockMvcRestDocs.post;
import static io.u2ware.common.docs.MockMvcRestDocs.print;
import static io.u2ware.common.docs.MockMvcRestDocs.put;
import static io.u2ware.common.docs.MockMvcRestDocs.result;

import java.util.Map;

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
public class BarTests {


	protected Log logger = LogFactory.getLog(getClass());

	protected @Autowired MockMvc mvc;

	protected @Autowired Oauth2Docs od;	
	protected @Autowired BarDocs bd;


	@Test 
	void contextLoads1() throws Exception{

		mvc.perform(get("/rest/profile/bars")).andExpect(is4xx());

        Jwt u = od.jose("barUser1");

		//////////////////////////////////////////////
		// RestfulJpaRepository
		//////////////////////////////////////////////
		mvc.perform(get("/rest/bars")).andExpect(is4xx());         // unauthorized
		mvc.perform(get("/rest/bars").auth(u)).andExpect(is4xx()); // not supported

		mvc.perform(get("/rest/bars/search")).andExpect(is4xx());          // unauthorized
		mvc.perform(get("/rest/bars/search").auth(u)).andExpect(is4xx());  // not supported

		mvc.perform(post("/rest/bars/search")).andExpect(is4xx());         // unauthorized
		mvc.perform(post("/rest/bars/search").auth(u)).andExpect(is2xx()); // ok

		// C
		mvc.perform(post("/rest/bars").content(bd::newEntity)).andExpect(is4xx());
		mvc.perform(post("/rest/bars").auth(u).content(bd::newEntity)).andExpect(is2xx()).andDo(print())
			.andDo(result(bd::context, "b1"));


		// R
		String uri = bd.context("b1", "$._links.self.href");
		mvc.perform(get(uri)).andExpect(is4xx());             // unauthorized
		mvc.perform(get(uri).auth(u)).andExpect(is4xx());     // not supported

		mvc.perform(post(uri)).andExpect(is4xx());             // unauthorized
		mvc.perform(post(uri).auth(u)).andExpect(is2xx()).andDo(print());// ok


		// U
		Map<String,Object> body = bd.context("b1");
		mvc.perform(put(uri).content(bd::resetEntity, body)).andExpect(is4xx());     // unauthorized
		mvc.perform(put(uri).content(bd::resetEntity, body).auth(u)).andExpect(is2xx()).andDo(print()); // ok


		// D
		mvc.perform(delete(uri)).andExpect(is4xx());        // unauthorized
		mvc.perform(delete(uri).auth(u)).andExpect(is2xx());// ok



	}



}
