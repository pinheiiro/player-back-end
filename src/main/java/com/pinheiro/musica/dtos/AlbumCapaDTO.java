package com.pinheiro.musica.dtos;

import org.springframework.web.multipart.MultipartFile;

public record AlbumCapaDTO(
        MultipartFile capa
) {
}