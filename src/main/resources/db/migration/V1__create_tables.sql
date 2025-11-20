CREATE TABLE artistas (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    caminho_foto VARCHAR(500)
);

CREATE TABLE albuns (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    artista_id BIGINT NOT NULL,
    ano_lancamento INTEGER,
    caminho_capa VARCHAR(500),
    CONSTRAINT fk_albuns_artistas FOREIGN KEY (artista_id) REFERENCES artistas(id)
);

CREATE TABLE musicas (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    album_id BIGINT NOT NULL,
    artista_id BIGINT NOT NULL,
    duracao_segundos INTEGER,
    duracao_formatada VARCHAR(10),
    caminho_arquivo VARCHAR(500),
    CONSTRAINT fk_musicas_albuns FOREIGN KEY (album_id) REFERENCES albuns(id),
    CONSTRAINT fk_musicas_artistas FOREIGN KEY (artista_id) REFERENCES artistas(id)
);