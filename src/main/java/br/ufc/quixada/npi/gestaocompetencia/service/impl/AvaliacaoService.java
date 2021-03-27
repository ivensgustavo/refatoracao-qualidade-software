package br.ufc.quixada.npi.gestaocompetencia.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.ufc.quixada.npi.gestaocompetencia.exception.GestaoCompetenciaException;
import br.ufc.quixada.npi.gestaocompetencia.exception.NotAllowedException;
import br.ufc.quixada.npi.gestaocompetencia.model.Diagnostico;
import br.ufc.quixada.npi.gestaocompetencia.model.ItemAvaliacao;
import br.ufc.quixada.npi.gestaocompetencia.repository.UnidadeRepository;
import br.ufc.quixada.npi.gestaocompetencia.repository.UsuarioRepository;
import br.ufc.quixada.npi.gestaocompetencia.service.DiagnosticoService;
import br.ufc.quixada.npi.gestaocompetencia.service.ItemAvaliacaoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.ufc.quixada.npi.gestaocompetencia.model.Avaliacao;
import br.ufc.quixada.npi.gestaocompetencia.model.Avaliacao.TipoAvaliacao;
import br.ufc.quixada.npi.gestaocompetencia.model.Unidade;
import br.ufc.quixada.npi.gestaocompetencia.model.Usuario;
import br.ufc.quixada.npi.gestaocompetencia.repository.AvaliacaoRepository;

import static br.ufc.quixada.npi.gestaocompetencia.model.Avaliacao.TipoAvaliacao.*;

@Service
public class AvaliacaoService {

	@Autowired
	private AvaliacaoRepository avaliacaoRepository;

	@Autowired
    private DiagnosticoService diagnosticoService;
	
	@Autowired
	private ItemAvaliacaoService itemAvaliacaoService;

	@Autowired
	private UnidadeRepository unidadeRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

    private static final String PERMISSAO_AVALIACOES = "Sem permissão para criar as avaliações";

	public List<Avaliacao> find(Usuario usuario, Diagnostico diagnostico, TipoAvaliacao tipo, Avaliacao.Perspectiva perspectiva){
		return avaliacaoRepository.findAvaliacao(usuario, diagnostico, tipo, perspectiva);
	}
	
	private boolean hasNoAvaliacoes(List<Avaliacao> lista) {
		return lista == null || lista.isEmpty();
	}
	
	private Avaliacao getAvaliacaoSimples(List<Avaliacao> avaliacoes, TipoAvaliacao tipo, Usuario usuario, Diagnostico diagnostico, Avaliacao.Perspectiva perspectiva) {
		if (hasNoAvaliacoes(avaliacoes)) {
			return this.createSimple(tipo, usuario, diagnostico, perspectiva);
		} else {
			return avaliacoes.get(0);
		}
	}
	
	private List<Avaliacao> getAvaliacaoComplexa(List<Avaliacao> avaliacoes, TipoAvaliacao tipo, Usuario usuario, Diagnostico diagnostico, Avaliacao.Perspectiva perspectiva) {
		if (hasNoAvaliacoes(avaliacoes)) {
			return this.createComplex(tipo, usuario, diagnostico, perspectiva);
		} else {
			return avaliacoes;
		}
	}
	
	private List<Avaliacao> getAvaliacaoComplexaSecondaryAcess(List<Avaliacao> avaliacoes, TipoAvaliacao tipo, Usuario usuario, Diagnostico diagnostico, Avaliacao.Perspectiva perspectiva){
		if (hasNoAvaliacoes(avaliacoes) && TipoAvaliacao.SUBORDINADO.equals(tipo)) {
				return this.createComplexSecondaryAccess(tipo, usuario, diagnostico, perspectiva);
		} else {
			return avaliacoes;
		}
	}
	
