package br.insper.chouse.pool.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "votos", uniqueConstraints =
        @UniqueConstraint(name = "uk_voto_usuario_enquete", columnNames = {"usuario_id", "enquete_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "enquete_id", nullable = false)
    private Long enqueteId;

    @Column(name = "opcao_id", nullable = false)
    private Long opcaoId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataHora;

    public Voto(Long usuarioId, Long enqueteId, Long opcaoId) {
        this.usuarioId = usuarioId;
        this.enqueteId = enqueteId;
        this.opcaoId = opcaoId;
    }

    @PrePersist
    void definirDataHora() {
        if (dataHora == null) {
            dataHora = LocalDateTime.now();
        }
    }
}
