package backend.rest.busStops;

import java.util.UUID;

import org.springframework.data.rest.core.annotation.RestResource;

import backend.domain.BusStop;
import io.u2ware.common.data.jpa.repository.RestfulJpaRepository;
import java.util.List;


public interface BusStopRepository extends RestfulJpaRepository<BusStop, UUID>{

  @RestResource(exported = false)
  List<BusStop> findByBusStopName(String busStopName);
  
}
