package backend.rest.buses;

import java.util.UUID;

import backend.domain.Bus;
import io.u2ware.common.data.jpa.repository.RestfulJpaRepository;
import java.util.List;


public interface BusRepository extends RestfulJpaRepository<Bus, UUID> {

  List<Bus> findByBusNumber(Integer busNumber);
}