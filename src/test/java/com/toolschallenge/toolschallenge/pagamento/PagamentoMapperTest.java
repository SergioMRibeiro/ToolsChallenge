package com.toolschallenge.toolschallenge.pagamento;

import com.toolschallenge.toolschallenge.pagamento.domain.dto.PagamentoRequestDto;
import com.toolschallenge.toolschallenge.pagamento.domain.dto.PagamentoResponseDto;
import com.toolschallenge.toolschallenge.pagamento.domain.entity.Pagamento;
import com.toolschallenge.toolschallenge.pagamento.domain.enums.MetodoPagamentoType;
import com.toolschallenge.toolschallenge.pagamento.domain.valueobejct.Descricao;
import com.toolschallenge.toolschallenge.pagamento.domain.valueobejct.MetodoPagamento;
import com.toolschallenge.toolschallenge.pagamento.domain.valueobejct.Transacao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoMapperTest {

    // --------------------------------------------------
    // toEntity - happy paths
    // --------------------------------------------------

    @Test
    void toEntity_validRequest_parsesAllFields() {
        // Arrange
        PagamentoRequestDto.FormaPagamento forma = new PagamentoRequestDto.FormaPagamento("avista", "2");
        PagamentoRequestDto.Descricao desc = new PagamentoRequestDto.Descricao("500,00", "01/01/2023 12:00:00", "Loja");
        PagamentoRequestDto.Transacao trans = new PagamentoRequestDto.Transacao("1111-2222-3333-4444", "tx-1", desc, forma);
        PagamentoRequestDto request = new PagamentoRequestDto(trans);

        // Act
        Pagamento result = PagamentoMapper.toEntity(request);

        // Assert
        assertNotNull(result);
        assertEquals("1111-2222-3333-4444", result.getTransacao().getCartao());
        assertEquals("tx-1", result.getTransacao().getId());
        assertNotNull(result.getTransacao().getDescricao());
        assertEquals(0, result.getTransacao().getDescricao().getValor().compareTo(new BigDecimal("500.00")));
        assertEquals(LocalDateTime.of(2023, 1, 1, 12, 0), result.getTransacao().getDescricao().getDataHora());
        assertEquals("Loja", result.getTransacao().getDescricao().getEstabelecimento());
        assertNotNull(result.getTransacao().getMetodoPagamento());
        assertEquals(MetodoPagamentoType.AVISTA, result.getTransacao().getMetodoPagamento().getMetodoPagamentoType());
        assertEquals(2, result.getTransacao().getMetodoPagamento().getParcelas());
    }

    @Test
    void toEntity_valueWithDotGrouping_parsesCorrectly() {
        // Arrange
        PagamentoRequestDto.FormaPagamento forma = new PagamentoRequestDto.FormaPagamento("AVISTA", "1");
        PagamentoRequestDto.Descricao desc = new PagamentoRequestDto.Descricao("1.234,56", "01/01/2023 12:00:00", "Loja");
        PagamentoRequestDto.Transacao trans = new PagamentoRequestDto.Transacao("card", "tx-2", desc, forma);
        PagamentoRequestDto request = new PagamentoRequestDto(trans);

        // Act
        Pagamento result = PagamentoMapper.toEntity(request);

        // Assert
        assertEquals(0, result.getTransacao().getDescricao().getValor().compareTo(new BigDecimal("1234.56")));
    }

    @Test
    void toEntity_isoDate_parsedUsingFallback() {
        // Arrange
        PagamentoRequestDto.FormaPagamento forma = new PagamentoRequestDto.FormaPagamento("AVISTA", "1");
        PagamentoRequestDto.Descricao desc = new PagamentoRequestDto.Descricao("10,00", "2023-01-01T12:00:00", "Loja");
        PagamentoRequestDto.Transacao trans = new PagamentoRequestDto.Transacao("card", "tx-iso", desc, forma);
        PagamentoRequestDto request = new PagamentoRequestDto(trans);

        // Act
        Pagamento result = PagamentoMapper.toEntity(request);

        // Assert
        assertEquals(LocalDateTime.of(2023, 1, 1, 12, 0), result.getTransacao().getDescricao().getDataHora());
    }

    @Test
    void toEntity_tipoWithSpaces_normalizesToEnum() {
        // Arrange - "PARCELADO EMISSOR" should map to PARCELADO_EMISSOR
        PagamentoRequestDto.FormaPagamento forma = new PagamentoRequestDto.FormaPagamento("PARCELADO EMISSOR", "3");
        PagamentoRequestDto.Descricao desc = new PagamentoRequestDto.Descricao("20,00", "01/01/2023 12:00:00", "Loja");
        PagamentoRequestDto.Transacao trans = new PagamentoRequestDto.Transacao("card", "tx-3", desc, forma);
        PagamentoRequestDto request = new PagamentoRequestDto(trans);

        // Act
        Pagamento result = PagamentoMapper.toEntity(request);

        // Assert
        assertEquals(MetodoPagamentoType.PARCELADO_EMISSOR, result.getTransacao().getMetodoPagamento().getMetodoPagamentoType());
        assertEquals(3, result.getTransacao().getMetodoPagamento().getParcelas());
    }

    // --------------------------------------------------
    // toEntity - error scenarios
    // --------------------------------------------------

    @Test
    void toEntity_unknownTipo_throwsIllegalArgumentException() {
        // Arrange
        PagamentoRequestDto.FormaPagamento forma = new PagamentoRequestDto.FormaPagamento("PARCELA LOJA", "1");
        PagamentoRequestDto.Descricao desc = new PagamentoRequestDto.Descricao("10,00", "01/01/2023 12:00:00", "Loja");
        PagamentoRequestDto.Transacao trans = new PagamentoRequestDto.Transacao("card", "tx-4", desc, forma);
        PagamentoRequestDto request = new PagamentoRequestDto(trans);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> PagamentoMapper.toEntity(request));
    }

    @Test
    void toEntity_invalidParcelas_throwsNumberFormatException() {
        // Arrange
        PagamentoRequestDto.FormaPagamento forma = new PagamentoRequestDto.FormaPagamento("AVISTA", "X");
        PagamentoRequestDto.Descricao desc = new PagamentoRequestDto.Descricao("10,00", "01/01/2023 12:00:00", "Loja");
        PagamentoRequestDto.Transacao trans = new PagamentoRequestDto.Transacao("card", "tx-5", desc, forma);
        PagamentoRequestDto request = new PagamentoRequestDto(trans);

        // Act & Assert
        assertThrows(NumberFormatException.class, () -> PagamentoMapper.toEntity(request));
    }

    // --------------------------------------------------
    // toDto scenarios
    // --------------------------------------------------

    @Test
    void toDto_withMetodoPagamento_returnsDtoWithNormalizedTipoAndParcelas() {
        // Arrange
        Pagamento pagamento = new Pagamento();
        Transacao t = new Transacao();
        Descricao d = new Descricao();
        MetodoPagamento pm = new MetodoPagamento();

        d.setValor(new BigDecimal("100.00"));
        d.setDataHora(LocalDateTime.of(2023, 2, 2, 14, 30));
        d.setEstabelecimento("Loja");
        t.setDescricao(d);
        t.setCartao("card-1");
        t.setId("id-1");

        pm.setMetodoPagamentoType(MetodoPagamentoType.PARCELADO_EMISSOR);
        pm.setParcelas(5);
        t.setMetodoPagamento(pm);
        pagamento.setTransacao(t);

        // Act
        PagamentoResponseDto dto = PagamentoMapper.toDto(pagamento);

        // Assert
        assertNotNull(dto);
        assertEquals("PARCELADO EMISSOR", dto.transacao().formaPagamento().tipo());
        assertEquals("5", dto.transacao().formaPagamento().parcelas());
    }

    @Test
    void toDto_nullPagamento_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> PagamentoMapper.toDto(null));
    }

}