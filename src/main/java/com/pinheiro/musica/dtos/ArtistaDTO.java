package com.pinheiro.musica.dtos;

public record ArtistaDTO(
        Long id,
        String nome,
        byte[] foto
) {
}