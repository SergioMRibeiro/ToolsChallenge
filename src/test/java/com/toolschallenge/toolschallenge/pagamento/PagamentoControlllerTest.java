package com.toolschallenge.toolschallenge.pagamento;

import com.toolschallenge.toolschallenge.exception.ResourceNotFoundException;
import com.toolschallenge.toolschallenge.pagamento.api.PagamentoControlller;
import com.toolschallenge.toolschallenge.pagamento.api.dto.PagamentoRequestDto;
import com.toolschallenge.toolschallenge.pagamento.api.dto.PagamentoResponseDto;
import com.toolschallenge.toolschallenge.pagamento.domain.entity.Pagamento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoControlllerTest {

    @Mock
    private PagamentoService pagamentoService;

    @InjectMocks
    private PagamentoControlller pagamentoControlller;

    // --- helpers ---
    private Pagamento buildPagamento(String id) {
        Pagamento p = new Pagamento();
        com.toolschallenge.toolschallenge.pagamento.domain.valueobejct.Transacao t = new com.toolschallenge.toolschallenge.pagamento.domain.valueobejct.Transacao();
        t.setId(id);
        p.setTransacao(t);
        return p;
    }

    // --------------------------------------------------
    // registerPayment (POST) scenarios
    // --------------------------------------------------

    @Test
    void registerPayment_validRequest_returnsCreatedResponse() {
        // Arrange
        PagamentoRequestDto requestDto = mock(PagamentoRequestDto.class);
        Pagamento entity = buildPagamento("tx-1");
        Pagamento savedEntity = buildPagamento("tx-1");
        PagamentoResponseDto responseDto = mock(PagamentoResponseDto.class);

        try (MockedStatic<PagamentoMapper> mockedMapper = mockStatic(PagamentoMapper.class)) {
            mockedMapper.when(() -> PagamentoMapper.toEntity(eq(requestDto))).thenReturn(entity);
            when(pagamentoService.registerNewPayment(entity)).thenReturn(savedEntity);
            mockedMapper.when(() -> PagamentoMapper.toDto(eq(savedEntity))).thenReturn(responseDto);

            // Act
            var response = pagamentoControlller.registerPayment(requestDto);

            // Assert
            assertNotNull(response);
            assertEquals(201, response.getStatusCode().value());
            assertSame(responseDto, response.getBody());

            verify(pagamentoService, times(1)).registerNewPayment(entity);
            mockedMapper.verify(() -> PagamentoMapper.toEntity(eq(requestDto)), times(1));
            mockedMapper.verify(() -> PagamentoMapper.toDto(eq(savedEntity)), times(1));
        }
    }

    @Test
    void registerPayment_serviceThrows_runtimeException_propagated() {
        // Arrange
        PagamentoRequestDto requestDto = mock(PagamentoRequestDto.class);
        Pagamento entity = buildPagamento("tx-err");

        try (MockedStatic<PagamentoMapper> mockedMapper = mockStatic(PagamentoMapper.class)) {
            mockedMapper.when(() -> PagamentoMapper.toEntity(eq(requestDto))).thenReturn(entity);
            when(pagamentoService.registerNewPayment(entity)).thenThrow(new RuntimeException("boom"));

            // Act & Assert
            RuntimeException ex = assertThrows(RuntimeException.class, () -> pagamentoControlller.registerPayment(requestDto));
            assertEquals("boom", ex.getMessage());

            verify(pagamentoService, times(1)).registerNewPayment(entity);
            mockedMapper.verify(() -> PagamentoMapper.toEntity(eq(requestDto)), times(1));
        }
    }

    // --------------------------------------------------
    // getById (GET /{id}) scenarios
    // --------------------------------------------------

    @Test
    void getById_existingId_returnsOkAndDto() {
        // Arrange
        String id = "tx-get-1";
        Pagamento entity = buildPagamento(id);
        PagamentoResponseDto responseDto = mock(PagamentoResponseDto.class);

        try (MockedStatic<PagamentoMapper> mockedMapper = mockStatic(PagamentoMapper.class)) {
            when(pagamentoService.getById(id)).thenReturn(entity);
            mockedMapper.when(() -> PagamentoMapper.toDto(eq(entity))).thenReturn(responseDto);

            // Act
            var response = pagamentoControlller.getById(id);

            // Assert
            assertNotNull(response);
            assertEquals(200, response.getStatusCode().value());
            assertSame(responseDto, response.getBody());

            verify(pagamentoService, times(1)).getById(id);
            mockedMapper.verify(() -> PagamentoMapper.toDto(eq(entity)), times(1));
        }
    }

    @Test
    void getById_serviceThrows_resourceNotFound_propagated() {
        // Arrange
        String id = "missing";
        when(pagamentoService.getById(id)).thenThrow(new ResourceNotFoundException("Pagamento não encontrado"));

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> pagamentoControlller.getById(id));
        assertTrue(ex.getMessage().contains("Pagamento não encontrado"));
        verify(pagamentoService, times(1)).getById(id);
    }

    // --------------------------------------------------
    // resgatarTodosPagamentos (GET) scenarios
    // --------------------------------------------------

    @Test
    void regatarTodosPagamentos_nonEmpty_returnsListOfDtos() {
        // Arrange
        Pagamento p1 = buildPagamento("a");
        Pagamento p2 = buildPagamento("b");
        PagamentoResponseDto dto1 = mock(PagamentoResponseDto.class);
        PagamentoResponseDto dto2 = mock(PagamentoResponseDto.class);

        try (MockedStatic<PagamentoMapper> mockedMapper = mockStatic(PagamentoMapper.class)) {
            org.springframework.data.domain.Page<Pagamento> page = new org.springframework.data.domain.PageImpl<>(List.of(p1, p2), org.springframework.data.domain.PageRequest.of(0,10), 2);
            when(pagamentoService.resgatarTodosPagamentos(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);
            mockedMapper.when(() -> PagamentoMapper.toDto(eq(p1))).thenReturn(dto1);
            mockedMapper.when(() -> PagamentoMapper.toDto(eq(p2))).thenReturn(dto2);

            // Act
            var response = pagamentoControlller.regatarTodosPagamentos(0,10);

            // Assert
            assertNotNull(response);
            assertEquals(200, response.getStatusCode().value());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().getContent().size());
            assertTrue(response.getBody().getContent().contains(dto1));
            assertTrue(response.getBody().getContent().contains(dto2));

            verify(pagamentoService, times(1)).resgatarTodosPagamentos(any(org.springframework.data.domain.Pageable.class));
            mockedMapper.verify(() -> PagamentoMapper.toDto(eq(p1)), times(1));
            mockedMapper.verify(() -> PagamentoMapper.toDto(eq(p2)), times(1));
        }
    }

    @Test
    void regatarTodosPagamentos_empty_returnsEmptyList() {
        // Arrange
        try (MockedStatic<PagamentoMapper> mockedMapper = mockStatic(PagamentoMapper.class)) {
            org.springframework.data.domain.Page<Pagamento> page = new org.springframework.data.domain.PageImpl<>(List.of(), org.springframework.data.domain.PageRequest.of(0,10), 0);
            when(pagamentoService.resgatarTodosPagamentos(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

            // Act
            var response = pagamentoControlller.regatarTodosPagamentos(0,10);

            // Assert
            assertNotNull(response);
            assertEquals(200, response.getStatusCode().value());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().getContent().isEmpty());

            verify(pagamentoService, times(1)).resgatarTodosPagamentos(any(org.springframework.data.domain.Pageable.class));
            mockedMapper.verifyNoInteractions();
        }
    }

    // --------------------------------------------------
    // estornarPagamento (PATCH) scenarios
    // --------------------------------------------------

    @Test
    void estornarPagamento_existingId_returnsOkAndDto() {
        // Arrange
        String id = "tx-estorno-1";
        Pagamento entity = buildPagamento(id);
        PagamentoResponseDto responseDto = mock(PagamentoResponseDto.class);

        try (MockedStatic<PagamentoMapper> mockedMapper = mockStatic(PagamentoMapper.class)) {
            when(pagamentoService.estornarPagamento(id)).thenReturn(entity);
            mockedMapper.when(() -> PagamentoMapper.toDto(eq(entity))).thenReturn(responseDto);

            // Act
            var response = pagamentoControlller.estornarPagamento(id);

            // Assert
            assertNotNull(response);
            assertEquals(200, response.getStatusCode().value());
            assertSame(responseDto, response.getBody());

            verify(pagamentoService, times(1)).estornarPagamento(id);
            mockedMapper.verify(() -> PagamentoMapper.toDto(eq(entity)), times(1));
        }
    }

    @Test
    void estornarPagamento_nonExisting_throwsResourceNotFound_propagated() {
        // Arrange
        String id = "missing-estorno";
        when(pagamentoService.estornarPagamento(id)).thenThrow(new ResourceNotFoundException("Pagamento não encontrado"));

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> pagamentoControlller.estornarPagamento(id));
        assertTrue(ex.getMessage().contains("Pagamento não encontrado"));
        verify(pagamentoService, times(1)).estornarPagamento(id);
    }

}
