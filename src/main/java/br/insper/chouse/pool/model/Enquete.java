package br.insper.chouse.pool.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "enquetes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enquete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pergunta;

    @Column(nullable = false)
    private String categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEnquete status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadaEm;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "ordem")
    private List<Opcao> opcoes = new ArrayList<>();

    public Enquete(String pergunta, String categoria, Opcao opcaoA, Opcao opcaoB) {
        if (pergunta == null || pergunta.isBlank()) {
            throw new IllegalArgumentException("Pergunta é obrigatória");
        }
        this.pergunta = pergunta.trim();
        this.categoria = categoria == null || categoria.isBlank() ? "GERAL" : categoria.trim();
        this.status = StatusEnquete.ATIVA;
        this.opcoes.add(opcaoA);
        this.opcoes.add(opcaoB);
    }

    @PrePersist
    void definirDataCriacao() {
        if (criadaEm == null) {
            criadaEm = LocalDateTime.now();
        }
    }

    public int getTotalVotos() {
        return opcoes.stream().mapToInt(Opcao::getTotalVotos).sum();
    }

    public Map<Long, Double> calcularPercentuais() {
        int total = getTotalVotos();
        Map<Long, Double> percentuais = new LinkedHashMap<>();
        opcoes.forEach(opcao -> percentuais.put(opcao.getId(), opcao.percentual(total)));
        return percentuais;
    }

    public void encerrar() {
        status = StatusEnquete.ENCERRADA;
    }

    public Opcao encontrarOpcao(Long opcaoId) {
        return opcoes.stream()
                .filter(opcao -> opcao.getId().equals(opcaoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Opção não pertence à enquete"));
    }
}
