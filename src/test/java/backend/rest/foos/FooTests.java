package backend.rest.foos;

import static io.u2ware.common.docs.MockMvcRestDocs.get;
import static io.u2ware.common.docs.MockMvcRestDocs.is2xx;
import static io.u2ware.common.docs.MockMvcRestDocs.is4xx;
import static io.u2ware.common.docs.MockMvcRestDocs.post;
import static io.u2ware.common.docs.MockMvcRestDocs.print;

import java.util.Map;

import static io.u2ware.common.docs.MockMvcRestDocs.*;

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
public class FooTests {


	protected Log logger = LogFactory.getLog(getClass());


	protected @Autowired MockMvc mvc;

	protected @Autowired Oauth2Docs od;	
	protected @Autowired FooDocs fd;




	@Test
	void contextLoads1() throws Exception{
		
		mvc.perform(get("/rest/profile/foos")).andExpect(is4xx()).andDo(print());

        Jwt u = od.jose("fooUser");

		//////////////////////////////////////////////
		// CrudRepository
		//////////////////////////////////////////////
		mvc.perform(get("/rest/foos")).andExpect(is4xx());             // unauthorized
		mvc.perform(get("/rest/foos").auth(u)).andExpect(is2xx());     // ok

		mvc.perform(get("/rest/foos/search")).andExpect(is4xx());          // unauthorized
		mvc.perform(get("/rest/foos/search").auth(u)).andExpect(is2xx());  // ok

		mvc.perform(post("/rest/foos/search")).andExpect(is4xx());          // unauthorized
		mvc.perform(post("/rest/foos/search").auth(u)).andExpect(is4xx());  // not supported

		// C
		mvc.perform(post("/rest/foos").content(fd::newEntity, "a")).andExpect(is4xx());
		mvc.perform(post("/rest/foos").auth(u).content(fd::newEntity, "a")).andExpect(is2xx())
			.andDo(result(fd::context, "f1"));


		// R
		String uri = fd.context("f1", "$._links.self.href");
		mvc.perform(get(uri)).andExpect(is4xx());             // unauthorized
		mvc.perform(get(uri).auth(u)).andExpect(is2xx()).andDo(print());// ok

		mvc.perform(post(uri)).andExpect(is4xx());         // unauthorized
		mvc.perform(post(uri).auth(u)).andExpect(is4xx()); // not supported


		// U
		Map<String,Object> body = fd.context("f1");
		mvc.perform(put(uri).content(fd::resetEntity, body)).andExpect(is4xx());     // unauthorized
		mvc.perform(put(uri).content(fd::resetEntity, body).auth(u)).andExpect(is2xx()).andDo(print());     // ok


		// D
		mvc.perform(delete(uri)).andExpect(is4xx());         // unauthorized
		mvc.perform(delete(uri).auth(u)).andExpect(is2xx()); // ok



	}



}
