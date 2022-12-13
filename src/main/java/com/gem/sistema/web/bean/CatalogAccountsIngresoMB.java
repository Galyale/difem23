package com.gem.sistema.web.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.StreamedContent;
import com.gem.sistema.business.domain.Cuenta;
import com.gem.sistema.business.repository.catalogs.CuentaRepository;
import com.gem.sistema.business.service.catalogos.AccountService;
import com.gem.sistema.business.service.reportador.ReportValidationException;
import com.gem.sistema.web.util.FrontProperty;

// TODO: Auto-generated Javadoc
/**
 * The Class CatalogAccountsMB2.
 */
@ManagedBean
@ViewScoped
public class CatalogAccountsIngresoMB extends BaseDirectReport {

	/** Longitud del primer nivel de cuentas. */
	public static final int LENGTH_FIRST_LEVEL = 4;

	/** Longitud del segundo nivel de cuentas. */
	public static final int LENGTH_SECOND_LEVEL = 10;

	/** Longitud del tercer nivel de cuentas. */
	public static final int LENGTH_THIRD_LEVEL = 15;

	/** Longitud del cuarto nivel de cuentas. */
	public static final int LENGTH_FOUR_LEVEL = 4;

	/** Longitud del tercer nivel de cuentas. */
	public static final int LENGTH_FIVE_LEVEL = 4;
	protected static final String PHEADER1 = FrontProperty.getPropertyValue("pHeader1");

	/** The Constant PHEADER2. */
	protected static final String PHEADER2 = FrontProperty.getPropertyValue("pHeader2");

	/** The Constant PHEADER3. */
	protected static final String PHEADER3 = FrontProperty.getPropertyValue("pHeader3");

	/** The Constant PHEADER4. */
	protected static final String PHEADER4 = FrontProperty.getPropertyValue("pHeader4");
	private List<Cuenta> cuentas;
	private List<Cuenta> cuentasOmitidas;
	private Cuenta cuenta;

	/** Servicios para el modulo de centas. */
	@ManagedProperty(value = "#{accountService}")
	private AccountService accountService;

	/**
	 * Repositorio de Cuenta.
	 */
	@ManagedProperty(value = "#{cuentaRepository}")
	private CuentaRepository cuentaRepository;

	/** Ruta donde se encuentra el archivo jasper del reporte de cuentas. */
	// @Value("${view.report.path.jasper.cuentas}")
	private static final String REPORT_PATH_JASPER_ACCOUNTS = FrontProperty
			.getPropertyValue("view.report.path.jasper.cuentas");

	/**
	 * Inicializa los objetos.
	 */
	@PostConstruct
	public void init() {
		jasperReporteName = REPORT_PATH_JASPER_ACCOUNTS;
		jasperReporteName = jasperReporteName.replace(".jasper", "");
		endFilename = jasperReporteName + ".pdf";
		cuentasOmitidas = new ArrayList<Cuenta>();
		cuentasOmitidas.add(new Cuenta("4399", "0000000001", "000000000000001", "0009"));
		reset();
	}

	private String getCondiciones() {
		String cond = " WHERE SUBSTR(C.cuenta,1,1)=4 ";
		validateAndAddFillZeros();
		if (cuenta.getCuenta() != null && !cuenta.getCuenta().equals("")) {
			cond += " AND C.cuenta='" + cuenta.getCuenta() + "'";
		}
		if (!cuenta.getScuenta().equals("")) {
			cond += " AND C.scuenta='" + cuenta.getScuenta() + "'";
		}
		if (!cuenta.getSscuenta().equals("")) {
			cond += " AND C.sscuenta='" + cuenta.getSscuenta() + "'";
		}
		if (!cuenta.getSsscuenta().equals("")) {
			cond += " AND C.ssscuenta='" + cuenta.getSsscuenta() + "'";
		}
		if (!cuenta.getSssscuenta().equals("")) {
			cond += " AND C.sssscuenta='" + cuenta.getSssscuenta() + "'";
		}
		if (cuenta.getNomcta() != null && !cuenta.getNomcta().equals("")) {
			cond += " AND C.nomcta='" + cuenta.getNomcta() + "'";
		}
		String omitidas = "";
		for (int i = 0; i < cuentasOmitidas.size(); i++) {
			omitidas += " AND C.cuenta||C.scuenta||C.sscuenta||C.ssscuenta||C.sssscuenta <> '"
					+ cuentasOmitidas.get(i).getCuenta() + cuentasOmitidas.get(i).getScuenta()
					+ cuentasOmitidas.get(i).getSscuenta() + cuentasOmitidas.get(i).getSsscuenta() + "'";
		}

		cond += omitidas + "AND C.cuenta<> '4311' ";
		String query = "Select C FROM Cuenta C " + cond
				+ " ORDER BY C.cuenta,C.scuenta,C.sscuenta,C.ssscuenta,C.sssscuenta ";
		return query;
	}

