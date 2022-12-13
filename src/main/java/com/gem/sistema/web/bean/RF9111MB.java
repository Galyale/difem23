package com.gem.sistema.web.bean;

import static com.gem.sistema.util.UtilFront.generateNotificationFront;
import static javax.faces.application.FacesMessage.SEVERITY_ERROR;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.StreamedContent;

import com.gem.sistema.business.domain.Conctb;
import com.gem.sistema.business.domain.TcMes;
import com.gem.sistema.business.domain.TrPuestoFirma;
import com.gem.sistema.business.repository.catalogs.ConctbRepository;
import com.gem.sistema.business.repository.catalogs.TcMesRepository;
import com.gem.sistema.business.service.callsp.ParamsRF9111DTO;
import com.gem.sistema.business.service.catalogos.PuestosFirmasService;
import com.gem.sistema.util.ConstantsClaveEnnum;
import com.gem.sistema.web.security.model.GemUser;
import com.roonin.utils.UtilDate;

// TODO: Auto-generated Javadoc
/**
 * The Class RF9111MB.
 */
@ManagedBean
@ViewScoped
public class RF9111MB extends BaseDirectReport {

	/** The mes. */
	private Integer mes;

	/** The descrip mes. */
	private TcMes descripMes;

	/** The report by PL. */
	@ManagedProperty(value = "#{baseDirectReportByPL}")
	private BaseDirectReportByPL reportByPL;

	/** The tc mes repository. */
	@ManagedProperty(value = "#{tcMesRepository}")
	private TcMesRepository tcMesRepository;

	@ManagedProperty("#{conctbRepository}")
	private ConctbRepository conctbRepository;

	@ManagedProperty("#{puestosFirmasService}")
	private PuestosFirmasService puestosFirmasService;

	private static final String NAME_PROCEDURE = "SP_GENERA_XLS_FLUJO_EFECTIVO";

	/**
	 * Gets the report by PL.
	 *
	 * @return the report by PL
	 */
	public BaseDirectReportByPL getReportByPL() {
		return reportByPL;
	}

	/**
	 * Sets the report by PL.
	 *
	 * @param reportByPL the new report by PL
	 */
	public void setReportByPL(BaseDirectReportByPL reportByPL) {
		this.reportByPL = reportByPL;
	}

	/**
	 * Gets the descrip mes.
	 *
	 * @return the descrip mes
	 */
	public TcMes getDescripMes() {
		return descripMes;
	}

	/**
	 * Sets the descrip mes.
	 *
	 * @param descripMes the new descrip mes
	 */
	public void setDescripMes(TcMes descripMes) {
		this.descripMes = descripMes;
	}

	/**
	 * Gets the tc mes repository.
	 *
	 * @return the tc mes repository
	 */
	public TcMesRepository getTcMesRepository() {
		return tcMesRepository;
	}

