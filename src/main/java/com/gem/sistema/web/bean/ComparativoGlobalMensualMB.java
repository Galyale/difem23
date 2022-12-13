package com.gem.sistema.web.bean;

import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import com.gem.sistema.business.domain.Conctb;
import com.gem.sistema.business.domain.TrPuestoFirma;
import com.gem.sistema.business.repository.catalogs.ConctbRepository;
import com.gem.sistema.business.service.catalogos.PuestosFirmasService;
import com.gem.sistema.business.service.catalogos.ValidatePolicyService;
import com.gem.sistema.util.ConstantsClaveEnnum;
import com.gem.sistema.web.security.model.GemUser;

// TODO: Auto-generated Javadoc
/**
 * The Class ComparativoGlobalMensualMB.
 */
@ManagedBean
@ViewScoped
public class ComparativoGlobalMensualMB extends BaseDirectReport {
	
	private static final String DOWNLOAD_TXT = " jQuery('#form1\\\\:downTxt').click();";

	/** The mes. */
	private Integer mes;

	/** The num firmas. */
	private Integer numFirmas;


	@ManagedProperty("#{validatePolicyService}")
	private ValidatePolicyService validatePolicyService;
	
	@ManagedProperty("#{puestosFirmasService}")
	private PuestosFirmasService puestosFirmasService;
	
	@ManagedProperty("#{conctbRepository}")
	private ConctbRepository conctbRepository;

	public PuestosFirmasService getPuestosFirmasService() {
		return puestosFirmasService;
	}

	public void setPuestosFirmasService(PuestosFirmasService puestosFirmasService) {
		this.puestosFirmasService = puestosFirmasService;
	}

	public ConctbRepository getConctbRepository() {
		return conctbRepository;
	}

	public void setConctbRepository(ConctbRepository conctbRepository) {
		this.conctbRepository = conctbRepository;
	}

	/**
	 * Gets the num firmas.
	 *
	 * @return the num firmas
	 */
	public Integer getNumFirmas() {
		return numFirmas;
	}

	/**
	 * Sets the num firmas.
	 *
	 * @param numFirmas
	 *            the new num firmas
	 */
	public void setNumFirmas(Integer numFirmas) {
		this.numFirmas = numFirmas;
	}

	

	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init() {
		LOGGER.info(":: En postconsruct ComparativoGlobalMensualMB ");
		jasperReporteName = "ReporteComparativo_GlobalMensual";
		endFilename = jasperReporteName + ".pdf";

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gem.sistema.web.bean.BaseDirectReport#getParametersReports()
	 */
	public Map<String, Object> getParametersReports() {
		Integer idSector = this.getUserDetails().getIdSector();
		Map<String, Object> paramsReport = new java.util.HashMap<String, Object>();
		Conctb conctb = conctbRepository.findByIdsector(idSector);
		TrPuestoFirma firma = new TrPuestoFirma();
		GemUser user = this.getUserDetails();
		paramsReport.put("mes", mes);
		paramsReport.put("sector", user.getIdSector());
		paramsReport.put("numeroFirmas", 4);
		paramsReport.put("anio", conctb.getAnoemp());
		paramsReport.put("campo1", conctb.getNomDep());
		paramsReport.put("p_img1", conctb.getImagePathLeft());
		paramsReport.put("p_img2", conctb.getImagePathRigth());
		
		firma = puestosFirmasService.getFirmaBySectorAnioClave(idSector, 0L,
				ConstantsClaveEnnum.CLAVE_F07.getValue());
		paramsReport.put("L1", firma.getPuesto().getPuesto());
		paramsReport.put("N1", firma.getNombre());
		firma = puestosFirmasService.getFirmaBySectorAnioClave(idSector, 0L,
				ConstantsClaveEnnum.CLAVE_F08.getValue());
		paramsReport.put("L2", firma.getPuesto().getPuesto());
		paramsReport.put("N2", firma.getNombre());
		firma = puestosFirmasService.getFirmaBySectorAnioClave(idSector, 0L,
				ConstantsClaveEnnum.CLAVE_F09.getValue());
		paramsReport.put("L3", firma.getPuesto().getPuesto());
		paramsReport.put("N3", firma.getNombre());
		firma = puestosFirmasService.getFirmaBySectorAnioClave(idSector, 0L,
				ConstantsClaveEnnum.CLAVE_F10.getValue());
		paramsReport.put("L4", firma.getPuesto().getPuesto());
		paramsReport.put("N4", firma.getNombre());
		
		// L4, N4, L5, N5, L27 y N27

		return paramsReport;
	}

	public void downloadTxt() {
		if (this.validatePolicyService.isOpenMonth(mes, null, this.getUserDetails().getIdSector()) == Boolean.TRUE) {
			this.validatePolicyService.isAfectMonth(mes, null, this.getUserDetails().getIdSector());
			this.validatePolicyService.existPolices(mes, null, this.getUserDetails().getIdSector());

			RequestContext.getCurrentInstance().execute(DOWNLOAD_TXT);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gem.sistema.web.bean.BaseDirectReport#generaReporteSimple(int)
	 */
	public StreamedContent generaReporteSimple(int type) {
		return null;
		/*
		 * Map<String, Object> paramsQuery = new java.util.HashMap<String,
		 * Object>(); paramsQuery.put("ID_REF", new Integer(0)); //FALTA return
		 * reporteadorDirectoImpl.getFileReport(tcReporte,paramsQuery,
		 * reporteName,type);
		 */
	}

	/**
	 * Gets the mes.
	 *
	 * @return the mes
	 */
	public Integer getMes() {
		return mes;
	}

	/**
	 * Sets the mes.
	 *
	 * @param mes
	 *            the new mes
	 */
	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public ValidatePolicyService getValidatePolicyService() {
		return validatePolicyService;
	}

	public void setValidatePolicyService(ValidatePolicyService validatePolicyService) {
		this.validatePolicyService = validatePolicyService;
	}

}
