package com.toolschallenge.toolschallenge.pagamento;

import com.toolschallenge.toolschallenge.pagamento.domain.dto.PagamentoRequestDto;
import com.toolschallenge.toolschallenge.pagamento.domain.dto.PagamentoResponseDto;
import com.toolschallenge.toolschallenge.pagamento.domain.entity.Pagamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/payment")
public class PagamentoControlller {
    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping
     public PagamentoResponseDto registerPayment(@RequestBody PagamentoRequestDto pagamentoRequest) {
            Pagamento pagamento = pagamentoService.registerNewPayment(pagamentoRequest);
        return PagamentoMapper.toResponseDto(pagamento);

     }

     @GetMapping("/{id}")
    public Optional<Pagamento> getById (@PathVariable Long id) {
        return  pagamentoService.getById(id);
     }

    @GetMapping
    public List<PagamentoResponseDto> regatarTodosPagamentos () {
        List<Pagamento> pagamentos = pagamentoService.resgatarTodosPagamentos();

        return pagamentos.stream()
                .map(PagamentoMapper::toResponseDto)
                .toList();
    }
}
