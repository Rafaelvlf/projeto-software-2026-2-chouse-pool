package br.insper.chouse.pool.event;

import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

@Value
public class VotoRegistrado implements Serializable {
    Long votoId;
    Long enqueteId;
    Long opcaoId;
    LocalDateTime ocorridoEm;
}
