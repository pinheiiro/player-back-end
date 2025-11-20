package com.pinheiro.musica.controller;

import com.pinheiro.musica.dtos.ArtistaRequestDTO;
import com.pinheiro.musica.dtos.ArtistaResponseDTO;
import com.pinheiro.musica.service.ArtistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/artistas")
public class ArtistaController {
    
    @Autowired
    private ArtistaService artistaService;
    
    @PostMapping
    public ResponseEntity<ArtistaResponseDTO> criar(@RequestBody ArtistaRequestDTO artistaRequestDTO) {
        ArtistaResponseDTO responseDTO = artistaService.salvar(artistaRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }
    
    @GetMapping
    public ResponseEntity<List<ArtistaResponseDTO>> listarTodos() {
        List<ArtistaResponseDTO> responseDTOs = artistaService.listarTodos();
        return ResponseEntity.ok(responseDTOs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ArtistaResponseDTO> buscarPorId(@PathVariable Long id) {
        ArtistaResponseDTO responseDTO = artistaService.buscarPorId(id);
        return ResponseEntity.ok(responseDTO);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ArtistaResponseDTO> atualizar(@PathVariable Long id, @RequestBody ArtistaRequestDTO artistaRequestDTO) {
        ArtistaResponseDTO responseDTO = artistaService.atualizar(id, artistaRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        artistaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/foto")
    public ResponseEntity<ArtistaResponseDTO> uploadFoto(@PathVariable Long id, @RequestParam("arquivo") MultipartFile arquivo) {
        ArtistaResponseDTO responseDTO = artistaService.uploadFoto(id, arquivo);
        return ResponseEntity.ok(responseDTO);
    }
}