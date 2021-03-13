package br.ufc.quixada.npi.gestaocompetencia.model;

import java.util.Date;

import javax.validation.constraints.NotEmpty;


public class DadosPessoais {
	
	@NotEmpty
	protected String nome;
	
	private String nomeSocial;
	private Date dataNasc;
	private String sexo;
	private String deficiencia;
	private String endereco;
	private String telefone;
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getNomeSocial() {
		return nomeSocial;
	}
	
	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}
	
	public Date getDataNasc() {
		return dataNasc;
	}
	
	public void setDataNasc(Date dataNasc) {
		this.dataNasc = dataNasc;
	}
	
	public String getSexo() {
		return sexo;
	}
	
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	
	public String getDeficiencia() {
		return deficiencia;
	}
	
	public void setDeficiencia(String deficiencia) {
		this.deficiencia = deficiencia;
	}
	
	public String getEndereco() {
		return endereco;
	}
	
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	
	public String getTelefone() {
		return telefone;
	}
	
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	
	
}
