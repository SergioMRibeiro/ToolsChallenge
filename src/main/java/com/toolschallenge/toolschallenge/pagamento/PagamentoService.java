package com.toolschallenge.toolschallenge.pagamento;

import com.toolschallenge.toolschallenge.pagamento.domain.dto.PagamentoRequestDto;
import com.toolschallenge.toolschallenge.pagamento.domain.entity.Pagamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.toolschallenge.toolschallenge.pagamento.domain.enums.TransacaoStatusType.*;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    public Pagamento registerNewPayment (PagamentoRequestDto pagamentoRequest){
        Pagamento pagamento = PagamentoMapper.toEntity(pagamentoRequest);

        if (pagamento.getTransacao().getDescricao().getValor().compareTo(new BigDecimal("1000.00")) > 0) {
            pagamento.getTransacao().getDescricao().setTransacaoStatusType(NEGADO);
        } else {
            pagamento.getTransacao().getDescricao().setTransacaoStatusType(AUTORIZADO);
        }

        String nsu;
        do {
            nsu = String.valueOf((long) (Math.random() * 1_000_000_000L));
        } while (pagamentoRepository.existsByTransacao_Descricao_Nsu(nsu));
        pagamento.getTransacao().getDescricao().setNsu(nsu);

        String codigo;
        do {
            codigo = String.valueOf((long) (Math.random() * 1_000_000_000L));
        } while (pagamentoRepository.existsByTransacao_Descricao_CodigoAutorizacao(codigo));
        pagamento.getTransacao().getDescricao().setCodigoAutorizacao(codigo);

        return pagamentoRepository.save(pagamento);

    }

    public Optional<Pagamento> getById (Long id){
        return pagamentoRepository.findById(id);
    }

    public List<Pagamento> resgatarTodosPagamentos() {
        return pagamentoRepository.findAll();
    }

}
