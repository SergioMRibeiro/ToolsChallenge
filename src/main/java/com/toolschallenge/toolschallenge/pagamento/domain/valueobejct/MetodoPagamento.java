package com.toolschallenge.toolschallenge.pagamento.domain.valueobejct;

import com.toolschallenge.toolschallenge.pagamento.domain.enums.MetodoPagamentoType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MetodoPagamento {
    @Column(name = "metodo_pagamento", nullable = false)
    private MetodoPagamentoType metodoPagamentoType;

    @Column(name = "parcelas", nullable = false)
    private Integer parcelas;
}
