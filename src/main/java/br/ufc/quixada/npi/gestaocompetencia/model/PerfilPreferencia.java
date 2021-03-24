package br.ufc.quixada.npi.gestaocompetencia.model;

import javax.persistence.*;

@Entity
public class PerfilPreferencia {

    @EmbeddedId
    private PerfilPreferenciaKey perfilPreferenciaKey;

    @ManyToOne
    @MapsId("perfil_id")
    @JoinColumn(name = "perfil_id")
    private Perfil perfil;

    @ManyToOne
    @MapsId("preferencia_id")
    @JoinColumn(name = "preferencia_id")
    private Preferencia preferencia;

    @ManyToOne
    @JoinColumn(name = "item_preferencia_id")
    private ItemPreferencia itemPreferencia;

    public PerfilPreferencia() {
    	
    }
    
    public PerfilPreferencia(Perfil perfil, Preferencia preferencia) {
    	this.perfilPreferenciaKey = new PerfilPreferenciaKey(perfil.getId(), preferencia.getId());
    	this.perfil = perfil;
    	this.preferencia =preferencia;
    	this.itemPreferencia = null;
    }
    
    public PerfilPreferenciaKey getPerfilPreferenciaKey() {
        return perfilPreferenciaKey;
    }

    public void setPerfilPreferenciaKey(PerfilPreferenciaKey perfilPreferenciaKey) {
        this.perfilPreferenciaKey = perfilPreferenciaKey;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
    	 PerfilPreferenciaKey perfilPreferenciaKey = new PerfilPreferenciaKey();
         perfilPreferenciaKey.setPerfilId(perfil.getId());
         perfilPreferenciaKey.setPreferenciaId(preferencia.getId());
         this.setPerfilPreferenciaKey(perfilPreferenciaKey);
         this.perfil = perfil;
    }

    public Preferencia getPreferencia() {
        return preferencia;
    }

    public void setPreferencia(Preferencia preferencia) {
        this.preferencia = preferencia;
    }

    public ItemPreferencia getItemPreferencia() {
        return itemPreferencia;
    }

    public void setItemPreferencia(ItemPreferencia itemPreferencia) {
        this.itemPreferencia = itemPreferencia;
    }

    @Override
    public String toString() {
        return "PerfilPreferencia{" +
                "perfilPreferenciaKey=" + perfilPreferenciaKey.getPerfilId() +
                ", perfil=" + perfil +
                ", preferencia=" + preferencia +
                ", itemPreferencia=" + itemPreferencia +
                '}';
    }
}
