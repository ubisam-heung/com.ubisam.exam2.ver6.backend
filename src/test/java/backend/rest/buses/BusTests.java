package backend.rest.buses;

import static io.u2ware.common.docs.MockMvcRestDocs.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import backend.domain.Bus;
import backend.domain.Bus.BusRepairHistory;
import backend.oauth2.Oauth2Docs;
import backend.rest.busDrivers.BusDriverDocs;
import backend.rest.busRoutes.BusRouteDocs;
import io.u2ware.common.data.jpa.repository.query.JpaSpecificationBuilder;

@SpringBootTest
@AutoConfigureMockMvc
public class BusTests {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private Oauth2Docs od;

  @Autowired
  private BusDocs bd;

  @Autowired
  private BusRepository busRepository;

  @Autowired
  private BusDriverDocs bdd;

  @Autowired
  private BusRouteDocs brd;

  @Test
  void contextLoads() throws Exception {
    // 유저 설정
    Jwt u = od.jose("user");

    // 사전 설정
    mvc.perform(post("/rest/busDrivers").content(bdd::newEntity, "김길동").auth(u)).andDo(result(bdd::context, "busDriverEntity1")).andExpect(is2xx());
    mvc.perform(post("/rest/busDrivers").content(bdd::newEntity, "이길동").auth(u)).andDo(result(bdd::context, "busDriverEntity2")).andExpect(is2xx());
    mvc.perform(post("/rest/busRoutes").content(brd::newEntity, "가산노선").auth(u)).andDo(result(brd::context, "busRouteEntity1")).andExpect(is2xx());
    mvc.perform(post("/rest/busRoutes").content(brd::newEntity, "남구로노선").auth(u)).andDo(result(brd::context, "busRouteEntity2")).andExpect(is2xx());
    String busDriverLink1 = bdd.context("busDriverEntity1", "$._links.self.href");
    String busDriverLink2 = bdd.context("busDriverEntity2", "$._links.self.href");
    String busRouteLink1 = bdd.context("busRouteEntity1", "$._links.self.href");
    String busRouteLink2 = bdd.context("busRouteEntity2", "$._links.self.href");

    Set<Object> busType = new HashSet<>();
    busType.add("간선");
    busType.add("지선");

    BusRepairHistory brh1 = new BusRepairHistory("brh1","수리내용1", "202604");
    BusRepairHistory brh2 = new BusRepairHistory("brh2","수리내용2", "202604");
    BusRepairHistory brh3 = new BusRepairHistory("brh3","수리내용3", "202604");

    Map<String, Object> req = new HashMap<>();
    req.put("title", "entity1");
    req.put("busNumber", 1140);
    req.put("busType", busType);
    req.put("busDriverLink", busDriverLink1);
    req.put("busRouteLink", busRouteLink1);
    req.put("busRepairHistory", new Object[]{brh1, brh2});

    // Crud - C
    mvc.perform(post("/rest/buses").content(req)).andDo(print()).andExpect(is4xx()).andDo(result(bd::context, "entity1"));
    mvc.perform(post("/rest/buses").content(req).auth(u)).andDo(print()).andExpect(is2xx()).andDo(result(bd::context, "entity1"));
    String uri = bd.context("entity1", "$._links.self.href");
    req = bd.context("entity1", "$");
        
    // Crud - R
    mvc.perform(post(uri)).andExpect(is4xx());
    mvc.perform(post(uri).auth(u)).andExpect(is2xx());

    // Crud - U
    req.put("busNumber", 1139);
    req.put("busDriverLink", busDriverLink2);
    req.put("busRouteLink", busRouteLink2);
    req.put("busRepairHistory", new Object[]{brh2, brh3});
    mvc.perform(put(uri).content(req)).andExpect(is4xx());
    mvc.perform(put(uri).content(req).auth(u)).andExpect(is2xx());

    // Crud - D
    mvc.perform(delete(uri)).andExpect(is4xx());
    mvc.perform(delete(uri).auth(u)).andExpect(is2xx());
  }
  // 핸들러 테스트용
  @Test
  void contextLoads2 () throws Exception{
    List<Bus> result;
    boolean hasResult;

    // 30개의 버스 추가
    List<Bus> busLists = new ArrayList<>();
    for(int i=1; i<=30; i++){
      busLists.add(bd.newEntity(i+"140"));
    }
    busRepository.saveAll(busLists);

    // 번호 쿼리
    JpaSpecificationBuilder<Bus> numberQuery = JpaSpecificationBuilder.of(Bus.class);
    numberQuery.where().and().eq("busNumber", 1140);
    result = busRepository.findAll(numberQuery.build());
    hasResult = result.stream().anyMatch(u -> 1140 == u.getBusNumber());
    assertEquals(true, hasResult);
  }

  @Test
  void contextLoads3 () throws Exception{
    // 유저 설정
    Jwt u = od.jose("user1");
    // 30개의 버스 추가
    List<Bus> busLists = new ArrayList<>();
    for(int i=1; i<=30; i++){
      busLists.add(bd.newEntity(i+"140"));
    }
    busRepository.saveAll(busLists);

    String uri = "/rest/buses/search";

    // Search - 단일 검색(버스 번호, 종류)
    mvc.perform(post(uri).content(bd::setSearch, "busNumber","4140").auth(u)).andExpect(is2xx());
    mvc.perform(post(uri).content(bd::setSearch, "busType", "간선").auth(u)).andExpect(is2xx());

    // Search - 페이지네이션 6개씩 5페이지
    mvc.perform(post(uri).param("size", "6").auth(u)).andExpect(is2xx());

    // Search - 정렬 busNumber,desc
    mvc.perform(post(uri).param("sort", "busNumber,desc").auth(u)).andExpect(is2xx());
  }
}
