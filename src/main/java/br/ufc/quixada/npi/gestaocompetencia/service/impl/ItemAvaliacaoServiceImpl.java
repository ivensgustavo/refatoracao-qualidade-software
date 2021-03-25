package br.ufc.quixada.npi.gestaocompetencia.service.impl;

import br.ufc.quixada.npi.gestaocompetencia.exception.GestaoCompetenciaException;
import br.ufc.quixada.npi.gestaocompetencia.model.Avaliacao;
import br.ufc.quixada.npi.gestaocompetencia.model.ItemAvaliacao;
import br.ufc.quixada.npi.gestaocompetencia.repository.ItemAvaliacaoRepository;
import br.ufc.quixada.npi.gestaocompetencia.service.ItemAvaliacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemAvaliacaoServiceImpl implements ItemAvaliacaoService {
    @Autowired
    private ItemAvaliacaoRepository itemAvaliacaoRepository;

    @Override
    @Transactional
    public ItemAvaliacao findByFator(ItemAvaliacao itemAvaliacao) {
        return itemAvaliacaoRepository.findByAvaliacaoAndFator(itemAvaliacao.getAvaliacao(), itemAvaliacao.getFator());
    }

    @Override
    @Transactional
    public ItemAvaliacao findByResponsabilidade(ItemAvaliacao itemAvaliacao) {
        return itemAvaliacaoRepository.findByAvaliacaoAndResponsabilidade(itemAvaliacao.getAvaliacao(), itemAvaliacao.getResponsabilidade());
    }

    @Override
    @Transactional
    public List<ItemAvaliacao> findAll() {
        return itemAvaliacaoRepository.findAll();
    }

    @Override
    @Transactional
    public List<ItemAvaliacao> search(Specification<ItemAvaliacao> specification) {
        return itemAvaliacaoRepository.findAll(specification);
    }

    @Override
    @Transactional
    public ItemAvaliacao update(ItemAvaliacao itemAvaliacao) {
        return itemAvaliacaoRepository.save(itemAvaliacao);
    }

    @Override
    @Transactional
    public void delete(ItemAvaliacao itemAvaliacao) {
        itemAvaliacaoRepository.delete(itemAvaliacao);
    }

    @Override
    @Transactional
    public ItemAvaliacao save(ItemAvaliacao itemAvaliacao) {
        return itemAvaliacaoRepository.save(itemAvaliacao);
    }
    
    public ItemAvaliacao avaliar(ItemAvaliacao item, Avaliacao avaliacao) {
    	if (Avaliacao.Perspectiva.COMPORTAMENTAL.equals(item.getAvaliacao().getPerspectiva()) && this.findByFator(item) != null) {
			throw new GestaoCompetenciaException("Item já avaliado");
		}
		if (Avaliacao.Perspectiva.RESPONSABILIDADE.equals(item.getAvaliacao().getPerspectiva()) && this.findByResponsabilidade(item) != null) {
			throw new GestaoCompetenciaException("Item já avaliado");
		}
		item.setAvaliacao(avaliacao);
		return this.save(item);
    }
}
