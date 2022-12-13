package com.gem.sistema.business.service.catalogos;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gem.sistema.business.domain.TcClave;
import com.gem.sistema.business.domain.TcPuesto;
import com.gem.sistema.business.domain.TrPuestoFirma;
import com.gem.sistema.business.repository.catalogs.TcClaveRepository;
import com.gem.sistema.business.repository.catalogs.TcPuestoRepository;
import com.gem.sistema.business.repository.catalogs.TrPuestoFirmaRepository;

/**
 * @author Alfredo Neri
 *
 */
@Service(value = "puestosFirmasService")
public class PuestosFirmasServiceImpl implements PuestosFirmasService {

	@Autowired
	private TcClaveRepository claveRepository;

	@Autowired
	private TcPuestoRepository puestoRepository;

	@Autowired
	private TrPuestoFirmaRepository firmaRepository;

	@Override
	public List<TcClave> listClaves(Integer idSector) {
		return claveRepository.findAvailableKeys(idSector);
	}

	@Override
	public List<TcPuesto> listPuestos(Integer idSector, Long idAnio) {
		return this.puestoRepository.findPuestosBySectorAndAnio(idSector, idAnio);
	}

	@Override
	public void createRelationshipPuesto(TcPuesto puesto, Long idAnio) {
		TrPuestoFirma firma = new TrPuestoFirma();
		firma.setPuesto(puesto);
		firma.setIdAnio(idAnio);
		firma.setNombre(StringUtils.EMPTY);
		
		firmaRepository.save(firma);
	}

	@Override
	public void deleteRelationshipPuesto(List<TrPuestoFirma>  firma) {
		firmaRepository.delete(firma);
	}

	@Override
	public List<TrPuestoFirma> listPuestosFirmas(Integer idSector, Long idAnio) {
		return this.firmaRepository.findFirmasBySectorAndAnio(idSector, idAnio); //puestosFirmasDAO.listPuestosFirmas(idSector, idAnio);
	}

	@Override
	public void createPuesto(TcPuesto puesto, Long idAnio) {
		this.puestoRepository.save(puesto);
		TrPuestoFirma firma = new TrPuestoFirma();
		firma.setNombre(StringUtils.EMPTY);
		firma.setPuesto(puesto);
		firma.setIdAnio(idAnio);
		this.firmaRepository.save(firma);
	}

	@Override
	public void updateNameFirma(TrPuestoFirma firma) {
		this.firmaRepository.save(firma);
	}

	
	@Override
	public void updateNamePuesto(TcPuesto puesto) {
		this.puestoRepository.save(puesto);
	}

	@Override
	public TrPuestoFirma getFirmaBySectorAnioClave(Integer idSector, Long idAnio, String clave) {

		TrPuestoFirma trPuestoFirma = this.firmaRepository.findFirmaBySectorAndAnioAndClave(idSector, idAnio, clave);
		
		if(null == trPuestoFirma) {
			TcPuesto tcPuesto = new TcPuesto();
			trPuestoFirma = new TrPuestoFirma();
			trPuestoFirma.setPuesto(tcPuesto);
		}
		return trPuestoFirma;
	}

	public TcClaveRepository getClaveRepository() {
		return claveRepository;
	}

	public void setClaveRepository(TcClaveRepository claveRepository) {
		this.claveRepository = claveRepository;
	}

	public TcPuestoRepository getPuestoRepository() {
		return puestoRepository;
	}

	public void setPuestoRepository(TcPuestoRepository puestoRepository) {
		this.puestoRepository = puestoRepository;
	}

	public TrPuestoFirmaRepository getFirmaRepository() {
		return firmaRepository;
	}

	public void setFirmaRepository(TrPuestoFirmaRepository firmaRepository) {
		this.firmaRepository = firmaRepository;
	}
}
