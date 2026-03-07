package com.toolschallenge.toolschallenge.pagamento;

import com.toolschallenge.toolschallenge.pagamento.domain.dto.PagamentoRequestDto;
import com.toolschallenge.toolschallenge.pagamento.domain.dto.PagamentoResponseDto;
import com.toolschallenge.toolschallenge.pagamento.domain.entity.Pagamento;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<PagamentoResponseDto>> regatarTodosPagamentos () {
        List<Pagamento> pagamentos = pagamentoService.resgatarTodosPagamentos();

        return ResponseEntity.status(HttpStatus.OK).body(pagamentos.stream()
                .map(PagamentoMapper::toDto)
                .toList());
    }

    @PatchMapping("/{id}/estorno")
    public ResponseEntity<PagamentoResponseDto> estornarPagamento(@PathVariable String id) {
        Pagamento pagamento = pagamentoService.estornarPagamento(id);
        PagamentoResponseDto response =  PagamentoMapper.toDto(pagamento);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
