# ToolsChallenge
Desafio oferecido pela empresa **A.R.Phoenix**, com objetico de implementação de uma API de pagamentos. 

A aplicação solicita operações para:
- Registro de pagamento
- Estorno de pagamento
- Consulta de pagamentos

A API segue os princípios de REST, utiliza JSON para troca de dados e retorna status HTTP apropriados conforme o resultado de cada operação.

## Tecnologias utilizadas
- Java 21
- Spring Boot
- Maven
- Lombok
- Spring Data JPA
- Flyway (versionamento de banco de dados)
- JUnit 5
- Mockito

## Requisitos para rodar o projeto
- Java 21
- Maven
- Acesso ao banco de dados utilizado pela aplicação
- Configurar o ambiente
## Configuração do ambiente
Antes de executar o projeto é necessário configurar as credenciais de acesso ao banco de dados.

A aplicação utiliza variáveis de ambiente para evitar que credenciais fiquem expostas no código.

|Variável   |Descrição                           |
|-----------|------------------------------------|
|DB_URL     | URL de conexão com o banco de dados|
|DB_USERNAME| Usuário do banco                   |
|DB_PASSWORD| Senha do banco                     |

A configuração de variáveis de ambiente pode ser feita diretamente pelo terminal, mas algumas IDEs permitem essa configuração em suas ferramentas de run, como o Intellij.

Exemplo de configuração no sistema Linux 
```
export DB_URL=jdbc:postgresql://localhost:5432/pagamentos
export DB_USER=postgres
export DB_PASSWORD=postgres
```

Exemplo de configuração no sistema Windows 
```
$env:DB_USERNAME= DB_URL=jdbc:postgresql://localhost:5432/pagamentos
$env:DB_URL= DB_USER=postgres
$env:DB_PASSWORD= DB_PASSWORD=postgres
```

## Passos para a execução
1. Clonar o repositório
```
git clone <url-do-repositorio>
```
2. Entrar no diretório
```
cd ToolsChallenge
```
3. Executar o projeto
```
mvn spring-boot:run
ou (no caso do windows)
.\mvnw spring-boot:run
```
A aplicação será inicado em:
```
http://localhost:8081
```

## Sobre o Código
### Arquitetuira de pastas
O projeto segue uma arquitetura baseada em camadas, organizado por domínio

Segue uma breve descrição de cada camada do domínio.

**Controller**
Responsável por expor os endpoints da API e lidar com requisições HTTP.

**Service**
Camada onde ficam as regras de negócio da aplicação.

**Repository**
Responsável pela comunicação com o banco de dados utilizando Spring Data JPA.

**DTO**
Objetos utilizados para transferência de dados entre a API e o cliente.

**Entity**
Representação das entidades persistidas no banco de dados.

**Mapper**
Responsável pela conversão entre DTOs e entidades.

**Enums**
Representam estados e tipos utilizados no domínio da aplicação.

**Value Objects**
Objetos de domínio utilizados para representar conceitos específicos do negócio.

Segue uma representação da arquitetura de diretórios:
```
com.toolschallenge.toolschallenge

├── exception
|
├── pagamento
│   ├── api
│   │   ├── dto
│   │   └── PagamentoController
│   │
│   ├── domain
│   │   ├── entity
│   │   ├── value
│   │   └── enums
│   │
│   ├── PagamentoRepository
│   │
│   ├── PagamentoService
│   │
│   └── PagamentoMapper
│
└── ToolschallengeApplication
```


## Funcionalidade
#### Pagamento
1. Responsável pelo registro de um novo pagamento.
2. Fluxo simplificado:
3. Receber dados do pagamento via API
4. Validar as informações recebidas
5. Gerar NSU e código de autorização
6. Avaliar o valor do pagamento (Adicionado para utilização do enum "NEGADO")
8. Autorizar ou negar a transação
9. Persistir o pagamento no banco

Regras aplicadas:
- Pagamentos acima de determinado valor são negados (10000.00)
- Cada pagamento possui NSU único
- Cada pagamento possui código de autorização único

#### Estorno
Responsável por realizar o estorno de um pagamento previamente registrado.

Fluxo:
1. Buscar pagamento pelo ID
2. Validar se o pagamento pode ser estornado
3. Alterar status da transação para "CANCELADO"
4. Persistir alteração

#### Consulta
Permite recuperar informações de pagamentos registrados.

Operações disponíveis:
- Consulta por ID
- Consulta de todos os pagamentos

### Exemplos de entrada e saída
#### Registrar pagamento