	public List<Avaliacao> findComplete(Usuario usuario, Diagnostico diagnostico, TipoAvaliacao tipo, Avaliacao.Perspectiva perspectiva){
		List<Avaliacao> avaliacoes = this.find(usuario, diagnostico, tipo, perspectiva);
		List<Avaliacao> retorno = new ArrayList<>();

		if(diagnosticoService.verifyAccess(diagnostico, usuario.getUnidade())) {
			if (TipoAvaliacao.AUTOAVALIACAO.equals(tipo) || TipoAvaliacao.CHEFIA.equals(tipo)) {
				retorno.add(this.getAvaliacaoSimples(avaliacoes, tipo, usuario, diagnostico, perspectiva));
			} else if (TipoAvaliacao.SUBORDINADO.equals(tipo) || TipoAvaliacao.PARES.equals(tipo)) {
				retorno.addAll(this.getAvaliacaoComplexa(avaliacoes, tipo, usuario, diagnostico, perspectiva));
			}
		} else if(diagnosticoService.verifySecondaryAccess(diagnostico, usuario.getUnidade())) {
			retorno.addAll(this.getAvaliacaoComplexaSecondaryAcess(avaliacoes, tipo, usuario, diagnostico, perspectiva));
		}

		return retorno;
		
	}
	
	public ResponseEntity<List<ItemAvaliacao>> avaliar(List<ItemAvaliacao> avaliacoes) throws GestaoCompetenciaException{
		List<ItemAvaliacao> itens = new ArrayList<>();
		for(ItemAvaliacao item : avaliacoes) {
			Avaliacao avaliacao = this.findById(item.getAvaliacao().getId());
			itens.add(itemAvaliacaoService.avaliar(item, avaliacao));
		}
		return ResponseEntity.ok().build();
	}

	public List<Avaliacao> findByAvaliado(Usuario usuario, Diagnostico diagnostico, TipoAvaliacao tipo, Avaliacao.Perspectiva perspectiva){
		return avaliacaoRepository.findAvaliacaoByAvaliado (usuario, diagnostico, tipo, perspectiva);
	}

	public List<Avaliacao> findAvaliacoes(Usuario usuario,TipoAvaliacao tipo){
		return avaliacaoRepository.findByAvaliadorAndTipoAndIgnoradaFalse(usuario,tipo);
	}

	public List<Avaliacao> findAvaliacoesJustificadasUnidade(Diagnostico diagnostico, Unidade unidade, TipoAvaliacao tipo, Avaliacao.Perspectiva perspectiva) {
		return avaliacaoRepository.findJustificadasUnidade(diagnostico, unidade, tipo, perspectiva);
	}

	public List<Avaliacao> findAvaliacoesJustificadasUsuario(Diagnostico diagnostico, Usuario usuario, TipoAvaliacao tipo, Avaliacao.Perspectiva perspectiva) {
		return avaliacaoRepository.findJustificadasUsuario(diagnostico, usuario, tipo, perspectiva);
	}

	public Avaliacao findAvaliacoesByAvaliadorAndAvaliadoAndTipo(Usuario avaliador, Usuario avaliado,
			TipoAvaliacao tipo) {
		return avaliacaoRepository.findByAvaliadorAndAvaliadoAndTipoAndIgnoradaFalse(avaliador,avaliado,tipo);
	}

	public List<Avaliacao> findByAvaliadorAndUnidade(Usuario avaliador, Unidade unidade) {
		return avaliacaoRepository.findByAvaliadorAndUnidade(avaliador,unidade);
	}

	public void delete(Avaliacao avaliacao) {
		avaliacaoRepository.delete(avaliacao);
	}

	public Avaliacao postAvaliacao(Avaliacao avaliacao) {
		return avaliacaoRepository.save(avaliacao);
	}

	public Avaliacao createSimple(TipoAvaliacao tipo, Usuario avaliador, Diagnostico diagnostico, Avaliacao.Perspectiva perspectiva) {
		Avaliacao avaliacao = this.createBase(tipo, avaliador, diagnostico, perspectiva);
		
		switch (tipo) {
			case AUTOAVALIACAO:
				avaliacao.setAvaliado(avaliador);
				break;
			case CHEFIA:
				avaliacao.setAvaliado(avaliador.equals(avaliador.getUnidade().getChefe()) ? avaliador.getUnidade().getUnidadePai().getChefe() : avaliador.getUnidade().getChefe());
				break;
            default:
                throw new NotAllowedException(PERMISSAO_AVALIACOES);
		}
		avaliacao.setUnidade(avaliacao.getAvaliado().getUnidade());
		return avaliacaoRepository.save(avaliacao);
	}
	
