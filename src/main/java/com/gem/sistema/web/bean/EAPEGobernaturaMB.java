package com.gem.sistema.web.bean;

import static com.roonin.utils.UtilDate.getLastDayByAnoEmp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import com.gem.sistema.business.domain.Conctb;

import com.gem.sistema.business.domain.TcPeriodo;
import com.gem.sistema.business.domain.TrPuestoFirma;
import com.gem.sistema.business.repository.catalogs.ConctbRepository;
import com.gem.sistema.business.repository.catalogs.TcPeriodoRepositoy;
import com.gem.sistema.business.service.catalogos.PuestosFirmasService;
import com.gem.sistema.business.service.catalogos.ValidatePolicyService;
import com.gem.sistema.business.service.reportador.ReportValidationException;
import com.gem.sistema.util.ConstantsClaveEnnum;

/**
 * @author alfredo
 *
 */
@ManagedBean(name = "eAPEGobernaturaMB")
@ViewScoped
public class EAPEGobernaturaMB extends BaseDirectReport {

	private static final String DOWNLOAD_XLS = " jQuery('#form1\\\\:downXls').click();";
	private Integer mes;
	private List<TcPeriodo> listMes;

	@ManagedProperty("#{tcPeriodoRepositoy}")
	private TcPeriodoRepositoy periodoRepositoy;


	@ManagedProperty("#{validatePolicyService}")
	private ValidatePolicyService validatePolicyService;

	@ManagedProperty("#{puestosFirmasService}")
	private PuestosFirmasService puestosFirmasService;

	@ManagedProperty("#{conctbRepository}")
	private ConctbRepository conctbRepository;


	@PostConstruct
	public void init() {
		jasperReporteName = "RF009122_EdoAvancePEfin";
		endFilename = "APE_Gobernatura.pdf";
		listMes = periodoRepositoy.findByTipoPeriodo(1);
		if (!CollectionUtils.isEmpty(listMes)) {
			mes = listMes.get(0).getPeriodo();
		}
	}

	public void generatePDF() {
		if (this.validatePolicyService.isOpenMonth(Integer.valueOf(mes), null,
				this.getUserDetails().getIdSector()) == Boolean.TRUE) {
			this.validatePolicyService.isAfectMonth(Integer.valueOf(mes), null, this.getUserDetails().getIdSector());
			this.validatePolicyService.existPolices(Integer.valueOf(mes), null, this.getUserDetails().getIdSector());

			this.createFilePdfInline();
		}
	}

	public void downloadXls() {
		if (this.validatePolicyService.isOpenMonth(Integer.valueOf(mes), null,
				this.getUserDetails().getIdSector()) == Boolean.TRUE) {
			this.validatePolicyService.isAfectMonth(Integer.valueOf(mes), null, this.getUserDetails().getIdSector());
			this.validatePolicyService.existPolices(Integer.valueOf(mes), null, this.getUserDetails().getIdSector());

			RequestContext.getCurrentInstance().execute(DOWNLOAD_XLS);
		}
	}

	@Override
	public Map<String, Object> getParametersReports() throws ReportValidationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		TcPeriodo periodo = periodoRepositoy.findByTipoPeriodoAndPeriodo(1, mes);
		Integer sector = this.getUserDetails().getIdSector();
		TrPuestoFirma firma = null;
		Conctb conctb = conctbRepository.findByIdsector(this.getUserDetails().getIdSector());

		parameters.put("p_IdSector", sector);
		parameters.put("p_Mes", Integer.valueOf(mes));
		parameters.put("p_NombreFin", "GOBERNATURA");
		parameters.put("p_NumFin", StringUtils.EMPTY);
		parameters.put("p_Mun", this.getUserDetails().getMunicipio());

		firma = puestosFirmasService.getFirmaBySectorAnioClave(sector, 0L, ConstantsClaveEnnum.CLAVE_F07.getValue());
		parameters.put("p_L1", firma.getPuesto().getPuesto());
		parameters.put("p_N1", firma.getNombre());
		firma = puestosFirmasService.getFirmaBySectorAnioClave(sector, 0L, ConstantsClaveEnnum.CLAVE_F08.getValue());
		parameters.put("p_L2", firma.getPuesto().getPuesto());
		parameters.put("p_N2", firma.getNombre());
		firma = puestosFirmasService.getFirmaBySectorAnioClave(sector, 0L, ConstantsClaveEnnum.CLAVE_F09.getValue());
		parameters.put("p_L3", firma.getPuesto().getPuesto());
		parameters.put("p_N3", firma.getNombre());
		firma = puestosFirmasService.getFirmaBySectorAnioClave(sector, 0L, ConstantsClaveEnnum.CLAVE_F10.getValue());
		parameters.put("p_L4", firma.getPuesto().getPuesto());
		parameters.put("p_N4", firma.getNombre());
		
