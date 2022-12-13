
package com.gem.sistema.web.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gem.sistema.business.domain.Conctb;
import com.gem.sistema.business.domain.Fuentef;
import com.gem.sistema.business.domain.TcMes;
import com.gem.sistema.business.domain.TrPuestoFirma;
import com.gem.sistema.business.repository.catalogs.ConctbRepository;
import com.gem.sistema.business.repository.catalogs.FuentefRepository;
import com.gem.sistema.business.repository.catalogs.TcMesRepository;
import com.gem.sistema.business.repository.catalogs.TcPeriodoRepositoy;
import com.gem.sistema.business.service.catalogos.PuestosFirmasService;
import com.gem.sistema.business.service.catalogos.ValidatePolicyService;
import com.gem.sistema.business.service.reportador.ReportValidationException;
import com.gem.sistema.util.ConstantsClaveEnnum;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections4.CollectionUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

// TODO: Auto-generated Javadoc
/**
 * The Class EstadoAvancePresupuestalEgresosFuenteFinanciamientoMB.
 */
@ManagedBean(name = "estadoAvancePresupuestalEgresosFuenteFinanciamientoMB")
@ViewScoped
public class EstadoAvancePresupuestalEgresosFuenteFinanciamientoMB extends BaseDirectReport {

	private static final String DOWNLOAD_XLS = " jQuery('#form1\\\\:downXls').click();";

	/** The mes. */
	private String mes;

	/** The fuente F. */
	private Fuentef fuenteF;

	/** The list mes. */
	private List<TcMes> listMes;

	/** The list fuentef. */
	private List<Fuentef> listFuentef;

	/** The tcmes repository. */
	@ManagedProperty("#{tcMesRepository}")
	private TcMesRepository tcmesRepository;

	/** The fuentef repository. */
	@ManagedProperty("#{fuentefRepository}")
	private FuentefRepository fuentefRepository;

	@ManagedProperty("#{validatePolicyService}")
	private ValidatePolicyService validatePolicyService;

	@ManagedProperty("#{conctbRepository}")
	private ConctbRepository conctbRepository;

	@ManagedProperty("#{tcPeriodoRepositoy}")
	private TcPeriodoRepositoy periodoRepositoy;

	@ManagedProperty("#{puestosFirmasService}")
	private PuestosFirmasService puestosFirmasService;

	public TcPeriodoRepositoy getPeriodoRepositoy() {
		return periodoRepositoy;
	}

	public void setPeriodoRepositoy(TcPeriodoRepositoy periodoRepositoy) {
		this.periodoRepositoy = periodoRepositoy;
	}

	public ValidatePolicyService getValidatePolicyService() {
		return validatePolicyService;
	}