```
POST - /pagamento
```

Exmeplo de payload:
```
{
    "transacao": {
        "cartao": "4444********1234",
        "id": "1000023568900001",
        "descricao": {
            "valor": "200,00",
            "dataHora": "01/02/2026 10:30:15",
            "estabelecimento": "B&B"
        },
        "formaPagamento": {
            "tipo": "PARCELADO LOJA",
            "parcelas": "2"
        }
    }
}
```

Resposta:
```
{
    "transacao": {
        "cartao": "4444********1234",
        "id": "1000023568900001",
        "descricao": {
            "valor": "200,00",
            "dataHora": "01/02/2026 10:30:15",
            "estabelecimento": "B&B",
            "nsu": "9829159978",
            "codigoAutorizacao": "339936500",
            "status": "AUTORIZADO"
        },
        "formaPagamento": {
            "tipo": "PARCELADO LOJA",
            "parcelas": "2"
        }
    }
}
```

#### Consultar pagamento

```
GET - /pagamento/4000023568900001
```

Resposta:
```
{
    "transacao": {
        "cartao": "4444********1234",
        "id": "4000023568900001",
        "descricao": {
            "valor": "200,00",
            "dataHora": "01/02/2026 10:30:15",
            "estabelecimento": "B&B",
            "nsu": "9829159978",
            "codigoAutorizacao": "339936500",
            "status": "AUTORIZADO"
        },
        "formaPagamento": {
            "tipo": "PARCELADO LOJA",
            "parcelas": "2"
        }
    }
}
```
#### Consultar todos os pagamentos
```
GET - /pagamento/
```
A resposta será uma lista como body abaixo:
```
{
    "transacao": {
        "cartao": "4444********1234",
        "id": "4000023568900001",
        "descricao": {
            "valor": "200,00",
            "dataHora": "01/02/2026 10:30:15",
            "estabelecimento": "B&B",
            "nsu": "9829159978",
            "codigoAutorizacao": "339936500",
            "status": "AUTORIZADO"
        },
        "formaPagamento": {
            "tipo": "PARCELADO LOJA",
            "parcelas": "2"
        }
    }
}
```

#### Estornar pagamento
```
PATCH - /pagamento/1000023568900001/estorno
```

Retorno:
```
{
    "transacao": {
        "cartao": "4444********1234",
        "id": "11000023568900001",
        "descricao": {
            "valor": "200,00",
            "dataHora": "01/02/2026 10:30:15",
            "estabelecimento": "B&B",
            "nsu": "9829159978",
            "codigoAutorizacao": "339936500",
            "status": "CANCELADO"
        },
        "formaPagamento": {
            "tipo": "PARCELADO LOJA",
            "parcelas": "2"
        }
    }
}
```

### Testes unitários
A aplicação possui testes unitários focados na camada de Service, responsável pelas regras de negócio.

Os testes utilizam:
- JUnit 5
- Mockito

Os cenários testados incluem:
- Registro de pagamento autorizado
- Registro de pagamento negado
- Geração de NSU
- Geração de código de autorização
- Consulta de pagamento
- Estorno de pagamento
## Ponto de vista do dev
#### Escolhas tomadas

Algumas decisões arquiteturais foram tomadas para manter o código organizado e alinhado com boas práticas:
- Criação das propriedades ID, createdAt e updatedAt internos,  para Pagamento
- Criação de lógica simples para implementação do status "NEGADO"
- Separação entre DTOs e entidades
- Uso de Mapper para conversão entre camadas
- Uso de Value Objects para representar elementos do domínio
- Uso de Flyway para versionamento do banco de dados
- Implementação de testes unitários na camada de serviço
- Essas escolhas ajudam a manter o código mais modular, testável e de fácil manutenção.
### Desafios
Durante o desenvolvimento alguns pontos exigiram maior atenção:
- Modelagem das entidades e objetos de valor
- Implementação da geração de NSU e codigoAutorizacao únicos
- Organização da arquitetura para manter separação de responsabilidades
- Cuidados com a normalização de "valor", "data" e "tipo"
- Cuidado na escolha de não seguir com o Id que vinha do payload por se tratar de um ID externo
### Propostas de melhoria
Algumas melhorias que poderiam ser implementadas em versões futuras:
- Refinar o tratamento global de exceptions
- Implementação de paginação na listagem de pagamentos
- Inclusão de logs estruturados
- Inclusão de testes de integração
- Adição de documentação automática da API (Swagger / OpenAPI)
