package br.ufc.quixada.npi.gestaocompetencia.model;



import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import br.ufc.quixada.npi.gestaocompetencia.model.enums.SituacaoComportamento;

public class ComportamentoSpecification implements Specification<Comportamento>{

	private static final long serialVersionUID = 1L;
	private static final String EXCLUIDO = "excluido";
	private static final String UNIDADE = "unidade";
	private static final String COMPETENCIA = "competencia";
	private ComportamentoSearch criteria;
	private Root<Comportamento> root;
	private CriteriaBuilder criteriaBuilder;
	final List<Predicate> predicates = new ArrayList<>();
	
	public ComportamentoSpecification(ComportamentoSearch criteria) {
		this.criteria = criteria;
	}
	
	private boolean comparaSituacao(SituacaoComportamento tipoSituacao) {
		return criteria.getSituacao().equals(tipoSituacao);
	}
	
	private void addPredicadoQuandoSituacaoIgualANaoAssociados() {
		predicates.add(criteriaBuilder.and(criarPredicadoIsNull(COMPETENCIA),criarPredicadoIsFalse(EXCLUIDO)));
	}
	
	private void addPredicadoQuandoSituacaoIgualAAssociados() {
		predicates.add(criteriaBuilder.and(criarPredicadoIsNotNull(COMPETENCIA),criarPredicadoIsFalse(EXCLUIDO)));
	}
	
	private void addPredicadoQuandoSituacaoIgualANaoConsilidados() {
		predicates.add(criteriaBuilder.and(criarPredicadoIsFalse("consolidado"), criarPredicadoIsFalse(EXCLUIDO)));
	}
	
	private void addPredicadoQuandoSitucaoIgualAConsolidados() {
		predicates.add(criarPredicadoIsTrue("consolidado"));
	}
	
	private void addPredicadoQuandoSituacaoIgualAExcluidos() {
		predicates.add(criarPredicadoIsTrue(EXCLUIDO));
	}
	
	private void addPredicadoQuandoSituacaoIgualATodosOuOriginais() {
		predicates.add(criarPredicadoIsFalse(EXCLUIDO));
	}
	
	private void addPredicadoQuandoSituacaoIgualAEditados() {
		predicates.add(
				this.getPredicadoSitucaoIgualAEditados()
			);
	}
	
	private Predicate getPredicadoSitucaoIgualAEditados() {
		return criteriaBuilder.and(
				criarPredicadoIsNotNull("descricaoAtualizada"),
				criarPredicadoIsFalse(EXCLUIDO),
				criarPredicadoNotEqual("descricao", "descricaoAtualizada")
			);
	}
	
	private Predicate criarPredicadoIsNotNull(String campo) {
		return criteriaBuilder.isNotNull(root.get(campo));
	}
	
	private Predicate criarPredicadoIsNull(String campo) {
		return criteriaBuilder.isNull(root.get(campo));
	}
	
	private Predicate criarPredicadoIsFalse(String campo) {
		return criteriaBuilder.isFalse(root.get(campo));
	}
	
	private Predicate criarPredicadoIsTrue(String campo) {
		return criteriaBuilder.isTrue(root.get(campo));
	}
	
	private Predicate criarPredicadoNotEqual(String valor1, String valor2) {
		return criteriaBuilder.notEqual(root.get(valor1), root.get(valor2));
	}
	
	/*private void filtrarSituacaoEAddPredicados(Root<Comportamento> root, CriteriaBuilder criteriaBuilder) {
		
	}*/
	@Override
	public Predicate toPredicate(Root<Comportamento> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {	
		
		this.root = root;
		this.criteriaBuilder = criteriaBuilder;

		Path<Unidade> unidade = root.get(UNIDADE);
		
		if(criteria.getCompetencia() != null) {
			predicates.add(criteriaBuilder.equal(root.get(COMPETENCIA), criteria.getCompetencia()));
		}

		if(criteria.getMapeamento() != null) {
			predicates.add(criteriaBuilder.equal(root.get("mapeamento"), criteria.getMapeamento()));
		}

		// Filtro de Situação
		if(criteria.getSituacao() != null) {
			if(this.comparaSituacao(SituacaoComportamento.NAO_ASSOCIADOS)) {
				this.addPredicadoQuandoSituacaoIgualANaoAssociados();
				
			} else if(this.comparaSituacao(SituacaoComportamento.ASSOCIADOS)) {
				this.addPredicadoQuandoSituacaoIgualAAssociados();
				
			} else if(this.comparaSituacao(SituacaoComportamento.NAO_CONSOLIDADOS)) {
				this.addPredicadoQuandoSituacaoIgualANaoConsilidados();
				
			}else if(this.comparaSituacao(SituacaoComportamento.CONSOLIDADOS)) {
				this.addPredicadoQuandoSitucaoIgualAConsolidados();
				
			} else if(this.comparaSituacao(SituacaoComportamento.EXCLUIDOS)) {
				this.addPredicadoQuandoSituacaoIgualAExcluidos();
				
			} else if(this.comparaSituacao(SituacaoComportamento.TODOS) || this.comparaSituacao(SituacaoComportamento.ORIGINAIS)) {
				this.addPredicadoQuandoSituacaoIgualATodosOuOriginais();
				
			} else if(this.comparaSituacao(SituacaoComportamento.EDITADOS)) {
				this.addPredicadoQuandoSituacaoIgualAEditados();
			}
		}

        if(criteria.getUnidades() != null) {
            predicates.add(unidade.in(criteria.getUnidades()));
        }
		
		return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
	}

}