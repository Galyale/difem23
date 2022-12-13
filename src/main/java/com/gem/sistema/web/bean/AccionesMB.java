package com.gem.sistema.web.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;

import com.gem.sistema.business.domain.TcAccion;
import com.gem.sistema.business.service.catalogos.AccionesService;

@ManagedBean(name = "accionesMB")
@ViewScoped
public class AccionesMB {

	private List<TcAccion> acciones;

	private TcAccion selectedAccion;

	private TcAccion accion;

	private String search = StringUtils.EMPTY;
	private String titleModal = StringUtils.EMPTY;
	private Boolean edition = Boolean.FALSE;

	@ManagedProperty(value = "#{accionesService}")
	private AccionesService accionesService;

	@PostConstruct
	public void init() {
		acciones = accionesService.listadoAcciones();
		this.search = StringUtils.EMPTY;
	}

	public void editRows() {
		titleModal = "Modificar Acción";
		this.accion = this.selectedAccion;
		this.edition = Boolean.TRUE;
	}

	public void addRow() {
		titleModal = "Nueva Acción";
		this.edition = Boolean.FALSE;
		selectedAccion = new TcAccion();
		this.accion = new TcAccion();
	}

	public void saveAccion() {

		if (!edition) {
			selectedAccion = accion;
		}

		if (isValid(selectedAccion)) {

			accionesService.saveAccion(selectedAccion);
			acciones = accionesService.listadoAcciones();
			displayInfoMessage("Se ha guardado la información correctamente");
			RequestContext.getCurrentInstance().execute("PF('carDialog').hide();");
			this.accion = new TcAccion();
		} else {
			displayInfoMessage("Favor de completar la Información de la Acción.");
		}
	}

	public void deleteAccion() {
		accionesService.deleteAccion(selectedAccion);
		acciones = accionesService.listadoAcciones();
		displayInfoMessage("Se ha eliminado correctamente la Acción.");
	}

	public void searchAccion() {
		
		if(StringUtils.isNotEmpty(search)) 
			acciones = accionesService.searchAcciones(search);
		else
			acciones = accionesService.listadoAcciones();
		
	}

	public void calcelRequest() {

		if (selectedAccion.getId() == null) {
			selectedAccion = new TcAccion();
		}

	}

	private Boolean isValid(TcAccion accion) {
		List<String> erros = new ArrayList<String>();
		if (accion == null)
			return false;
		if (StringUtils.isEmpty(accion.getClave()))
			erros.add("La clave es requerida");
		if (StringUtils.isEmpty(accion.getNombre()))
			erros.add("El nombre es requerido");
		if (StringUtils.isEmpty(accion.getDescripcion()))
			erros.add("La descripción es requerido");

		erros.forEach(this::displayInfoMessage);
		return erros.isEmpty();
	}

	public void displayInfoMessage(String str) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", str);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public List<TcAccion> getAcciones() {
		return acciones;
	}

	public void setAcciones(List<TcAccion> acciones) {
		this.acciones = acciones;
	}

	public TcAccion getSelectedAccion() {
		return selectedAccion;
	}

	public void setSelectedAccion(TcAccion selectedAccion) {
		this.selectedAccion = selectedAccion;
	}

	public TcAccion getAccion() {
		return accion;
	}

	public void setAccion(TcAccion accion) {
		this.accion = accion;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getTitleModal() {
		return titleModal;
	}

	public void setTitleModal(String titleModal) {
		this.titleModal = titleModal;
	}

	public Boolean getEdition() {
		return edition;
	}

	public void setEdition(Boolean edition) {
		this.edition = edition;
	}

	public AccionesService getAccionesService() {
		return accionesService;
	}

	public void setAccionesService(AccionesService accionesService) {
		this.accionesService = accionesService;
	}

}
