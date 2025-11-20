package com.pinheiro.musica.dtos;

import com.pinheiro.musica.model.Artista;
import java.util.List;

public record AlbumDTO(
        Long id,
        String nome,
        Artista artista,
        Integer anoLancamento,
        String caminhoCapa,
        List<MusicaDTO> musicas
) {
}