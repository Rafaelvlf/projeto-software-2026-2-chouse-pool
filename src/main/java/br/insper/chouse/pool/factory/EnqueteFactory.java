package br.insper.chouse.pool.factory;

import br.insper.chouse.pool.model.Enquete;
import br.insper.chouse.pool.model.Opcao;
import org.springframework.stereotype.Component;

@Component
public class EnqueteFactory {

    public Enquete criar(String pergunta, String textoA, String textoB) {
        return criar(pergunta, "GERAL", textoA, textoB);
    }

    public Enquete criar(String pergunta, String categoria, String textoA, String textoB) {
        if (textoA != null && textoB != null && textoA.trim().equalsIgnoreCase(textoB.trim())) {
            throw new IllegalArgumentException("As opções devem ser diferentes");
        }
        return new Enquete(pergunta, categoria, new Opcao(textoA), new Opcao(textoB));
    }
}
