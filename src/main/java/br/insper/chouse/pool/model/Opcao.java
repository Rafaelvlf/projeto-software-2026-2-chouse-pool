package br.insper.chouse.pool.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "opcoes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Opcao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String texto;

    @Column(nullable = false)
    private int totalVotos;

    public Opcao(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Texto da opção é obrigatório");
        }
        this.texto = texto.trim();
    }

    public void incrementarVotos() {
        totalVotos++;
    }

    public double percentual(int total) {
        return total == 0 ? 0 : totalVotos * 100.0 / total;
    }
}
