package com.toolschallenge.toolschallenge.pagamento;

import com.toolschallenge.toolschallenge.exception.ConflictException;
import com.toolschallenge.toolschallenge.exception.ResourceNotFoundException;
import com.toolschallenge.toolschallenge.pagamento.domain.entity.Pagamento;
import com.toolschallenge.toolschallenge.pagamento.domain.valueobejct.Descricao;
import com.toolschallenge.toolschallenge.pagamento.domain.valueobejct.Transacao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.toolschallenge.toolschallenge.pagamento.domain.enums.TransacaoStatusType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @InjectMocks
    private PagamentoService pagamentoService;

    // --- helpers ---
    private Pagamento buildPagamento(BigDecimal value, String transacaoId) {
        Pagamento p = new Pagamento();
        Transacao t = new Transacao();
        Descricao d = new Descricao();

        d.setValor(value);
        d.setDataHora(LocalDateTime.of(2023,1,1,12,0,0));
        d.setEstabelecimento("Loja");
        d.setNsu(null);
        d.setCodigoAutorizacao(null);
        d.setTransacaoStatusType(null);

        t.setCartao("1111-2222-3333-4444");
        t.setId(transacaoId);
        t.setDescricao(d);

        p.setTransacao(t);
        return p;
    }

    // --------------------------------------------------
    // registerNewPayment scenarios (updated to pass Pagamento directly)
    // --------------------------------------------------

    @Test
    void registerNewPayment_valueBelowLimit_authorizedAndSaved() {
        // Arrange
        Pagamento pagamento = buildPagamento(new BigDecimal("500.00"), "1234567891012123");

        when(pagamentoRepository.existsByTransacao_Descricao_Nsu(anyString())).thenReturn(false);
        when(pagamentoRepository.existsByTransacao_Descricao_CodigoAutorizacao(anyString())).thenReturn(false);
        when(pagamentoRepository.save(any())).thenReturn(pagamento);

        // Act
        Pagamento result = pagamentoService.registerNewPayment(pagamento);

        // Assert
        assertNotNull(result);
        assertEquals(AUTORIZADO, result.getTransacao().getDescricao().getTransacaoStatusType());
        assertNotNull(result.getTransacao().getDescricao().getNsu());
        assertEquals(10, result.getTransacao().getDescricao().getNsu().length());
        assertNotNull(result.getTransacao().getDescricao().getCodigoAutorizacao());
        assertEquals(9, result.getTransacao().getDescricao().getCodigoAutorizacao().length());

        verify(pagamentoRepository, atLeastOnce()).existsByTransacao_Descricao_Nsu(anyString());
        verify(pagamentoRepository, atLeastOnce()).existsByTransacao_Descricao_CodigoAutorizacao(anyString());
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }

    @Test
    void registerNewPayment_valueEqualLimit_authorized() {
        // Arrange
        Pagamento pagamento = buildPagamento(new BigDecimal("10000.00"), "tx-10000");

        when(pagamentoRepository.existsByTransacao_Descricao_Nsu(anyString())).thenReturn(false);
        when(pagamentoRepository.existsByTransacao_Descricao_CodigoAutorizacao(anyString())).thenReturn(false);
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Pagamento result = pagamentoService.registerNewPayment(pagamento);

        // Assert
        assertEquals(AUTORIZADO, result.getTransacao().getDescricao().getTransacaoStatusType());
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }

    @Test
    void registerNewPayment_valueAboveLimit_negadoAndSaved() {
        // Arrange
        Pagamento pagamento = buildPagamento(new BigDecimal("15000.00"), "tx-2");

        when(pagamentoRepository.existsByTransacao_Descricao_Nsu(anyString())).thenReturn(false);
        when(pagamentoRepository.existsByTransacao_Descricao_CodigoAutorizacao(anyString())).thenReturn(false);
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Pagamento result = pagamentoService.registerNewPayment(pagamento);

        // Assert
        assertEquals(NEGADO, result.getTransacao().getDescricao().getTransacaoStatusType());
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }

    @Test
    void registerNewPayment_whenSaveThrows_exceptionPropagated() {
        // Arrange
        Pagamento pagamento = buildPagamento(new BigDecimal("10.00"), "tx-3");

        when(pagamentoRepository.existsByTransacao_Descricao_Nsu(anyString())).thenReturn(false);
        when(pagamentoRepository.existsByTransacao_Descricao_CodigoAutorizacao(anyString())).thenReturn(false);
        when(pagamentoRepository.save(any(Pagamento.class))).thenThrow(new RuntimeException("DB down"));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> pagamentoService.registerNewPayment(pagamento));
        assertEquals("DB down", ex.getMessage());
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }

    // --------------------------------------------------
    // getById scenarios
    // --------------------------------------------------

    @Test
    void getById_existingId_returnsPayment() {
        // Arrange
        Pagamento pagamento = buildPagamento(new BigDecimal("100.00"), "tx-get-1");
        when(pagamentoRepository.getByTransacao_Id("tx-get-1")).thenReturn(pagamento);

        // Act
        Pagamento found = pagamentoService.getById("tx-get-1");

        // Assert
        assertSame(pagamento, found);
        verify(pagamentoRepository, times(1)).getByTransacao_Id("tx-get-1");
    }

    @Test
    void getById_nonExisting_throwsResourceNotFoundException() {
        // Arrange
        when(pagamentoRepository.getByTransacao_Id("missing")).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> pagamentoService.getById("missing"));
        assertTrue(ex.getMessage().contains("Pagamento não encontrado"));
        verify(pagamentoRepository, times(1)).getByTransacao_Id("missing");
    }

    // --------------------------------------------------
    // resgatarTodosPagamentos scenarios
    // --------------------------------------------------

    @Test
    void resgatarTodosPagamentos_withValidPageable_returnsPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2);
        Pagamento p1 = buildPagamento(new BigDecimal("10.00"), "a");
        Pagamento p2 = buildPagamento(new BigDecimal("20.00"), "b");

        Page<Pagamento> page = new PageImpl<>(List.of(p1, p2), pageable, 10);
        when(pagamentoRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<Pagamento> result = pagamentoService.resgatarTodosPagamentos(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().contains(p1));
        assertTrue(result.getContent().contains(p2));

        verify(pagamentoRepository, times(1)).findAll(pageable);
    }

    @Test
    void resgatarTodosPagamentos_nullPageable_throwsIllegalArgumentException() {
        // Arrange
        when(pagamentoRepository.findAll((Pageable) isNull())).thenThrow(new IllegalArgumentException("pageable cannot be null"));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> pagamentoService.resgatarTodosPagamentos(null));
        assertEquals("pageable cannot be null", ex.getMessage());

        verify(pagamentoRepository, times(1)).findAll((Pageable) isNull());
    }

    // --------------------------------------------------
    // estornarPagamento scenarios
    // --------------------------------------------------

    @Test
    void estornarPagamento_existing_setsCancelledAndSaves() {
        // Arrange
        Pagamento pagamento = buildPagamento(new BigDecimal("200.00"), "tx-estorno-1");
        pagamento.getTransacao().getDescricao().setTransacaoStatusType(AUTORIZADO);

        when(pagamentoRepository.getByTransacao_Id("tx-estorno-1")).thenReturn(pagamento);
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Pagamento result = pagamentoService.estornarPagamento("tx-estorno-1");

        // Assert
        assertEquals(CANCELADO, result.getTransacao().getDescricao().getTransacaoStatusType());
        verify(pagamentoRepository, times(1)).getByTransacao_Id("tx-estorno-1");
        verify(pagamentoRepository, times(1)).save(result);
    }

    @Test
    void estornarPagamento_nonExisting_throwsResourceNotFoundException() {
        // Arrange
        when(pagamentoRepository.getByTransacao_Id("missing-estorno")).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> pagamentoService.estornarPagamento("missing-estorno"));
        assertTrue(ex.getMessage().contains("Pagamento não encontrado"));
        verify(pagamentoRepository, times(1)).getByTransacao_Id("missing-estorno");
        verify(pagamentoRepository, never()).save(any());
    }

    @Test
    void estornarPagamento_alreadyCancelled_throwsConflictException() {
        // Arrange
        Pagamento pagamento = buildPagamento(new BigDecimal("200.00"), "tx-estorno-2");
        pagamento.getTransacao().getDescricao().setTransacaoStatusType(CANCELADO);

        when(pagamentoRepository.getByTransacao_Id("tx-estorno-2")).thenReturn(pagamento);

        // Act & Assert
        ConflictException ex = assertThrows(ConflictException.class, () -> pagamentoService.estornarPagamento("tx-estorno-2"));
        assertTrue(ex.getMessage().contains("Pagamentos já cancelado anteriormente"));
        verify(pagamentoRepository, times(1)).getByTransacao_Id("tx-estorno-2");
        verify(pagamentoRepository, never()).save(any());
    }

}