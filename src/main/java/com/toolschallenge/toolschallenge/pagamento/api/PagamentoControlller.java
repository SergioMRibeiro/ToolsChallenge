package com.toolschallenge.toolschallenge.pagamento.api;

import com.toolschallenge.toolschallenge.pagamento.PagamentoMapper;
import com.toolschallenge.toolschallenge.pagamento.PagamentoService;
import com.toolschallenge.toolschallenge.pagamento.api.dto.PagamentoRequestDto;
import com.toolschallenge.toolschallenge.pagamento.api.dto.PagamentoResponseDto;
import com.toolschallenge.toolschallenge.pagamento.domain.entity.Pagamento;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pagamento")
public class PagamentoControlller {

    private final PagamentoService pagamentoService;

    public PagamentoControlller(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping
     public ResponseEntity<PagamentoResponseDto> registerPayment(@Valid @RequestBody PagamentoRequestDto pagamentoRequest) {
        Pagamento pagamento = PagamentoMapper.toEntity(pagamentoRequest);
        PagamentoResponseDto response =  PagamentoMapper.toDto(pagamentoService.registerNewPayment(pagamento));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

     }

     @GetMapping("/{id}")
    public ResponseEntity<PagamentoResponseDto>  getById (@PathVariable String id) {
         Pagamento pagamento = pagamentoService.getById(id);
         return ResponseEntity.status(HttpStatus.OK).body(PagamentoMapper.toDto(pagamento));

     }

    @GetMapping
    public ResponseEntity<Page<PagamentoResponseDto>> regatarTodosPagamentos (
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size // O limite em 10 é para evitar que o cliente solicite uma quantidade muito grande de registros, o que poderia sobrecarregar o sistema.
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Pagamento> pagamentosPage = pagamentoService.resgatarTodosPagamentos(pageable);
        List<PagamentoResponseDto> dtos = pagamentosPage.stream()
                .map(PagamentoMapper::toDto)
                .collect(Collectors.toList());
        Page<PagamentoResponseDto> dtoPage = new PageImpl<>(dtos, pageable, pagamentosPage.getTotalElements());

        return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
    }

    @PatchMapping("/{id}/estorno")
    public ResponseEntity<PagamentoResponseDto> estornarPagamento(@PathVariable String id) {
        Pagamento pagamento = pagamentoService.estornarPagamento(id);
        PagamentoResponseDto response =  PagamentoMapper.toDto(pagamento);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
