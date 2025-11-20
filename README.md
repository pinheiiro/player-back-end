# Aplicação de Streaming de Música

Esta é uma aplicação de streaming de música desenvolvida com Spring Boot, que permite o cadastro de artistas, álbuns e músicas, com funcionalidades de upload e streaming.

## Funcionalidades

- Cadastro de artistas com nome e foto
- Cadastro de álbuns com nome, artista, ano de lançamento e capa
- Cadastro de músicas com nome, álbum e arquivo de áudio
- Paginação de álbuns (20 por página)
- Streaming de músicas
- API RESTful para todas as operações
- Tratamento global de exceções
- DTOs padronizados para requisições e respostas

## Tecnologias Utilizadas

- Java 21
- Spring Boot 3.5.7
- PostgreSQL
- Flyway para migrações de banco de dados
- Maven para gerenciamento de dependências

## Estrutura do Banco de Dados

O banco de dados possui três tabelas principais:

1. `artistas` - Armazena informações dos artistas
2. `albuns` - Armazena informações dos álbuns
3. `musicas` - Armazena informações das músicas

## Endpoints da API

### Artistas
- `POST /artistas` - Criar um novo artista
- `GET /artistas` - Listar todos os artistas
- `GET /artistas/{id}` - Buscar artista por ID
- `PUT /artistas/{id}` - Atualizar artista
- `DELETE /artistas/{id}` - Deletar artista
- `POST /artistas/{id}/foto` - Upload de foto do artista

### Álbuns
- `POST /albuns` - Criar um novo álbum
- `GET /albuns` - Listar todos os álbuns (com paginação)
- `GET /albuns/{id}` - Buscar álbum por ID
- `PUT /albuns/{id}` - Atualizar álbum
- `DELETE /albuns/{id}` - Deletar álbum
- `POST /albuns/{id}/capa` - Upload de capa do álbum
- `GET /albuns/{id}/capa` - Obter capa do álbum

### Músicas
- `POST /musicas` - Criar uma nova música com upload de arquivo (multipart)
- `GET /musicas/album/{albumId}` - Listar músicas de um álbum
- `GET /musicas/{id}` - Buscar música por ID
- `PUT /musicas/{id}` - Atualizar música
- `DELETE /musicas/{id}` - Deletar música
- `GET /musicas/{id}/stream` - Stream da música

## Estrutura de DTOs

A API utiliza DTOs (Data Transfer Objects) separados para requisições e respostas:

- `ArtistaRequestDTO` / `ArtistaResponseDTO`
- `AlbumRequestDTO` / `AlbumResponseDTO`
- `MusicaRequestDTO` / `MusicaResponseDTO`

## Upload de Arquivos

Todos os uploads de arquivos são feitos juntamente com os dados, utilizando requisições multipart. Os arquivos são armazenados em diretórios separados:

- Músicas: `uploads/musicas/`
- Capas de álbuns: `uploads/capas-album/`
- Fotos de artistas: `uploads/fotos-artista/`

## Tratamento de Exceções

A aplicação utiliza um handler global de exceções que trata:

- `ResourceNotFoundException` - quando um recurso não é encontrado
- `ValidationException` - quando há erros de validação
- `MaxUploadSizeExceededException` - quando o arquivo enviado é muito grande
- Exceções genéricas - para erros inesperados

## Arquitetura

A aplicação segue uma arquitetura em camadas:

1. **Controllers** - Responsáveis apenas por receber requisições e enviar respostas
2. **Services** - Contêm toda a lógica de negócio e regras da aplicação
3. **Repositories** - Responsáveis pelo acesso aos dados
4. **Exceptions** - Tratamento global de exceções

Todos os DTOs foram implementados utilizando Java Records para garantir imutabilidade e concisão.

## Configuração

A aplicação utiliza as seguintes configurações padrão:

- Banco de dados PostgreSQL em `localhost:5432` com nome `musica`
- Usuário: `postgres`
- Senha: `postgres`
- Porta da aplicação: `8080`

## Migrações do Banco de Dados

As migrações são gerenciadas pelo Flyway e estão localizadas em `src/main/resources/db/migration`:

1. `V1__create_tables.sql` - Criação das tabelas
2. `V2__insert_sample_data.sql` - Dados de exemplo

## Como Executar

1. Certifique-se de ter o PostgreSQL instalado e em execução
2. Configure o banco de dados conforme as credenciais no `application.yml`
3. Execute a aplicação com `mvn spring-boot:run` ou através da sua IDE