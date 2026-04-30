package backend.rest.busDrivers;

import java.util.UUID;

import backend.domain.BusDriver;
import io.u2ware.common.data.jpa.repository.RestfulJpaRepository;

public interface BusDriverRepository extends RestfulJpaRepository<BusDriver, UUID>{

}