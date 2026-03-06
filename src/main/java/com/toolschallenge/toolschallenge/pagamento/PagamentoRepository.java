package com.toolschallenge.toolschallenge.pagamento;

import com.toolschallenge.toolschallenge.pagamento.domain.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    // the nsu is inside pagamento.transacao.descricao
    boolean existsByTransacao_Descricao_Nsu(String nsu);

    boolean existsByTransacao_Descricao_CodigoAutorizacao(String codigoAutorizacao);
}
