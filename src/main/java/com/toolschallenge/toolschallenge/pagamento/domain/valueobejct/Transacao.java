package com.toolschallenge.toolschallenge.pagamento.domain.valueobejct;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transacao {
    @Column(name = "cartao", nullable = false)
    private String cartao;

    @Column(name = "transacao_id", nullable = false, unique = true)
    private String id;

    @Embedded
    private Descricao descricao;
}
