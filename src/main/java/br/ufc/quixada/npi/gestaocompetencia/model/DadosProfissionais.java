package br.ufc.quixada.npi.gestaocompetencia.model;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import br.ufc.quixada.npi.gestaocompetencia.model.enums.Lotacao;
import br.ufc.quixada.npi.gestaocompetencia.model.enums.VinculoProfissional;

public class DadosProfissionais {

	private String cargo;
	private String funcao;
	
	@Enumerated(EnumType.STRING)
	private VinculoProfissional vinculo;
	
	@Enumerated(EnumType.STRING)
	private Lotacao lotacao;
	
	private String imagem;
	private String siape;
	private String enderecoFuncional;
	private String telefoneFuncional;
	public String getCargo() {
		return cargo;
	}
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}
	public String getFuncao() {
		return funcao;
	}
	public void setFuncao(String funcao) {
		this.funcao = funcao;
	}
	public VinculoProfissional getVinculo() {
		return vinculo;
	}
	public void setVinculo(VinculoProfissional vinculo) {
		this.vinculo = vinculo;
	}
	public Lotacao getLotacao() {
		return lotacao;
	}
	public void setLotacao(Lotacao lotacao) {
		this.lotacao = lotacao;
	}
	public String getImagem() {
		return imagem;
	}
	public void setImagem(String imagem) {
		this.imagem = imagem;
	}
	public String getSiape() {
		return siape;
	}
	public void setSiape(String siape) {
		this.siape = siape;
	}
	public String getEnderecoFuncional() {
		return enderecoFuncional;
	}
	public void setEnderecoFuncional(String enderecoFuncional) {
		this.enderecoFuncional = enderecoFuncional;
	}
	public String getTelefoneFuncional() {
		return telefoneFuncional;
	}
	public void setTelefoneFuncional(String telefoneFuncional) {
		this.telefoneFuncional = telefoneFuncional;
	}
	
	
}
