package com.pinheiro.musica.dtos;

import java.util.List;

public record AlbumResponseDTO(
        Long id,
        String nome,
        ArtistaResponseDTO artista,
        Integer anoLancamento,
        String urlCapa,
        List<MusicaResponseDTO> musicas
) {
}