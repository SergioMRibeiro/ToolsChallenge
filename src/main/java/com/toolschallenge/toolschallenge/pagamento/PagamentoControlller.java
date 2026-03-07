package com.toolschallenge.toolschallenge.pagamento;

import com.toolschallenge.toolschallenge.pagamento.domain.dto.PagamentoRequestDto;
import com.toolschallenge.toolschallenge.pagamento.domain.dto.PagamentoResponseDto;
import com.toolschallenge.toolschallenge.pagamento.domain.entity.Pagamento;
import jakarta.validation.Valid;
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
     public PagamentoResponseDto registerPayment(@Valid @RequestBody PagamentoRequestDto pagamentoRequest) {
            Pagamento pagamento = pagamentoService.registerNewPayment(pagamentoRequest);
        return PagamentoMapper.toDto(pagamento);

     }

     @GetMapping("/{id}")
    public PagamentoResponseDto  getById (@PathVariable String id) {
         Pagamento pagamento = pagamentoService.getById(id);
         return PagamentoMapper.toDto(pagamento);

     }

    @GetMapping
    public List<PagamentoResponseDto> regatarTodosPagamentos () {
        List<Pagamento> pagamentos = pagamentoService.resgatarTodosPagamentos();

        return pagamentos.stream()
                .map(PagamentoMapper::toDto)
                .toList();
    }

    @PatchMapping("/{id}/estorno")
    public PagamentoResponseDto estornarPagamento(@PathVariable String id) {
        Pagamento pagamento = pagamentoService.estornarPagamento(id);
        return PagamentoMapper.toDto(pagamento);
    }

}
