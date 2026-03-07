package com.toolschallenge.toolschallenge.pagamento;

import com.toolschallenge.toolschallenge.exception.ConflictException;
import com.toolschallenge.toolschallenge.exception.ResourceNotFoundException;
import com.toolschallenge.toolschallenge.pagamento.domain.dto.PagamentoRequestDto;
import com.toolschallenge.toolschallenge.pagamento.domain.entity.Pagamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.toolschallenge.toolschallenge.pagamento.domain.enums.TransacaoStatusType.*;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    public Pagamento registerNewPayment (Pagamento pagamento){

        // Simulação de regra de negócio: se o valor for maior que R$ 10000,00 de uma só vez, o pagamento é negado, caso contrário, é autorizado.
        if (pagamento.getTransacao().getDescricao().getValor().compareTo(new BigDecimal("10000.00")) > 0) {
            pagamento.getTransacao().getDescricao().setTransacaoStatusType(NEGADO);
        } else {
            pagamento.getTransacao().getDescricao().setTransacaoStatusType(AUTORIZADO);
        }

        String nsu = generateUniqueNumber(
                n -> pagamentoRepository.existsByTransacao_Descricao_Nsu(n),
                10
        );

        pagamento.getTransacao().getDescricao().setNsu(nsu);

        String codigo = generateUniqueNumber(
                n -> pagamentoRepository.existsByTransacao_Descricao_CodigoAutorizacao(n),
                9
        );
        pagamento.getTransacao().getDescricao().setCodigoAutorizacao(codigo);

        return pagamentoRepository.save(pagamento);

    }

    public Pagamento getById (String id){
        Pagamento pagamento = pagamentoRepository.getByTransacao_Id(id);
        if (pagamento == null) {
            throw new ResourceNotFoundException("Pagamento não encontrado para id: " + id);
        }
        return pagamento;
    }

    public List<Pagamento> resgatarTodosPagamentos() {
        return pagamentoRepository.findAll();
    }

    public Pagamento estornarPagamento(String id) {
        Pagamento pagamento = pagamentoRepository.getByTransacao_Id(id);
        if (pagamento == null) {
            throw new ResourceNotFoundException("Pagamento não encontrado para id: " + id);
        }
        pagamento.getTransacao().getDescricao().setTransacaoStatusType(CANCELADO);

        return pagamentoRepository.save(pagamento);

    }


    /** Código para nsu (10 dígitos) e código de autorização (9 dígitos) */
    private String generateUniqueNumber(Predicate<String> existsCheck, int length) {

        long min = (long) Math.pow(10, length - 1);
        long max = (long) Math.pow(10, length);

        return Stream.generate(() -> ThreadLocalRandom.current().nextLong(min, max))
                .map(String::valueOf)
                .filter(n -> !existsCheck.test(n))
                .findFirst()
                .orElseThrow(() -> new ConflictException("Não foi possível gerar um número único após várias tentativas"));
    }

}