	private List<Avaliacao> obterResultadoAvaliacao(TipoAvaliacao tipo, Usuario avaliador, Diagnostico diagnostico, Avaliacao.Perspectiva perspectiva){
		List<Avaliacao> resultado = new ArrayList<>();
        if(avaliador.getUnidade().getChefe().equals(avaliador)) {
            resultado = avaliacaoRepository.findAvaliacaoByAvaliador(avaliador.getViceChefe(), diagnostico, tipo, perspectiva);
            resultado.addAll(avaliacaoRepository.findIgnoradas(avaliador.getViceChefe(), diagnostico, tipo, perspectiva));
        }

        if(this.existeUmViceChefe(avaliador)) {
            resultado = avaliacaoRepository.findAvaliacaoByAvaliador(avaliador.getUnidade().getChefe(), diagnostico, tipo, perspectiva);
            resultado.addAll(avaliacaoRepository.findIgnoradas(avaliador.getUnidade().getChefe(), diagnostico, tipo, perspectiva));
        }
        
        return resultado;
	}
	
	private List<Avaliacao> executarRotinaAvaliacaoNaoRealizada(List<Avaliacao> resultado, TipoAvaliacao tipo, Usuario avaliador, Diagnostico diagnostico, Avaliacao.Perspectiva perspectiva) {
		resultado.forEach(av -> avaliacaoRepository.delete(av));
		
		List<Usuario> servidores = usuarioRepository.findByUnidade(avaliador.getUnidade());
		List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
		
        servidores.remove(avaliador);

        if(this.existeUmViceChefe(avaliador)){
            servidores.remove(avaliador.getUnidade().getChefe());
        }

        List<Unidade> unidades = unidadeRepository.findByUnidadePai(avaliador.getUnidade());
        for (Unidade unidade : unidades) {
            servidores.add(unidade.getChefe());
        }

        for (Usuario servidor : servidores) {
            Avaliacao avaliacao = this.createBase(tipo, avaliador, diagnostico, perspectiva);
            avaliacao.setAvaliado(servidor);
            avaliacao.setUnidade(avaliacao.getAvaliado().getUnidade());
            avaliacoes.add(avaliacaoRepository.save(avaliacao));
        }
        
        return avaliacoes;
	}
	
	private List<Avaliacao> createComplexAvaliacaoSubordinado(TipoAvaliacao tipo, Usuario avaliador, Diagnostico diagnostico, Avaliacao.Perspectiva perspectiva){
		 List<Avaliacao> resultado = this.obterResultadoAvaliacao(tipo, avaliador, diagnostico, perspectiva);

         boolean avaliacaoRealizada = false;

         for(Avaliacao avaliacao : resultado) {
             if(!avaliacao.getItens().isEmpty()) {
                 avaliacaoRealizada = true;
                 break;
             }
         }

         if(avaliacaoRealizada) {
             return resultado;
         } 
         
         return executarRotinaAvaliacaoNaoRealizada(resultado, tipo, avaliador, diagnostico, perspectiva);
         
         
	}
	
	private Avaliacao criarAvaliacaoParesLimiteAleatorioMaiorQue10(int limiteAleatorio, List<Usuario> servidores, List<Usuario> servidoresAvaliados, Usuario avaliador, Diagnostico diagnostico, TipoAvaliacao tipo, Avaliacao.Perspectiva perspectiva) {
		List<Avaliacao> avaliacoesSalvas = avaliacaoRepository.findAvaliacaoByAvaliador(avaliador, diagnostico, tipo, perspectiva);
		List<Avaliacao> avaliacoesIgnoradas = avaliacaoRepository.findIgnoradas(avaliador, diagnostico, tipo, perspectiva);

        int aleatorio = new Random().nextInt(limiteAleatorio);

        for (Avaliacao avaliacaoSalva : avaliacoesSalvas) {
        	servidoresAvaliados.add(avaliacaoSalva.getAvaliado());
        }

        for (Avaliacao avaliacaoIgnorada : avaliacoesIgnoradas) {
        	servidoresAvaliados.add(avaliacaoIgnorada.getAvaliado());
        }

        while (servidoresAvaliados.contains(servidores.get(aleatorio))) {
        	aleatorio = new Random().nextInt(limiteAleatorio);
        }

        Avaliacao avaliacao = this.createBase(tipo, avaliador, diagnostico, perspectiva);
        avaliacao.setAvaliado(servidores.get(aleatorio));
        avaliacao.setUnidade(avaliacao.getAvaliado().getUnidade());
             
        return avaliacao;
	}
	
