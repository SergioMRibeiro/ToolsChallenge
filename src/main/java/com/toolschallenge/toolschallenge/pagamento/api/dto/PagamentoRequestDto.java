package com.toolschallenge.toolschallenge.pagamento.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PagamentoRequestDto(
        @NotNull @Valid Transacao transacao
) {
    public record Transacao(
            // deve conter apenas números, sem espaços ou caracteres especiais, e ter exatamente 16 dígitos
            @NotBlank String cartao,
            @Pattern(regexp = "^\\d{15}$", message = "ID deve conter exatamente 15 dígitos numéricos")
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
