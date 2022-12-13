package com.gem.sistema.business.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The persistent class for the TC_NIVEL database table.
 * 
 * @author Alfredo Neri
 *
 */
@Entity
@Table(name = "TC_NIVEL")
@NamedQuery(name = "TcNivel.findAll", query = "SELECT t FROM TcNivel t")
public class TcNivel extends AbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "ID_SUB_NIVEL")
	private Long idSubNivel;

	private String clave;

	private String nombre;

	public TcNivel() {
	}

	public Long getIdSubNivel() {
		return this.idSubNivel;
	}

	public void setIdSubNivel(Long idSubNivel) {
		this.idSubNivel = idSubNivel;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

}