package com.gem.sistema.business.domain;

import java.io.Serializable;
import javax.persistence.*;

import com.gem.sistema.annotation.IgnoredQuery;

/**
 * The persistent class for the TC_AREAS database table.
 * 
 */
@Entity
@Table(name = "TC_AREAS")
@NamedQuery(name = "TcArea.findAll", query = "SELECT t FROM TcArea t")
public class TcArea extends AbstractEntity implements Serializable {

	@IgnoredQuery
	private static final long serialVersionUID = 1L;

	private Integer clave;

	private String descripcion;

	@OneToOne(mappedBy = "area", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
	private TrPresupuestoArea presupuestoArea;

	public TcArea() {
	}

	public Integer getClave() {
		return this.clave;
	}

	public void setClave(Integer clave) {
		this.clave = clave;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public TrPresupuestoArea getPresupuestoArea() {
		return presupuestoArea;
	}

	public void setPresupuestoArea(TrPresupuestoArea presupuestoArea) {
		this.presupuestoArea = presupuestoArea;
	}

	@Override
	public String toString() {
		return "TcArea [clave=" + clave + ", descripcion=" + descripcion + "]";
	}

}