package com.toolschallenge.toolschallenge.pagamento;

import com.toolschallenge.toolschallenge.pagamento.domain.dto.PagamentoRequestDto;
import com.toolschallenge.toolschallenge.pagamento.domain.dto.PagamentoResponseDto;
import com.toolschallenge.toolschallenge.pagamento.domain.entity.Pagamento;
import com.toolschallenge.toolschallenge.pagamento.domain.enums.MetodoPagamentoType;
import com.toolschallenge.toolschallenge.pagamento.domain.valueobejct.Descricao;
import com.toolschallenge.toolschallenge.pagamento.domain.valueobejct.MetodoPagamento;
import com.toolschallenge.toolschallenge.pagamento.domain.valueobejct.Transacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PagamentoMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static Pagamento toEntity(PagamentoRequestDto request) {
        Pagamento pagamento = new Pagamento();

        Descricao descricao = new Descricao();
        Transacao transacao = new Transacao();
        transacao.setDescricao(descricao);

        transacao.setCartao(request.transacao().cartao());
        transacao.setId(request.transacao().id());

        pagamento.setTransacao(transacao);

        pagamento.setMetodoPagamento(new MetodoPagamento());

        String rawValor = request.transacao().descricao().valor();
        if (rawValor == null) rawValor = "0";
        String normalized = rawValor.replace(".", "").replace(",", ".");
        BigDecimal valor = new BigDecimal(normalized);
        pagamento.getTransacao().getDescricao().setValor(valor);

        LocalDateTime dataHora = LocalDateTime.parse(request.transacao().descricao().dataHora(), FORMATTER);
        pagamento.getTransacao().getDescricao().setDataHora(dataHora);

        pagamento.getTransacao().getDescricao().setEstabelecimento(request.transacao().descricao().estabelecimento());

        pagamento.getMetodoPagamento().setMetodoPagamentoType(MetodoPagamentoType.valueOf(request.formaPagamento().tipo()));
        pagamento.getMetodoPagamento().setParcelas(Integer.parseInt(request.formaPagamento().parcelas()));
        return pagamento;
    }

    public static PagamentoResponseDto toResponseDto(Pagamento pagamento) {
        return new PagamentoResponseDto(
                new PagamentoResponseDto.Transacao(
                        pagamento.getTransacao().getCartao(),
                        pagamento.getTransacao().getId(),
                        new PagamentoResponseDto.DescricaoDto(
                                pagamento.getTransacao().getDescricao().getValor().toString(),
                                pagamento.getTransacao().getDescricao().getDataHora().format(FORMATTER),
                                pagamento.getTransacao().getDescricao().getEstabelecimento(),
                                pagamento.getTransacao().getDescricao().getNsu(),
                                pagamento.getTransacao().getDescricao().getCodigoAutorizacao(),
                                pagamento.getTransacao().getDescricao().getTransacaoStatusType() != null ? pagamento.getTransacao().getDescricao().getTransacaoStatusType().name() : null
                        )
                ),
                new PagamentoResponseDto.FormaPagamento(
                        pagamento.getMetodoPagamento().getMetodoPagamentoType() != null ? pagamento.getMetodoPagamento().getMetodoPagamentoType().name() : null,
                        String.valueOf(pagamento.getMetodoPagamento().getParcelas())
                )
        );
    }
}
