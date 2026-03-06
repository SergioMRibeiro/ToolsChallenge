package com.toolschallenge.toolschallenge.pagamento.domain.valueobejct;

import com.toolschallenge.toolschallenge.pagamento.domain.enums.TransacaoStatusType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Descricao {
    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "estabelecimento" , nullable = false)
    private String estabelecimento;

    @Column(name = "nsu", nullable = false, unique = true, length = 9)
    private String nsu;

    @Column(name = "codigo_autorizacao", nullable = false, unique = true)
    private String codigoAutorizacao;

    @Column(name = "transacao_status_type")
    private TransacaoStatusType transacaoStatusType;
}
