package backend.rest.busRoutes;

import java.util.UUID;

import backend.domain.BusRoute;
import io.u2ware.common.data.jpa.repository.RestfulJpaRepository;
import java.util.List;


public interface BusRouteRepository extends RestfulJpaRepository<BusRoute, UUID>{

  List<BusRoute> findByBusRouteName(String busRouteName);
  
}
