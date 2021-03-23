package br.ufc.quixada.npi.gestaocompetencia.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.util.*;

@Entity
public class Usuario implements UserDetails, Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Integer id;
	
	@NotEmpty
	@Column(unique = true)
	private String email;
	
	@NotEmpty
	@JsonIgnore
	private String password;
	
	private DadosPessoais dadosPessoais;
	private DadosProfissionais dadosProfissionais;
	private boolean habilitado;
	
	@ManyToOne
	@JoinColumn(name = "unidade_id")
//	@JsonBackReference	
	private Unidade unidade;
	
	@JsonIgnore
	@OneToOne
	@JoinColumn(name = "perfil_id")
	private Perfil perfil;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public DadosPessoais getDadosPessoais() {
		return dadosPessoais;
	}

	public void setDadosPessoais(DadosPessoais dadosPessoais) {
		this.dadosPessoais = dadosPessoais;
	}
	
	public DadosProfissionais getDadosProfissionais() {
		return dadosProfissionais;
	}

	public void setDadosProfissionais(DadosProfissionais dadosProfissionais) {
		this.dadosProfissionais = dadosProfissionais;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public boolean isHabilitado() {
		return habilitado;
	}

	public void setHabilitado(boolean habilitado) {
		this.habilitado = habilitado;
	}

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}
	
	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.habilitado;
	}

	public List<String> getRoles() {
			return new ArrayList<>();
	}

	public boolean isSubordinado(Usuario usuario) {
		return unidade.getChefe().getId().equals(this.getId()) &&
				usuario.getUnidade().getId().equals(unidade.getId());
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", nome=" + this.dadosPessoais.nome + ", nomeSocial=" + this.dadosPessoais.getNomeSocial() + ", email=" + email
				+ ", password=" + password + ", habilitado=" + habilitado + ", unidade=" + unidade + ", cargo=" + this.dadosProfissionais.getCargo()
				+ ", funcao=" + this.dadosProfissionais.getFuncao() + ", siape=" + this.dadosProfissionais.getSiape() + ", dataNasc=" + this.dadosPessoais.getDataNasc() + ", sexo=" + dadosPessoais.getSexo()
				+ ", deficiencia=" + this.dadosPessoais.getDeficiencia() + ", endereco=" + this.dadosPessoais.getEndereco() + ", enderecoFuncional=" + this.dadosProfissionais.getEnderecoFuncional()
				+ ", telefone=" + this.dadosPessoais.getTelefone() + ", ramal=" + "]";
	}
	
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Usuario usuario = (Usuario) o;
		return id.equals(usuario.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
	public Map<String, Object> prepararMapServidores(){
		Map<String, Object> map = new LinkedHashMap<>();
		
		map.put("ID", this.getId());
		map.put("SERVIDOR", this);
		map.put("UNIDADE", this.getUnidade());
		if(this.getUnidade().hasPermissionCRUD(this)) {
			map.put("TIPO", "GESTOR");
		} else {
			map.put("TIPO", "SERVIDOR");
		}
		
		return map;
	}
	
	public boolean existeUmChefe() {
		return this.getUnidade().getChefe() != null && this.getUnidade().getChefe().equals(this);
	}
	
	public boolean existeUmViceChefe() {
		return this.getUnidade().getViceChefe() != null && this.getUnidade().getViceChefe().equals(this);
	}
}
