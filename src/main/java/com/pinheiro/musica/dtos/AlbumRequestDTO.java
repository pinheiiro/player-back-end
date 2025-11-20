package com.pinheiro.musica.dtos;

public record AlbumRequestDTO(
        String nome,
        Long artistaId,
        Integer anoLancamento
) {
}