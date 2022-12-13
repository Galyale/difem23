package com.gem.sistema.web.bean;

import static com.gem.sistema.util.Constants.ZERO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.SelectEvent;

import com.gem.sistema.business.domain.Cuenta;
import com.gem.sistema.business.dto.MatrizIngreso;
import com.gem.sistema.business.repository.catalogs.CuentaRepository;

@ManagedBean(name = "matrizIngresosMB")
@ViewScoped
public class MatrizIngresosMB extends AbstractMB {

	private Cuenta cuenta = new Cuenta();
	/** Array principal */
	private List<Cuenta> cuentas = new ArrayList<>();
	private List<MatrizIngreso> movimientos = new ArrayList<>();
	private List<Cuenta> cuentasDevengado = new ArrayList<Cuenta>();
	private List<Cuenta> cuentasRecaudado = new ArrayList<Cuenta>();
	private String busqueda = "";

	@ManagedProperty("#{cuentaRepository}")
	private CuentaRepository cuentaRepository;

	@PostConstruct
	public void init() {
		movimientos.add(new MatrizIngreso("DEVENGADO", "N", "", "A", "X", "", "", "X", "", ""));
		movimientos.add(new MatrizIngreso("RECAUDADO", "N", "", "A", "", "", "X", "", "", "X"));
		movimientos.add(new MatrizIngreso("SIMULTANEOS", "A", "", "", "X", "", "X", "X", "", "X"));
		buscar();
		cuenta = cuentas.isEmpty() ? new Cuenta() : cuentas.get(0);
		getAndSetCuentas8XXX();
	}

	public void buscar() {
		if (busqueda == null || busqueda.equals("")) {
			cuentas = cuentaRepository.findWhereCtaStartWithAndSssctaNotEmptyAndIdSectorOrdered("4",
					new Long(this.getUserDetails().getIdSector()));
		} else {
			if (busqueda.startsWith("4")) {
				cuentas = cuentaRepository.findWhereCtaStartWithAndSssctaNotEmptyAndIdSectorOrdered(busqueda,
						new Long(this.getUserDetails().getIdSector()));
			} else {
				cuentas = new ArrayList<Cuenta>();
			}
		}
	}

	private void getAndSetCuentas8XXX() {

		List<Cuenta> cuentasBySelected = cuentaRepository.find8120And8140And8150By4xxx(
				StringUtils.leftPad(cuenta.getCuenta(), 10, StringUtils.EMPTY + ZERO),
				StringUtils.leftPad(cuenta.getScuenta(), 15, StringUtils.EMPTY + ZERO),
				cuenta.getSscuenta().substring(11, cuenta.getSscuenta().length()),
				StringUtils.leftPad(cuenta.getSsscuenta(), 4, StringUtils.EMPTY + ZERO),
				new Long(this.getUserDetails().getIdSector()));
		cuentasBySelected.add(0, cuenta);
		Cuenta temporal = new Cuenta();
		temporal.setCuenta("1112");
		cuentasBySelected.add(0, temporal);
		cuentasDevengado = cuentasBySelected;
		cuentasDevengado.add(
				cuentasBySelected.stream().filter(c -> c.getCuenta().equals("8140")).findFirst().orElse(new Cuenta()));
		cuentasDevengado = cuentasDevengado.stream().sorted(Comparator.comparing(Cuenta::getCuenta))
				.collect(Collectors.toList());

	}

	public void rowSelected(SelectEvent event) {
		cuenta = (Cuenta) event.getObject();
		getAndSetCuentas8XXX();
	}

	public Cuenta getCuenta() {
		return cuenta;
	}

	public void setCuenta(Cuenta cuenta) {
		this.cuenta = cuenta;
	}

	public List<Cuenta> getCuentas() {
		return cuentas;
	}

	public void setCuentas(List<Cuenta> cuentas) {
		this.cuentas = cuentas;
	}

	public String getBusqueda() {
		return busqueda;
	}

	public void setBusqueda(String busqueda) {
		this.busqueda = busqueda;
	}

	public CuentaRepository getCuentaRepository() {
		return cuentaRepository;
	}

	public void setCuentaRepository(CuentaRepository cuentaRepository) {
		this.cuentaRepository = cuentaRepository;
	}

	public List<MatrizIngreso> getMovimientos() {
		return movimientos;
	}

	public void setMovimientos(List<MatrizIngreso> movimientos) {
		this.movimientos = movimientos;
	}

	public List<Cuenta> getCuentasDevengado() {
		return cuentasDevengado;
	}

	public void setCuentasDevengado(List<Cuenta> cuentasDevengado) {
		this.cuentasDevengado = cuentasDevengado;
	}

	public List<Cuenta> getCuentasRecaudado() {
		return cuentasRecaudado;
	}

	public void setCuentasRecaudado(List<Cuenta> cuentasRecaudado) {
		this.cuentasRecaudado = cuentasRecaudado;
	}

}

class MatrizIngresoCuenta extends Cuenta {
	private String movimiento;

	public String getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(String movimiento) {
		this.movimiento = movimiento;
	}

}