	public void buscar() {
		cuentas = accountService.getByQuery(getCondiciones());

	}

	public void reset() {
		cuenta = new Cuenta();
		buscar();
	}

	private void validateAndAddFillZeros() {
		if (StringUtils.isNotEmpty(cuenta.getScuenta())) {
			cuenta.setScuenta(accountService.fillZeros(cuenta.getScuenta(), LENGTH_SECOND_LEVEL));
		} else {
			cuenta.setScuenta(StringUtils.EMPTY);
		}
		if (StringUtils.isNotEmpty(cuenta.getSscuenta())) {
			cuenta.setSscuenta(accountService.fillZeros(cuenta.getSscuenta(), LENGTH_THIRD_LEVEL));
		} else {
			cuenta.setSscuenta(StringUtils.EMPTY);
		}
		if (StringUtils.isNotEmpty(cuenta.getSsscuenta())) {
			cuenta.setSsscuenta(accountService.fillZeros(cuenta.getSsscuenta(), LENGTH_FOUR_LEVEL));
		} else {
			cuenta.setSsscuenta(StringUtils.EMPTY);
		}

		if (StringUtils.isNotEmpty(cuenta.getSssscuenta())) {
			cuenta.setSssscuenta(accountService.fillZeros(cuenta.getSssscuenta(), LENGTH_FIVE_LEVEL));
		} else {
			cuenta.setSssscuenta(StringUtils.EMPTY);
		}

	}

	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

	public CuentaRepository getCuentaRepository() {
		return cuentaRepository;
	}

	public void setCuentaRepository(CuentaRepository cuentaRepository) {
		this.cuentaRepository = cuentaRepository;
	}

	@Override
	public Map<String, Object> getParametersReports() throws ReportValidationException {
		String condiciones = getCondiciones();
		condiciones = condiciones.replace("scuenta", "scta");
		condiciones = condiciones.replace("sscuenta", "sscta");
		condiciones = condiciones.replace("ssscuenta", "ssscta");
		condiciones = condiciones.replace("sssscuenta", "sssscta");
		System.out.println(condiciones);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pWhereClause", condiciones);
		params.put("pLogoName", this.getUserDetails().getPathImgCab1());
		params.put("pTitulo1", StringUtils.EMPTY);
		if (this.getUserDetails().getIdSector() == 2) {
			params.put("pTitulo2", StringUtils.EMPTY);
			params.put("pHeader1", PHEADER1);
			params.put("pHeader2", PHEADER2);
			params.put("pHeader3", PHEADER3);
			params.put("pHeader4", PHEADER4);
		}
		return params;
	}

	@Override
	public StreamedContent generaReporteSimple(int type) throws ReportValidationException {

		return null;
	}

	public List<Cuenta> getCuentas() {
		return cuentas;
	}

	public void setCuentas(List<Cuenta> cuentas) {
		this.cuentas = cuentas;
	}

	public Cuenta getCuenta() {
		return cuenta;
	}

	public void setCuenta(Cuenta cuenta) {
		this.cuenta = cuenta;
	}

}
