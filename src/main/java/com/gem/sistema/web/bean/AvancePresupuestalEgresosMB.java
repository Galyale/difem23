package com.gem.sistema.web.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import com.gem.sistema.business.domain.Conctb;
import com.gem.sistema.business.domain.TcPeriodo;
import com.gem.sistema.business.domain.TrPuestoFirma;
import com.gem.sistema.business.repository.catalogs.ConctbRepository;
import com.gem.sistema.business.repository.catalogs.FirmasRepository;
import com.gem.sistema.business.repository.catalogs.TcPeriodoRepositoy;
import com.gem.sistema.business.service.catalogos.PuestosFirmasService;
import com.gem.sistema.business.service.reportador.ReportValidationException;
import com.gem.sistema.util.ConstantsClaveEnnum;

import static com.roonin.utils.UtilDate.getLastDayByAnoEmp;

@ViewScoped
@ManagedBean(name = "avancePresupuestalEgresosMB")
public class AvancePresupuestalEgresosMB extends BaseDirectReport {
	private List<TcPeriodo> listMeses;
	private Integer tipoReporte;
	private Integer mes;

	@ManagedProperty("#{tcPeriodoRepositoy}")
	private TcPeriodoRepositoy periodoRepositoy;

	@ManagedProperty("#{firmasRepository}")
	private FirmasRepository firmasRepository;
	
	@ManagedProperty("#{conctbRepository}")
	private ConctbRepository conctbRepository;

	@ManagedProperty("#{puestosFirmasService}")
	private PuestosFirmasService puestosFirmasService;

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

	@PostConstruct
	public void init() {
		jasperReporteName = "AvancePresupuestalEgresos";
		endFilename = jasperReporteName + ".pdf";
		tipoReporte = 4;

		listMeses = periodoRepositoy.findByTipoPeriodo(1);
		if (!listMeses.isEmpty()) {
			mes = listMeses.get(0).getPeriodo();
		}
	}

	@Override
	public Map<String, Object> getParametersReports() throws ReportValidationException {
		Map<String, Object> params = new HashMap<String, Object>();
		TcPeriodo periodo = periodoRepositoy.findByTipoPeriodoAndPeriodo(1, mes);
		Integer idSector = this.getUserDetails().getIdSector();
		Conctb conctb = conctbRepository.findByIdsector(idSector);
		TrPuestoFirma firma = new TrPuestoFirma();
		firma = puestosFirmasService.getFirmaBySectorAnioClave(idSector, 0L, ConstantsClaveEnnum.CLAVE_F07.getValue());
		params.put("p_L1", firma.getPuesto().getPuesto());
		params.put("p_N1", firma.getNombre());
		firma = puestosFirmasService.getFirmaBySectorAnioClave(idSector, 0L, ConstantsClaveEnnum.CLAVE_F08.getValue());
		params.put("p_L2",  firma.getPuesto().getPuesto());
		params.put("p_N2", firma.getNombre());
		firma = puestosFirmasService.getFirmaBySectorAnioClave(idSector, 0L, ConstantsClaveEnnum.CLAVE_F09.getValue());
		params.put("p_L3",  firma.getPuesto().getPuesto());
		params.put("p_N3", firma.getNombre());
		firma = puestosFirmasService.getFirmaBySectorAnioClave(idSector, 0L, ConstantsClaveEnnum.CLAVE_F10.getValue());
		params.put("p_L4",  firma.getPuesto().getPuesto());
		params.put("p_N4", firma.getNombre());
		params.put("pEntidadName", conctb.getNomDep());
		params.put("pEntidadRFC", conctb.getRfc());
		params.put("pImagenPath", conctb.getImagePathLeft());
		params.put("pImagenPath2", conctb.getImagePathRigth());
		params.put("pLastDay", getLastDayByAnoEmp(mes, conctb.getAnoemp()));
		params.put("pMesName", periodo.getDescripcion());
		params.put("pYear", conctb.getAnoemp());
		params.put("pSql", this.generateQuery());
		params.put("p_IdSector", idSector);

		return params;
	}

