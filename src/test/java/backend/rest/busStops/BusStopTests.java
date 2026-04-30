package backend.rest.busStops;

import static io.u2ware.common.docs.MockMvcRestDocs.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import backend.domain.BusStop;
import backend.oauth2.Oauth2Docs;
import io.u2ware.common.data.jpa.repository.query.JpaSpecificationBuilder;

@SpringBootTest
@AutoConfigureMockMvc
public class BusStopTests {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private Oauth2Docs od;

  @Autowired
  private BusStopDocs bsd;

  @Autowired
  private BusStopRepository busStopRepository;

  @Test
  void contextLoads() throws Exception{
    // 유저 설정
    Jwt u = od.jose("user");

    // Crud - C
    mvc.perform(post("/rest/busStops").content(bsd::newEntity, "가산정류장")).andExpect(is4xx()).andDo(result(bsd::context, "entity1"));
    mvc.perform(post("/rest/busStops").content(bsd::newEntity, "가산정류장").auth(u)).andExpect(is2xx()).andDo(result(bsd::context, "entity1"));

    // Crud - R
    String uri = bsd.context("entity1", "$._links.self.href");
    mvc.perform(post(uri)).andExpect(is4xx());
    mvc.perform(post(uri).auth(u)).andExpect(is2xx());

    // Crud - U
    Map<String, Object> body = bsd.context("entity1", "$");
    mvc.perform(put(uri).content(bsd::updateEntity, body, "남구로정류장")).andExpect(is4xx());
    mvc.perform(put(uri).content(bsd::updateEntity, body, "남구로정류장").auth(u)).andExpect(is2xx());

    // Crud - D
    mvc.perform(delete(uri)).andExpect(is4xx());
    mvc.perform(delete(uri).auth(u)).andExpect(is2xx());

  }
  
  @Test
  void contextLoads2() throws Exception{
    
    List<BusStop> result;
    boolean hasResult;

    // 40개의 정류장 추가
    List<BusStop> busStopLists = new ArrayList<>();
    for(int i=1; i<= 40; i++){
      busStopLists.add(bsd.newEntity(i+"정류장", i+"위치"));
    }
    busStopRepository.saveAll(busStopLists);

    // 이름 쿼리
    JpaSpecificationBuilder<BusStop> nameQuery = JpaSpecificationBuilder.of(BusStop.class);
    nameQuery.where().and().eq("busStopName", "4정류장");
    result = busStopRepository.findAll(nameQuery.build());
    hasResult = result.stream().anyMatch(u -> "4정류장".equals(u.getBusStopName()));
    assertEquals(true, hasResult);

    // 라이센스 쿼리
    JpaSpecificationBuilder<BusStop> locationQuery = JpaSpecificationBuilder.of(BusStop.class);
    locationQuery.where().and().eq("busStopLocation", "4위치");
    result = busStopRepository.findAll(locationQuery.build());
    hasResult = result.stream().anyMatch(u -> "4위치".equals(u.getBusStopLocation()));
    assertEquals(true, hasResult);
   
  }

  @Test
  void contextLoads3() throws Exception{
   
    // 유저 설정
    Jwt u = od.jose("user1");
      
    // 40명의 운전수 추가
    List<BusStop> busStopLists = new ArrayList<>();
    for(int i=1; i<= 40; i++){
      busStopLists.add(bsd.newEntity(i+"정류장", i+"위치"));
    }
    busStopRepository.saveAll(busStopLists);

    String uri = "/rest/busStops/search";
 
    // Search - 단일 검색
    mvc.perform(post(uri).auth(u).content(bsd::setSearch, "busStopName", "6정류장")).andExpect(is2xx());
    mvc.perform(post(uri).auth(u).content(bsd::setSearch, "busStopLocation", "5위치")).andExpect(is2xx());

    // Search - 페이지네이션 
    mvc.perform(post(uri).auth(u).param("size", "4")).andExpect(is2xx());

    // Search - 정렬 busStopName, desc
    mvc.perform(post(uri).auth(u).param("sort", "busStopName,desc")).andExpect(is2xx());

  }


}
