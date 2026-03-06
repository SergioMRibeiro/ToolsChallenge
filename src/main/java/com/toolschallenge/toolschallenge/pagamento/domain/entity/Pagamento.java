package com.toolschallenge.toolschallenge.pagamento.domain.entity;

import com.toolschallenge.toolschallenge.pagamento.domain.valueobejct.MetodoPagamento;
import com.toolschallenge.toolschallenge.pagamento.domain.valueobejct.Transacao;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Pagamento")
@Builder
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Transacao transacao;

    @Embedded
    private MetodoPagamento metodoPagamento;
}
