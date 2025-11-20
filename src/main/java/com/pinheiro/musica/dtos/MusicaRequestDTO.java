package com.pinheiro.musica.dtos;

public record MusicaRequestDTO(
        String nome,
        Long albumId,
        Long artistaId,
        Integer duracaoSegundos,
        String duracaoFormatada
) {
}