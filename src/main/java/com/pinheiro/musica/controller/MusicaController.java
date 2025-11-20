package com.pinheiro.musica.controller;

import com.pinheiro.musica.dtos.MusicaRequestDTO;
import com.pinheiro.musica.dtos.MusicaResponseDTO;
import com.pinheiro.musica.service.MusicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/musicas")
public class MusicaController {
    
    @Autowired
    private MusicaService musicaService;
    
    @PostMapping
    public ResponseEntity<MusicaResponseDTO> criar(
            @RequestPart("dados") MusicaRequestDTO musicaRequestDTO,
            @RequestPart("arquivo") MultipartFile arquivo) {
        MusicaResponseDTO responseDTO = musicaService.salvar(musicaRequestDTO, arquivo);
        return ResponseEntity.ok(responseDTO);
    }
    
    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<MusicaResponseDTO>> listarPorAlbum(@PathVariable Long albumId) {
        List<MusicaResponseDTO> musicas = musicaService.listarPorAlbum(albumId);
        return ResponseEntity.ok(musicas);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MusicaResponseDTO> buscarPorId(@PathVariable Long id) {
        MusicaResponseDTO musica = musicaService.buscarPorId(id);
        return ResponseEntity.ok(musica);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MusicaResponseDTO> atualizar(@PathVariable Long id, @RequestBody MusicaRequestDTO musicaRequestDTO) {
        MusicaResponseDTO responseDTO = musicaService.atualizar(id, musicaRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        musicaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/stream")
    public ResponseEntity<byte[]> streamMusica(@PathVariable Long id) {
        byte[] arquivo = musicaService.obterArquivoMusica(id);
        if (arquivo != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(arquivo.length);
            return ResponseEntity.ok().headers(headers).body(arquivo);
        }
        return ResponseEntity.notFound().build();
    }
}