	private List<Avaliacao> createComplexAvaliacaoPares(Usuario avaliador, Diagnostico diagnostico, TipoAvaliacao tipo, Avaliacao.Perspectiva perspectiva) {
		List<Usuario> servidores = usuarioRepository.findByUnidade(avaliador.getUnidade());
		List<Avaliacao> avaliacoesIgnoradas = avaliacaoRepository.findIgnoradas(avaliador, diagnostico, tipo, perspectiva);
		List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
		List<Usuario> servidoresAvaliados = new ArrayList<Usuario>();
		
		servidores.remove(avaliador);
        servidores.remove(avaliador.getUnidade().getChefe());
        servidores.remove(avaliador.getViceChefe());
        int limiteAleatorio = servidores.size();
        int numIgnoradas = 0;

        if(!avaliacoesIgnoradas.isEmpty()) {
            numIgnoradas = avaliacoesIgnoradas.size();
        }

        if(
            limiteAleatorio >= 2 && limiteAleatorio > numIgnoradas &&
            avaliador != avaliador.getUnidade().getChefe() &&
            avaliador != avaliador.getUnidade().getViceChefe()
        ) {
            if(limiteAleatorio >= 10) {

                for (int i = 0; i < 10; i++) {
                    Avaliacao avaliacao = this.criarAvaliacaoParesLimiteAleatorioMaiorQue10(limiteAleatorio, servidores, servidoresAvaliados, avaliador, diagnostico, tipo, perspectiva);
                    avaliacoes.add(avaliacaoRepository.save(avaliacao));
                }
            } else {
                for (Usuario servidor : servidores) {
                    Avaliacao avaliacao = this.createBase(tipo, avaliador, diagnostico, perspectiva);
                    avaliacao.setAvaliado(servidor);
                    avaliacao.setUnidade(avaliacao.getAvaliado().getUnidade());
                    avaliacoes.add(avaliacaoRepository.save(avaliacao));
                }
            }
        }
        
        return avaliacoes;
	}
	

	public List<Avaliacao> createComplex(TipoAvaliacao tipo, Usuario avaliador, Diagnostico diagnostico, Avaliacao.Perspectiva perspectiva) {
		List<Avaliacao> avaliacoes = new ArrayList<>();
		List<Usuario> servidores = usuarioRepository.findByUnidade(avaliador.getUnidade());

		if(!servidores.isEmpty()) {
            switch (tipo) {
                case SUBORDINADO:
                	avaliacoes = createComplexAvaliacaoSubordinado(tipo, avaliador, diagnostico, perspectiva);
                    break;
                    
                case PARES:
                    avaliacoes = createComplexAvaliacaoPares(avaliador, diagnostico, tipo, perspectiva);
                    break;
                    
                default:
                    throw new NotAllowedException(PERMISSAO_AVALIACOES);
            }
        }

		return avaliacoes;
	}

    public List<Avaliacao> createComplexSecondaryAccess(TipoAvaliacao tipo, Usuario avaliador, Diagnostico diagnostico, Avaliacao.Perspectiva perspectiva) {
        List<Avaliacao> avaliacoes = new ArrayList<>();
        List<Avaliacao> resultado = new ArrayList<>();

        if(!avaliador.getUnidade().hasPermissionCRUD(avaliador)) {
            throw new NotAllowedException(PERMISSAO_AVALIACOES);
        }

        if(avaliador.getUnidade().getChefe().equals(avaliador)) {
            resultado = avaliacaoRepository.findAvaliacaoByAvaliador(avaliador.getViceChefe(), diagnostico, tipo, perspectiva);
            resultado.addAll(avaliacaoRepository.findIgnoradas(avaliador.getViceChefe(), diagnostico, tipo, perspectiva));
        }

        if(this.existeUmViceChefe(avaliador)) {
            resultado = avaliacaoRepository.findAvaliacaoByAvaliador(avaliador.getUnidade().getChefe(), diagnostico, tipo, perspectiva);
            resultado.addAll(avaliacaoRepository.findIgnoradas(avaliador.getUnidade().getChefe(), diagnostico, tipo, perspectiva));
        }

        boolean avaliacaoRealizada = false;

        for(Avaliacao avaliacao : resultado) {
            if(!avaliacao.getItens().isEmpty()) {
                avaliacaoRealizada = true;
                break;
            }
        }

        if(avaliacaoRealizada) {
            return resultado;
        } else {
            resultado.forEach(av -> avaliacaoRepository.delete(av));

            List<Unidade> subunidades = unidadeRepository.findByUnidadePai(avaliador.getUnidade());
            for (Unidade subunidade : subunidades) {
                if(diagnosticoService.verifyAccess(diagnostico, subunidade)) {
                    Avaliacao avaliacao = this.createBase(tipo, avaliador, diagnostico, perspectiva);
                    avaliacao.setAvaliado(subunidade.getChefe());
                    avaliacao.setUnidade(avaliacao.getAvaliado().getUnidade());
                    avaliacoes.add(avaliacaoRepository.save(avaliacao));
                }
            }
        }

        return avaliacoes;
    }

