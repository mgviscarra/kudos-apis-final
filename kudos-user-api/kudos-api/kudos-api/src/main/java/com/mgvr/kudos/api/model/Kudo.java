package com.mgvr.kudos.api.model;



import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

@Document(collection= "kudo")
public class Kudo {
	
	@Id
	private long id;
	private String fuente;
	private String destino;
	private String tema;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyyy")
	private Date fecha;
	private String lugar;
	private String texto;

	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}
	public String getFuente() {
		return fuente;
	}
	public void setFuente(String fuente) {
		this.fuente = fuente;
	}
	public String getDestino() {
		return destino;
	}
	public void setDestino(String destino) {
		this.destino = destino;
	}
	public String getTema(){return tema;}
	public void setTema(String tema){this.tema=tema;}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public void setLugar(String lugar) {
		this.lugar = lugar;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}	

	
}
