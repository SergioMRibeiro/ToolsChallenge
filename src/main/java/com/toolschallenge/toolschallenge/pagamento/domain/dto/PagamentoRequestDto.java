package com.toolschallenge.toolschallenge.pagamento.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PagamentoRequestDto(
        @NotNull @Valid Transacao transacao
) {
    public record Transacao(
            @NotBlank String cartao,
            @NotBlank String id,
            @NotNull @Valid Descricao descricao,
            @NotNull @Valid FormaPagamento formaPagamento
    ) {}

    public record Descricao(
            @NotBlank
            @Pattern(regexp = "^\\d+(,\\d{1,2})?$",
                    message = "Valor deve ser numérico com vírgula opcional")
            String valor,

            @NotBlank String dataHora,
            @NotBlank String estabelecimento
    ) {}

    public record FormaPagamento(
            @NotBlank String tipo,
            @NotBlank String parcelas
    ) {}
}
