package com.pinheiro.musica.dtos;

import org.springframework.web.multipart.MultipartFile;

public record AlbumNomeCapaDTO(
        String nome,
        MultipartFile capa
) {
}