	/**
	 * Sets the tc mes repository.
	 *
	 * @param tcMesRepository the new tc mes repository
	 */
	public void setTcMesRepository(TcMesRepository tcMesRepository) {
		this.tcMesRepository = tcMesRepository;
	}

	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init() {
		LOGGER.info(":: En postconsruct RF9111MB ");
		jasperReporteName = "rf9111";
		endFilename = jasperReporteName + ".pdf";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gem.sistema.web.bean.BaseDirectReport#getParametersReports()
	 */
	public Map<String, Object> getParametersReports() {
		Map<String, Object> paramsReport = new java.util.HashMap<String, Object>();

		descripMes = tcMesRepository.findByMes(StringUtils.leftPad(mes.toString(), 2, "0"));
		Integer idSector = this.getUserDetails().getIdSector();
		Conctb conctb = conctbRepository.findByIdsector(idSector);
		TrPuestoFirma firma = new TrPuestoFirma();
		paramsReport.put("CAMPO1", conctb.getNomDep());
		paramsReport.put("CAMPO2", conctb.getRfc());
		paramsReport.put("DIA", UtilDate.getLastDayByAnoEmp(mes, conctb.getAnoemp()));
		paramsReport.put("MES", descripMes.getDescripcion());

		paramsReport.put("ANIO", conctb.getAnoemp());
		paramsReport.put("SECTOR", idSector);
		paramsReport.put("pImg1", conctb.getImagePathLeft());
		paramsReport.put("pImg2", conctb.getImagePathRigth());
		firma = puestosFirmasService.getFirmaBySectorAnioClave(idSector, 0L, ConstantsClaveEnnum.CLAVE_F07.getValue());
		paramsReport.put("pL1", firma.getPuesto().getPuesto());
		paramsReport.put("pN1", firma.getNombre());
		firma = puestosFirmasService.getFirmaBySectorAnioClave(idSector, 0L, ConstantsClaveEnnum.CLAVE_F08.getValue());
		paramsReport.put("pL2", firma.getPuesto().getPuesto());
		paramsReport.put("pN2", firma.getNombre());
		firma = puestosFirmasService.getFirmaBySectorAnioClave(idSector, 0L, ConstantsClaveEnnum.CLAVE_F09.getValue());
		paramsReport.put("pL3", firma.getPuesto().getPuesto());
		paramsReport.put("pN3", firma.getNombre());
		firma = puestosFirmasService.getFirmaBySectorAnioClave(idSector, 0L, ConstantsClaveEnnum.CLAVE_F10.getValue());
		paramsReport.put("pL4", firma.getPuesto().getPuesto());
		paramsReport.put("pN4", firma.getNombre());

		return paramsReport;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gem.sistema.web.bean.BaseDirectReport#generaReporteSimple(int)
	 */
	public StreamedContent generaReporteSimple(int type) {
		return null;
	}

	/**
	 * Gets the file xls using link.
	 *
	 * @return the file xls using link
	 */
	public StreamedContent getFileXlsUsingLink() {
		StreamedContent streamedContent = null;
		try {
			GemUser user = getUserDetails();
			ParamsRF9111DTO params = new ParamsRF9111DTO();
			params.setIdSector(user.getIdSector());
			params.setMes(mes);
			reportByPL.callSpService.getFile(NAME_PROCEDURE, params);
			if (params.getCodError() == 0) {
				generateNotificationFront(SEVERITY_ERROR, MESSAGE_ERROR, params.getMsgError());
			} else {
				streamedContent = this.getFileXls();
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			generateNotificationFront(SEVERITY_ERROR, MESSAGE_ERROR, e.getMessage());
		}
		return streamedContent;
	}

	/**
	 * File pdf using link.
	 */
	public void filePdfUsingLink() {
		try {
			GemUser user = getUserDetails();
			ParamsRF9111DTO params = new ParamsRF9111DTO();
			params.setIdSector(user.getIdSector());
			params.setMes(mes);
			reportByPL.callSpService.getFile(NAME_PROCEDURE, params);
			if (params.getCodError() == 0) {
				generateNotificationFront(SEVERITY_ERROR, MESSAGE_ERROR, params.getMsgError());
			} else {
				this.createFilePdfInline();
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			generateNotificationFront(SEVERITY_ERROR, MESSAGE_ERROR, e.getMessage());
		}

	}

	public StreamedContent getFileTxtUsingLink() {
		StreamedContent streamedContent = null;
		try {
			GemUser user = getUserDetails();
			ParamsRF9111DTO params = new ParamsRF9111DTO();
			params.setIdSector(user.getIdSector());
			params.setMes(mes);
			reportByPL.callSpService.getFile(NAME_PROCEDURE, params);
			if (params.getCodError() == 0) {
				generateNotificationFront(SEVERITY_ERROR, MESSAGE_ERROR, params.getMsgError());
			} else {
				streamedContent = this.getFileTxt();
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			generateNotificationFront(SEVERITY_ERROR, MESSAGE_ERROR, e.getMessage());
		}
		return streamedContent;
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
	 * @param mes the new mes
	 */
	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public ConctbRepository getConctbRepository() {
		return conctbRepository;
	}

	public void setConctbRepository(ConctbRepository conctbRepository) {
		this.conctbRepository = conctbRepository;
	}

	public PuestosFirmasService getPuestosFirmasService() {
		return puestosFirmasService;
	}

	public void setPuestosFirmasService(PuestosFirmasService puestosFirmasService) {
		this.puestosFirmasService = puestosFirmasService;
	}

}