	public void setValidatePolicyService(ValidatePolicyService validatePolicyService) {
		this.validatePolicyService = validatePolicyService;
	}

	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init() {

		jasperReporteName = "EstadoAvancePresupuestalEgresosFuenteFinanciamiento";
		endFilename = jasperReporteName + ".pdf";

		// set specific charWidth and charHeight
		// JasperGenericServiceImpl serviceImpl = (JasperGenericServiceImpl)
		// jasperGenericService;
		// serviceImpl.CHAR_WIDTH = 5.45f;
		// serviceImpl.CHAR_HEIGHT = 13.85f;

		// llenar la lista
		listMes = tcmesRepository.findAll();
		listFuentef = fuentefRepository.getByIdSector(this.getUserDetails().getIdSector());

		// se inicializa las listas
		if (!CollectionUtils.isEmpty(listMes)) {
			mes = listMes.get(0).getMes();
		}
		if (!CollectionUtils.isEmpty(listFuentef)) {
			fuenteF = listFuentef.get(0);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gem.sistema.web.bean.BaseDirectReport#getParametersReports()
	 */
	@Override
	public Map<String, Object> getParametersReports() throws ReportValidationException {

		Integer sector = this.getUserDetails().getIdSector();
		Conctb conctb = conctbRepository.findByIdsector(sector);
		Map<String, Object> parameters = new HashMap<String, Object>();
		TrPuestoFirma firma = new TrPuestoFirma();

		parameters.put("mes", Integer.valueOf(mes));
		parameters.put("year", conctb.getAnoemp());
		parameters.put("imagePath", conctb.getImagePathLeft());
		parameters.put("imagePath2", conctb.getImagePathRigth());
		parameters.put("fuente", fuenteF.getLiga());
		parameters.put("entidadName", conctb.getNomDep());
		firma = this.puestosFirmasService.getFirmaBySectorAnioClave(sector, 0L, ConstantsClaveEnnum.CLAVE_F07.getValue());
		parameters.put("firmaP1", firma.getPuesto().getPuesto());
		parameters.put("firmaN1", firma.getNombre());
		firma = this.puestosFirmasService.getFirmaBySectorAnioClave(sector, 0L, ConstantsClaveEnnum.CLAVE_F08.getValue());
		parameters.put("firmaP2", firma.getPuesto().getPuesto());
		parameters.put("firmaN2", firma.getNombre());
		firma = this.puestosFirmasService.getFirmaBySectorAnioClave(sector, 0L, ConstantsClaveEnnum.CLAVE_F09.getValue());
		parameters.put("firmaP3", firma.getPuesto().getPuesto());
		parameters.put("firmaN3", firma.getNombre());
		firma = this.puestosFirmasService.getFirmaBySectorAnioClave(sector, 0L, ConstantsClaveEnnum.CLAVE_F10.getValue());
		parameters.put("firmaP4", firma.getPuesto().getPuesto());
		parameters.put("firmaN4", firma.getNombre());
		parameters.put("query", this.generateQueryByFuente(Integer.valueOf(mes), sector,fuenteF.getLiga().toString()));
		parameters.put("idsector", sector);

		return parameters;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gem.sistema.web.bean.BaseDirectReport#generaReporteSimple(int)
	 */
	@Override
	public StreamedContent generaReporteSimple(int type) throws ReportValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Generate query by fuente.
	 *
	 * @param mes
	 *            the mes
	 * @param idSector
	 *            the id sector
	 * @param fuente
	 *            the fuente
	 * @return the string
	 */
	public String generateQueryByFuente(Integer mes, Integer idSector, String fuente) {

		StringBuilder sQuery = new StringBuilder();
		StringBuilder sAmpli = new StringBuilder();
		StringBuilder sRedu = new StringBuilder();
		StringBuilder sEjpa = new StringBuilder();
		StringBuilder sEjxpa = new StringBuilder();
		StringBuilder sToeje = new StringBuilder();
		StringBuilder sCompro = new StringBuilder();
		String sAuto = "SUM( P.AUTO1_1 + P.AUTO1_2 + P.AUTO1_3 + P.AUTO1_4 + P.AUTO1_5 + P.AUTO1_6 + P.AUTO1_7 + P.AUTO1_8 + P.AUTO1_9 + P.AUTO1_10 + P.AUTO1_11 + P.AUTO1_12) AUTO1, ";
		String sFoot = "FROM PASO P, NATGAS N, FUENTEF F WHERE 1=1 AND P.PARTIDA = N.CLVGAS AND P.IDSECTOR = N.IDSECTOR AND P.IDSECTOR = F.IDSECTOR AND P.IDSECTOR = "
				+ idSector + " AND SUBSTR(P.PROGRAMA,13,3) = F.LIGA AND SUBSTR(P.PROGRAMA,13,3) = '" + fuente
				+ "' GROUP BY P.PARTIDA,F.NOMBREF, N.NOMGAS ) WHERE 	AMPLI1+EJPA1+EJXPA1+TOEJE1+REDU1+COMPRO1+AUTO1<>0" ;

		sQuery.append("SELECT * FROM ( SELECT P.PARTIDA, F.NOMBREF NOMBREF, N.NOMGAS NOMGAS, ");

		for (int i = 1; i <= mes; i++) {
			sAmpli.append(" P.AMPLI1_" + i + " +");
			sEjpa.append(" P.EJPA1_" + i + " +");
			sEjxpa.append(" P.EJXPA1_" + i + " +");
			sToeje.append(" P.TOEJE1_" + i + " +");
			sRedu.append(" P.REDU1_" + i + " +");
			sCompro.append(" P.COMPRO1_" + i + " +");
		}

		sAmpli.insert(0, "SUM(");
		sRedu.insert(0, "SUM(");
		sEjpa.insert(0, "SUM(");
		sEjxpa.insert(0, "SUM(");
		sToeje.insert(0, "SUM(");
		sCompro.insert(0, "SUM(");

		sQuery.append(sAuto).append(sAmpli.substring(0, sAmpli.length() - 1)).append(") AMPLI1, ")
				.append(sEjpa.substring(0, sEjpa.length() - 1)).append(") EJPA1, ")
				.append(sEjxpa.substring(0, sEjxpa.length() - 1)).append(") EJXPA1, ")
				.append(sToeje.substring(0, sToeje.length() - 1)).append(") TOEJE1, ")
				.append(sRedu.substring(0, sRedu.length() - 1)).append(") REDU1, ")
				.append(sCompro.substring(0, sCompro.length() - 1)).append(") COMPRO1 ").append(sFoot);

		return sQuery.toString();
	}

	/**
	 * Gets the mes.
	 *
	 * @return the mes
	 */
	public String getMes() {
		return mes;
	}

	/**
	 * Sets the mes.
	 *
	 * @param mes
	 *            the new mes
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}

	/**
	 * Gets the fuente F.
	 *
	 * @return the fuente F
	 */
	public Fuentef getFuenteF() {
		return fuenteF;
	}

	/**
	 * Sets the fuente F.
	 *
	 * @param fuenteF
	 *            the new fuente F
	 */
	public void setFuenteF(Fuentef fuenteF) {
		this.fuenteF = fuenteF;
	}

	/**
	 * Gets the list mes.
	 *
	 * @return the list mes
	 */
	public List<TcMes> getListMes() {
		return listMes;
	}

	/**
	 * Sets the list mes.
	 *
	 * @param listMes
	 *            the new list mes
	 */
	public void setListMes(List<TcMes> listMes) {
		this.listMes = listMes;
	}

	/**
	 * Gets the list fuentef.
	 *
	 * @return the list fuentef
	 */
	public List<Fuentef> getListFuentef() {
		return listFuentef;
	}

	/**
	 * Sets the list fuentef.
	 *
	 * @param listFuentef
	 *            the new list fuentef
	 */
	public void setListFuentef(List<Fuentef> listFuentef) {
		this.listFuentef = listFuentef;
	}

	/**
	 * Gets the tcmes repository.
	 *
	 * @return the tcmes repository
	 */
	public TcMesRepository getTcmesRepository() {
		return tcmesRepository;
	}

	/**
	 * Sets the tcmes repository.
	 *
	 * @param tcmesRepository
	 *            the new tcmes repository
	 */
	public void setTcmesRepository(TcMesRepository tcmesRepository) {
		this.tcmesRepository = tcmesRepository;
	}

	/**
	 * Gets the fuentef repository.
	 *
	 * @return the fuentef repository
	 */
	public FuentefRepository getFuentefRepository() {
		return fuentefRepository;
	}

	/**
	 * Sets the fuentef repository.
	 *
	 * @param fuentefRepository
	 *            the new fuentef repository
	 */
	public void setFuentefRepository(FuentefRepository fuentefRepository) {
		this.fuentefRepository = fuentefRepository;
	}

	public void downloadXls() throws ReportValidationException {
		if (this.validatePolicyService.isOpenMonth(Integer.valueOf(mes), null,
				this.getUserDetails().getIdSector()) == Boolean.TRUE) {
			RequestContext.getCurrentInstance().execute(DOWNLOAD_XLS);
		}
	}

	public void validateMonth() {
		this.validatePolicyService.isOpenMonth(Integer.valueOf(mes), null, this.getUserDetails().getIdSector());
	}

	public void viewPdf() {
		if (this.validatePolicyService.isOpenMonth(Integer.valueOf(mes), null,
				this.getUserDetails().getIdSector()) == Boolean.TRUE) {
			this.createFilePdfInline();

		}
	}

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

}
