package com.toolschallenge.toolschallenge.pagamento;

import com.toolschallenge.toolschallenge.pagamento.api.dto.PagamentoRequestDto;
import com.toolschallenge.toolschallenge.pagamento.api.dto.PagamentoResponseDto;
import com.toolschallenge.toolschallenge.pagamento.domain.entity.Pagamento;
import com.toolschallenge.toolschallenge.pagamento.domain.valueobejct.Descricao;
import com.toolschallenge.toolschallenge.pagamento.domain.valueobejct.MetodoPagamento;
import com.toolschallenge.toolschallenge.pagamento.domain.valueobejct.Transacao;
import com.toolschallenge.toolschallenge.pagamento.domain.enums.MetodoPagamentoType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PagamentoMapper {

    public static Pagamento toEntity(PagamentoRequestDto request) {
        Pagamento pagamento = new Pagamento();
        Transacao transacao = new Transacao();
        Descricao descricao = new Descricao();

        // Normalização e parse do valor ( "500,00" e "500.00")
        String rawValor = request.transacao().descricao().valor();
        String normalized = rawValor.replace(".", "").replace(",", ".");
        descricao.setValor(new BigDecimal(normalized));

        // Formatação e parse da data e hora dd/MM/yyyy HH:mm:ss
        String dataHoraRaw = request.transacao().descricao().dataHora();
        LocalDateTime dataHora;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            dataHora = LocalDateTime.parse(dataHoraRaw, formatter);
        } catch (Exception e) {
            dataHora = LocalDateTime.parse(dataHoraRaw);
        }
        descricao.setDataHora(dataHora);
        descricao.setEstabelecimento(request.transacao().descricao().estabelecimento());

        transacao.setDescricao(descricao);
        transacao.setCartao(request.transacao().cartao());
        transacao.setId(request.transacao().id());

        MetodoPagamento metodoPagamento = new MetodoPagamento();

        // Tratamento do tipo de pagamento para garantir que seja compatível com o enum
        String rawTipo = request.transacao().formaPagamento().tipo();
        String normalizedTipo = normalizeEnumName(rawTipo);
        metodoPagamento.setMetodoPagamentoType(MetodoPagamentoType.valueOf(normalizedTipo));
        metodoPagamento.setParcelas(Integer.parseInt(request.transacao().formaPagamento().parcelas()));
        transacao.setMetodoPagamento(metodoPagamento);

        pagamento.setTransacao(transacao);

        return pagamento;
    }

    private static String normalizeEnumName(String raw) {
        if (raw == null) return "";
        return raw.trim().replaceAll("\\s+", "_").toUpperCase(Locale.ROOT);
    }


    public static PagamentoResponseDto toDto(Pagamento pagamento) {
        return PagamentoResponseDto.from(pagamento);
    }
}