		parameters.put("p_Img", conctb.getImagePathLeft());
		parameters.put("p_Img2", conctb.getImagePathRigth());
		parameters.put("p_Query", this.generateQuery(sector, mes));
		parameters.put("p_An", conctb.getAnoemp());
		parameters.put("p_LastDay", getLastDayByAnoEmp(mes, conctb.getAnoemp()));
		parameters.put("p_DescrpMes", periodo.getDescripcion().toUpperCase());
		parameters.put("p_Dependencia", conctb.getNomDep());

		return parameters;
	}

	@Override
	public StreamedContent generaReporteSimple(int type) throws ReportValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	public String generateQuery(Integer idsector, Integer mes) {
		StringBuilder sSql = new StringBuilder();
		StringBuilder ejempa = new StringBuilder();
		StringBuilder ejxpa = new StringBuilder();
		StringBuilder toeje = new StringBuilder();
		StringBuilder ampli = new StringBuilder();
		StringBuilder redu = new StringBuilder();
		StringBuilder compro = new StringBuilder();

		sSql.append("SELECT T1.PARTIDA, ").append("T1.DESCRIPCION,").append("T1.A APROBADO,").append("T1.F AMPLIACION,")
				.append("T1.G REDUCCION,").append("(T1.A+T1.F-T1.G) MODIFICADO,").append("(T1.CPT)COMPROMETIDO,")
				.append("T1.C DEVENGADO,").append("T1.B PAGADO,").append("T1.D EJERCIDO,")
				.append("((T1.A +T1.F-T1.G)-D) POR_EJERCER").append(" FROM (SELECT P.PARTIDA PARTIDA,")
				.append("N.NOMGAS DESCRIPCION,")
				.append("SUM(P.AUTO1_1+P.AUTO1_2+P.AUTO1_3+P.AUTO1_4+P.AUTO1_5+P.AUTO1_6+P.AUTO1_7+P.AUTO1_8+P.AUTO1_9+P.AUTO1_10+P.AUTO1_11+P.AUTO1_12)A,");

		for (int i = 1; i <= mes; i++) {

			ejempa.append(" P.EJPA1_" + i + "+");
			ejxpa.append(" P.EJXPA1_" + i + "+");
			toeje.append(" P.TOEJE1_" + i + "+");
			ampli.append(" P.AMPLI1_" + i + "+");
			redu.append(" P.REDU1_" + i + "+");
			compro.append(" P.COMPRO1_" + i + "+");

		}

		sSql.append(" SUM( ").append(ejempa.substring(1, ejempa.length() - 1)).append(")B,").append(" SUM( ")
				.append(ejxpa.substring(1, ejxpa.length() - 1)).append(")C,").append(" SUM( ")
				.append(toeje.substring(1, toeje.length() - 1)).append(")D,").append(" SUM( ")
				.append(ampli.substring(1, ampli.length() - 1)).append(")F,").append(" SUM( ")
				.append(redu.substring(1, redu.length() - 1)).append(")G,").append(" SUM( ")
				.append(compro.substring(1, compro.length() - 1)).append(")CPT ");

		sSql.append(" FROM").append(" PASO P JOIN NATGAS N ").append(" ON N.CLVGAS= P.PARTIDA ")
				.append(" AND N.IDSECTOR=P.IDSECTOR ").append(" WHERE ").append(" P.IDSECTOR = " + idsector)
				.append(" AND SUBSTR(P.CLAVE, 1, 3) IN ('200', '201', '202', '2A0') GROUP BY P.PARTIDA,N.NOMGAS)")
				.append("T1 WHERE A+B+C+D+F+G+CPT<>0");
		return sSql.toString();
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public List<TcPeriodo> getListMes() {
		return listMes;
	}

	public void setListMes(List<TcPeriodo> listMes) {
		this.listMes = listMes;
	}

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
