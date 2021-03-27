package br.ufc.quixada.npi.gestaocompetencia.controller.impl;
import br.ufc.quixada.npi.gestaocompetencia.exception.GestaoCompetenciaException;
import br.ufc.quixada.npi.gestaocompetencia.model.*;

import br.ufc.quixada.npi.gestaocompetencia.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("perfil")
public class PerfilControllerImpl {

    @Autowired
    private PerfilService perfilService;

    @Autowired
    private AreaCapacitacaoService areaCapacitacaoService;

    private Perfil findByUsuario(Usuario usuario) {
    	return perfilService.findByUsuario(usuario);
    }
    
    @GetMapping("")
    public ResponseEntity<Perfil> getOrCreate(@AuthenticationPrincipal Usuario usuario) {
        Perfil perfil = this.findByUsuario(usuario);
        if(perfil == null) {
            this.create(usuario);
            return ResponseEntity.ok(this.findByUsuario(usuario));
        } else {
            return ResponseEntity.ok(perfil);
        }
    }

    @PostMapping("")
    public ResponseEntity<Perfil> create(@AuthenticationPrincipal Usuario usuario) {
        Perfil novoPerfil = new Perfil();
        novoPerfil.setUsuario(usuario);
        return ResponseEntity.ok(perfilService.create(novoPerfil));
    }
    
    private boolean verificarPerfilValido(Perfil perfil, Perfil perfilSalvo) {
    	if(perfil == null) return false;
    	return perfil.verificarValidade(perfilSalvo);
    }
    
    private void adicionarNovaAreaCapacitacaoAoPerfil(AreaCapacitacao areaCapacitacao, Usuario usuario) {
    	areaCapacitacao.addNovaAreaCapacitacaoAoperfil(usuario);
        areaCapacitacao = areaCapacitacaoService.create(areaCapacitacao);
    }
    
    private void modificarAreaCapacitacaoExistenteNoPerfil(AreaCapacitacao areaCapacitacao, AreaCapacitacao areaCapacitacaoSalva) {
    	areaCapacitacao.modificarAreaCapacitacaoExistenteNoPerfil(areaCapacitacaoSalva);
        areaCapacitacao = areaCapacitacaoService.update(areaCapacitacao);
    }
    
    private void atualizarAreaCapacitacao(Usuario usuario, AreaCapacitacao areaCapacitacao) {
    	 if(areaCapacitacao.getCompetencia() != null) {
             AreaCapacitacao areaCapacitacaoSalva = areaCapacitacaoService.findByUsuarioAndCompetencia(
                 usuario, areaCapacitacao.getCompetencia()
             );

             if(areaCapacitacaoSalva == null) {
                 this.adicionarNovaAreaCapacitacaoAoPerfil(areaCapacitacao, usuario);
             } else {
                 this.modificarAreaCapacitacaoExistenteNoPerfil(areaCapacitacao, areaCapacitacaoSalva);
             }
         }
    }

    @PutMapping("")
    public ResponseEntity<Perfil> update(@AuthenticationPrincipal Usuario usuario, @RequestBody Perfil perfil) {
        Perfil perfilSalvo = this.findByUsuario(usuario);
        boolean perfilValido = verificarPerfilValido(perfil, perfilSalvo);
        
        if(perfilValido) {

            for(AreaCapacitacao areaCapacitacao : perfil.getAreasCapacitacaoInstrutor()) {
            	atualizarAreaCapacitacao(usuario, areaCapacitacao);
            }

            perfil.setUsuario(perfilSalvo.getUsuario());
            return ResponseEntity.ok(perfilService.update(perfil));
        } else {
            throw new GestaoCompetenciaException("Perfil necessário para realizar a operação");
        }
    }
}
