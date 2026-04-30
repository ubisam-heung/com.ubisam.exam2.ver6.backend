package backend.rest.busStops;

import java.util.UUID;

import backend.domain.BusStop;
import io.u2ware.common.data.jpa.repository.RestfulJpaRepository;
import java.util.List;


public interface BusStopRepository extends  RestfulJpaRepository<BusStop, UUID>{

  List<BusStop> findByBusStopName(String busStopName);
  
}
