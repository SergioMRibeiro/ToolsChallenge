package com.toolschallenge.toolschallenge.pagamento.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.toolschallenge.toolschallenge.pagamento.domain.entity.Pagamento;
import com.toolschallenge.toolschallenge.pagamento.domain.valueobejct.Descricao;
import com.toolschallenge.toolschallenge.pagamento.domain.valueobejct.MetodoPagamento;
import com.toolschallenge.toolschallenge.pagamento.domain.enums.TransacaoStatusType;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * DTO de resposta que representa o JSON de saída solicitado.
 */
public record PagamentoResponseDto(
        @JsonProperty("transacao") Transacao transacao
) {

    public record Transacao(
            @JsonProperty("cartao") String cartao,
            String id,
            @JsonProperty("descricao") DescricaoDto descricao,
            @JsonProperty("formaPagamento") FormaPagamento formaPagamento
    ) {}

    public record DescricaoDto(
            String valor,
            @JsonProperty("dataHora") String dataHora,
            String estabelecimento,
            String nsu,
            @JsonProperty("codigoAutorizacao") String codigoAutorizacao,
            String status
    ) {}

    public record FormaPagamento(
            String tipo,
            String parcelas
    ) {}

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DecimalFormat PT_BR_DECIMAL;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.forLanguageTag("pt-BR"));
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        PT_BR_DECIMAL = new DecimalFormat("#,##0.00", symbols);
        PT_BR_DECIMAL.setMinimumFractionDigits(2);
        PT_BR_DECIMAL.setMaximumFractionDigits(2);
    }


    public static PagamentoResponseDto from(Pagamento pagamento) {
        return from(pagamento, null, null);
    }

    public static PagamentoResponseDto from(Pagamento pagamento, String cartao, String id) {
        if (pagamento == null) throw new IllegalArgumentException("pagamento cannot be null");

        Descricao desc = pagamento.getTransacao().getDescricao();
        MetodoPagamento pm = pagamento.getTransacao().getMetodoPagamento();

        // valor
        String valorStr = "0,00";
        if (desc != null && desc.getValor() != null) {
            BigDecimal v = desc.getValor();
            valorStr = PT_BR_DECIMAL.format(v);
        }

        // dataHora
        String dataHora = "";
        if (desc != null && desc.getDataHora() != null) {
            LocalDateTime ldt = desc.getDataHora();
            dataHora = ldt.format(DATE_FORMATTER);
        }

        String estabelecimento = desc != null && desc.getEstabelecimento() != null ? desc.getEstabelecimento() : "";
        String nsu = desc != null && desc.getNsu() != null ? desc.getNsu() : "";
        String codigoAutorizacao = desc != null && desc.getCodigoAutorizacao() != null ? desc.getCodigoAutorizacao() : "";

        String status = "";
        if (desc != null && desc.getTransacaoStatusType() != null) {
            TransacaoStatusType st = desc.getTransacaoStatusType();
            if (st == TransacaoStatusType.AUTORIZADO) status = "AUTORIZADO";
            else if (st == TransacaoStatusType.NEGADO) status = "NEGADO";
            else status = st.name();
        }

        DescricaoDto respostaDesc = new DescricaoDto(valorStr, dataHora, estabelecimento, nsu, codigoAutorizacao, status);

        // Normalize response tipo para o mesmo substituir "_" por espaço e manter o captalizado
        String tipo = "";
        if (pm != null && pm.getMetodoPagamentoType() != null) {
            tipo = pm.getMetodoPagamentoType().name();
            tipo = tipo.trim().replaceAll("_+", " ");
        }
        String parcelas = pm != null && pm.getParcelas() != null ? String.valueOf(pm.getParcelas()) : "";
        FormaPagamento respostaPm = new FormaPagamento(tipo, parcelas);

        Transacao respostaTrans = new Transacao(cartao != null ? cartao : pagamento.getTransacao().getCartao(), id != null ? id : pagamento.getTransacao().getId(), respostaDesc, respostaPm);

        return new PagamentoResponseDto(respostaTrans);
    }
}
