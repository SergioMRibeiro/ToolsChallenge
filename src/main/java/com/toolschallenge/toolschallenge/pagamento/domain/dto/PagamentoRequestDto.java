package com.toolschallenge.toolschallenge.pagamento.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record PagamentoRequestDto(
        @JsonProperty("transacao") @NotNull @Valid Transacao transacao,
        @JsonProperty("formaPagamento") @NotNull @Valid FormaPagamento formaPagamento
) {

    public record Transacao(
            @JsonProperty("cartao") @NotBlank String cartao,
            @NotBlank String id,
            @JsonProperty("descricao") @NotNull @Valid Descricao descricao
    ) {
    }

    public record Descricao(
            @NotBlank String valor,
            @JsonProperty("dataHora") @NotBlank String dataHora,
            @NotBlank String estabelecimento
    ) {
    }

    public record FormaPagamento(
            @NotBlank String tipo,
            @NotBlank String parcelas
    ) {
    }
}