	@Override
	public StreamedContent generaReporteSimple(int type) throws ReportValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	private String generateQuery() {
		String clave = "";
		switch (tipoReporte) {
		case 4:
			clave = " SUBSTR(P.PROGRAMA,1,4) ";
			break;
		case 8:
			clave = " SUBSTR(P.PROGRAMA,1,8) ";
			break;
		case 12:
			clave = " SUBSTR(P.PROGRAMA,1,12) ";
			break;
		default:
			clave = " '' ";
			break;
		}

		StringBuilder sql = new StringBuilder();
		StringBuilder redu = new StringBuilder();
		StringBuilder ejpa = new StringBuilder();
		StringBuilder ampli = new StringBuilder();
		StringBuilder ejxpa = new StringBuilder();
		StringBuilder compro = new StringBuilder();

		for (int i = 1; i <= mes; i++) {
			redu.append(" P.REDU1_").append(i).append(" + ");
			ejpa.append(" P.EJPA1_").append(i).append(" + ");
			ampli.append(" P.AMPLI1_").append(i).append(" + ");
			ejxpa.append(" P.EJXPA1_").append(i).append(" + ");
			compro.append(" P.COMPRO1_").append(i).append(" + ");
		}

		sql.append("SELECT CLAVE, NOMBRE, AUTORIZADO, AMPLIACION, ")
				.append(" REDUCCION, (AUTORIZADO + AMPLIACION - REDUCCION) MODIFICADO,")
				.append(" COMPROMETIDO, DEVENGADO, PAGADO, (COMPROMETIDO + DEVENGADO + PAGADO) EJERCIDO")
				.append(" FROM (").append(" SELECT ").append(clave).append(" CLAVE, M.CAMPO6 NOMBRE, ")
				.append(" SUM(P.AUTO1_1 + P.AUTO1_2 + P.AUTO1_3 + P.AUTO1_4 + P.AUTO1_5 + P.AUTO1_6 + ")
				.append("P.AUTO1_7 + P.AUTO1_8 + P.AUTO1_9 + P.AUTO1_10 + P.AUTO1_11 + P.AUTO1_12 ) AUTORIZADO,")
				.append(" SUM(").append(ampli.substring(0, ampli.length() - 2)).append(") AMPLIACION, ").append(" SUM(")
				.append(redu.substring(0, redu.length() - 2)).append(") REDUCCION,").append(" SUM(")
				.append(compro.substring(0, compro.length() - 2)).append(") COMPROMETIDO,").append(" SUM(")
				.append(ejxpa.substring(0, ejxpa.length() - 2)).append(") DEVENGADO,").append(" SUM(")
				.append(ejpa.substring(0, ejpa.length() - 2)).append(") PAGADO").append(" FROM PASO P")
				.append(" INNER JOIN MUNINEP M ON M.IDSECTOR = P.IDSECTOR AND M.CAMPO7 =").append(clave)
				.append(" WHERE P.IDSECTOR = ").append(this.getUserDetails().getIdSector())
				.append(" AND SUBSTR(P.PARTIDA,4,1) <> 0 GROUP BY ").append(clave).append(", M.CAMPO6").append(" ) T1 ")
				.append("WHERE AMPLIACION+REDUCCION+COMPROMETIDO+DEVENGADO+PAGADO+AUTORIZADO<>0 ORDER BY CLAVE");

		return sql.toString();
	}

	public List<TcPeriodo> getListMeses() {
		return listMeses;
	}

	public void setListMeses(List<TcPeriodo> listMeses) {
		this.listMeses = listMeses;
	}

	public Integer getTipoReporte() {
		return tipoReporte;
	}

	public void setTipoReporte(Integer tipoReporte) {
		this.tipoReporte = tipoReporte;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public TcPeriodoRepositoy getPeriodoRepositoy() {
		return periodoRepositoy;
	}

	public FirmasRepository getFirmasRepository() {
		return firmasRepository;
	}

	public void setFirmasRepository(FirmasRepository firmasRepository) {
		this.firmasRepository = firmasRepository;
	}

	public void setPeriodoRepositoy(TcPeriodoRepositoy periodoRepositoy) {
		this.periodoRepositoy = periodoRepositoy;
	}

}
