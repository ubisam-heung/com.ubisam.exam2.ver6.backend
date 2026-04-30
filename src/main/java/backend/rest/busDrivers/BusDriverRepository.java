package backend.rest.busDrivers;

import java.util.UUID;

import backend.domain.BusDriver;
import io.u2ware.common.data.jpa.repository.RestfulJpaRepository;
import java.util.List;


public interface BusDriverRepository extends RestfulJpaRepository<BusDriver, UUID>{

  List<BusDriver> findByBusDriverName(String busDriverName);

}