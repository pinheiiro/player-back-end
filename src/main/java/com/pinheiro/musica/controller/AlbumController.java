package com.pinheiro.musica.controller;

import com.pinheiro.musica.dtos.AlbumRequestDTO;
import com.pinheiro.musica.dtos.AlbumResponseDTO;
import com.pinheiro.musica.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/albuns")
public class AlbumController {
    
    @Autowired
    private AlbumService albumService;
    
    @PostMapping
    public ResponseEntity<AlbumResponseDTO> criar(@RequestBody AlbumRequestDTO albumRequestDTO) {
        AlbumResponseDTO responseDTO = albumService.salvar(albumRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }
    
    @GetMapping
    public ResponseEntity<Page<AlbumResponseDTO>> listarTodos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho);
        Page<AlbumResponseDTO> albuns = albumService.listarTodos(pageable);
        return ResponseEntity.ok(albuns);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDTO> buscarPorId(@PathVariable Long id) {
        AlbumResponseDTO album = albumService.buscarPorId(id);
        return ResponseEntity.ok(album);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponseDTO> atualizar(@PathVariable Long id, @RequestBody AlbumRequestDTO albumRequestDTO) {
        AlbumResponseDTO responseDTO = albumService.atualizar(id, albumRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        albumService.deletar(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/capa")
    public ResponseEntity<AlbumResponseDTO> uploadCapa(@PathVariable Long id, @RequestParam("arquivo") MultipartFile arquivo) {
        AlbumResponseDTO responseDTO = albumService.uploadCapa(id, arquivo);
        return ResponseEntity.ok(responseDTO);
    }
    
    @GetMapping("/{id}/capa")
    public ResponseEntity<byte[]> obterCapa(@PathVariable Long id) {
        byte[] capa = albumService.obterCapaAlbum(id);
        if (capa != null) {
            return ResponseEntity.ok(capa);
        }
        return ResponseEntity.notFound().build();
    }
}