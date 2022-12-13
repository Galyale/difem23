package com.gem.sistema.business.repository.catalogs;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.gem.sistema.business.domain.TrPresupuestoArea;

@Repository(value = "trPresupuestoAreaRepository")
public interface TrPresupuestoAreaRepository extends PagingAndSortingRepository<TrPresupuestoArea, Long> {

}
