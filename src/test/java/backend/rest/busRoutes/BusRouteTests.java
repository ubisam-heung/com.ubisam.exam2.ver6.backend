package backend.rest.busRoutes;

import static io.u2ware.common.docs.MockMvcRestDocs.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import backend.domain.BusRoute;
import backend.oauth2.Oauth2Docs;
import backend.rest.busStops.BusStopDocs;
import io.u2ware.common.data.jpa.repository.query.JpaSpecificationBuilder;

@SpringBootTest
@AutoConfigureMockMvc
public class BusRouteTests {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private Oauth2Docs od;

  @Autowired
  private BusRouteDocs brd;

  @Autowired
  private BusStopDocs bsd;

  @Autowired
  private BusRouteRepository busRouteRepository;

  @Test
  void contextLoads() throws Exception{
   
    // 유저 설정
    Jwt u = od.jose("user");

    // 사전 설정
    mvc.perform(post("/rest/busStops").content(bsd::newEntity, "가산정류장").auth(u)).andDo(result(bsd::context, "busStopEntity1"));
    String busStopLink1 = bsd.context("busStopEntity1", "$._links.self.href");
    mvc.perform(post("/rest/busStops").content(bsd::newEntity, "남구로정류장").auth(u)).andDo(result(bsd::context, "busStopEntity2"));
    String busStopLink2 = bsd.context("busStopEntity2", "$._links.self.href");
    mvc.perform(post("/rest/busStops").content(bsd::newEntity, "대림정류장").auth(u)).andDo(result(bsd::context, "busStopEntity3"));
    String busStopLink3 = bsd.context("busStopEntity3", "$._links.self.href");

    Map<String, Object> req = new HashMap<>();
    req.put("title", "entity1");
    req.put("busRouteName", "가산노선");
    req.put("busRouteStart", "시작1");
    req.put("busRouteEnd", "끝1");
    req.put("busStopLinks", Set.of(busStopLink1, busStopLink2));

    // Crud - C
    mvc.perform(post("/rest/busRoutes").content(req)).andExpect(is4xx()).andDo(result(brd::context, "entity1"));
    mvc.perform(post("/rest/busRoutes").content(req).auth(u)).andExpect(is2xx()).andDo(result(brd::context, "entity1"));
    String uri = brd.context("entity1", "$._links.self.href");
    req = brd.context("entity1", "$");

    // Crud - R
    mvc.perform(post(uri)).andExpect(is4xx());
    mvc.perform(post(uri).auth(u)).andExpect(is2xx());

    // Crud - U
    req.put("busRouteName", "건대노선");
    req.put("busStopLinks", Set.of(busStopLink2, busStopLink3));
    mvc.perform(put(uri).content(req)).andExpect(is4xx());
    mvc.perform(put(uri).content(req).auth(u)).andExpect(is2xx());
 
    // Crud - D
    mvc.perform(delete(uri)).andExpect(is4xx());
    mvc.perform(delete(uri).auth(u)).andExpect(is2xx());

  }
  
  @Test
  void contextLoads2() throws Exception{
    
    List<BusRoute> result;
    boolean hasResult;

    // 40개의 노선 추가
    List<BusRoute> busRouteLists = new ArrayList<>();
    for(int i=1; i<= 40; i++){
      busRouteLists.add(brd.newEntity(i+"노선", i+"시작", i+"끝"));
    }
    busRouteRepository.saveAll(busRouteLists);

    // 이름 쿼리
    JpaSpecificationBuilder<BusRoute> nameQuery = JpaSpecificationBuilder.of(BusRoute.class);
    nameQuery.where().and().eq("BusRouteName", "4노선");
    result = busRouteRepository.findAll(nameQuery.build());
    hasResult = result.stream().anyMatch(u -> "4노선".equals(u.getBusRouteName()));
    assertEquals(true, hasResult);

    // 시작점 쿼리
    JpaSpecificationBuilder<BusRoute> startQuery = JpaSpecificationBuilder.of(BusRoute.class);
    startQuery.where().and().eq("BusRouteStart", "4시작");
    result = busRouteRepository.findAll(startQuery.build());
    hasResult = result.stream().anyMatch(u -> "4시작".equals(u.getBusRouteStart()));
    assertEquals(true, hasResult);
    
    // 종점 쿼리
    JpaSpecificationBuilder<BusRoute> endQuery = JpaSpecificationBuilder.of(BusRoute.class);
    endQuery.where().and().eq("BusRouteEnd", "4끝");
    result = busRouteRepository.findAll(endQuery.build());
    hasResult = result.stream().anyMatch(u -> "4끝".equals(u.getBusRouteEnd()));
    assertEquals(true, hasResult);
  
  }

  @Test
  void contextLoads3() throws Exception{
    
    // 유저 설정
    Jwt u = od.jose("user1");
     
    // 40명의 운전수 추가
    List<BusRoute> busRouteLists = new ArrayList<>();
    for(int i=1; i<= 40; i++){
      busRouteLists.add(brd.newEntity(i+"노선", i+"시작", i+"끝"));
    }
    busRouteRepository.saveAll(busRouteLists);

    String uri = "/rest/busRoutes/search";

    // Search - 단일 검색
    mvc.perform(post(uri).auth(u).content(brd::setSearch, "busRouteName", "6노선")).andExpect(is2xx());
    mvc.perform(post(uri).auth(u).content(brd::setSearch, "busRouteStart", "5시작")).andExpect(is2xx());
    mvc.perform(post(uri).auth(u).content(brd::setSearch, "busRouteEnd", "4끝")).andExpect(is2xx());
  
    // Search - 페이지네이션 
    mvc.perform(post(uri).auth(u).param("size", "4")).andExpect(is2xx());

    // Search - 정렬 busStopName, desc
    mvc.perform(post(uri).auth(u).param("sort", "busStopName,desc")).andExpect(is2xx());
 
  }


}