    private boolean existeUmViceChefe(Usuario usuario) {
    	return usuario.existeUmViceChefe();
    }
    
	public Avaliacao justificar(Usuario avaliador, Avaliacao avaliacao, String justificativa) {
		List<Usuario> servidores = usuarioRepository.findByUnidade(avaliador.getUnidade());
		List<Usuario> servidoresAvaliados = new ArrayList<>();
		int limiteAleatorio;
		int numIgnoradas;
		int numSalvas;
		int total;
        int aleatorio;

		avaliacao.setJustificativa(justificativa);
		avaliacao.setIgnorada(true);
		avaliacaoRepository.save(avaliacao);

		if(avaliacao.getTipo() == PARES) {
            servidores.remove(avaliador);
            servidores.remove(avaliador.getUnidade().getChefe());
            servidores.remove(avaliador.getUnidade().getViceChefe());
            limiteAleatorio = servidores.isEmpty() ? 0 : servidores.size();

            List<Avaliacao> avaliacoesSalvas = avaliacaoRepository.findAvaliacaoByAvaliador(avaliador, avaliacao.getDiagnostico(), avaliacao.getTipo(), avaliacao.getPerspectiva());
            for (Avaliacao avaliacaoSalva : avaliacoesSalvas) {
                servidoresAvaliados.add(avaliacaoSalva.getAvaliado());
            }

            numSalvas = avaliacoesSalvas.isEmpty() ? 0 : avaliacoesSalvas.size();

            List<Avaliacao> avaliacoesIgnoradas = avaliacaoRepository.findIgnoradas(avaliador, avaliacao.getDiagnostico(), avaliacao.getTipo(), avaliacao.getPerspectiva());
            for (Avaliacao avaliacaoIgnorada : avaliacoesIgnoradas) {
                servidoresAvaliados.add(avaliacaoIgnorada.getAvaliado());
            }

            numIgnoradas = avaliacoesIgnoradas.isEmpty() ? 0 : avaliacoesIgnoradas.size();
            total = servidoresAvaliados.isEmpty() ? 0 : servidoresAvaliados.size();

            if(numSalvas == limiteAleatorio) {
                return avaliacao;
            }

            aleatorio = new Random().nextInt(limiteAleatorio);

            if(!servidores.isEmpty() && (limiteAleatorio - numIgnoradas) > 9 && (limiteAleatorio - total) > 0) {
                while (servidoresAvaliados.contains(servidores.get(aleatorio))) {
                    aleatorio = new Random().nextInt(limiteAleatorio);
                }

                Avaliacao av = this.createBase(avaliacao.getTipo(), avaliador, avaliacao.getDiagnostico(), avaliacao.getPerspectiva());
                av.setAvaliado(servidores.get(aleatorio));
                av.setUnidade(avaliacao.getAvaliado().getUnidade());

                return avaliacaoRepository.save(av);
            } else {
                return avaliacao;
            }
		} else {
			return avaliacao;
		}
	}

	public Avaliacao removerJustificativa(Avaliacao avaliacao) {
		avaliacao.setJustificativa(null);
		avaliacao.setIgnorada(false);
		return avaliacaoRepository.save(avaliacao);
	}

	private Avaliacao createBase(TipoAvaliacao tipo, Usuario avaliador, Diagnostico diagnostico, Avaliacao.Perspectiva perspectiva) {
		Avaliacao avaliacao = new Avaliacao();
		avaliacao.setDadosAvaliacaoBase(tipo, avaliador, diagnostico, perspectiva);
		return avaliacao;
	}

	public Avaliacao findById(Integer id) {
		return avaliacaoRepository.getOne(id);
	}
}
