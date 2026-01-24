package com.pinheiro.musica.controller;

import com.pinheiro.musica.dtos.MusicaRequestDTO;
import com.pinheiro.musica.dtos.MusicaResponseDTO;
import com.pinheiro.musica.service.MusicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/musicas")
public class MusicaController {
    
    @Autowired
    private MusicaService musicaService;

    @Value("${app.upload-dir}")
    private String caminhoPasta;
    
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
    public ResponseEntity<byte[]> streamMusica(@PathVariable Long id, @RequestHeader(value = "Range", required = false) String rangeHeader) {
        try {
            // Obter o caminho do arquivo ao invés do conteúdo completo
            String caminhoArquivo = musicaService.obterCaminhoArquivoMusica(id);
            Path pastaBase = Paths.get(this.caminhoPasta);
            Path path = pastaBase.resolve(caminhoArquivo).normalize();
            
            if (!Files.exists(path)) {
                return ResponseEntity.notFound().build();
            }
            
            long fileSize = Files.size(path);
            
            // Se não houver range header, retornar o arquivo completo
            if (rangeHeader == null || !rangeHeader.startsWith("bytes=")) {
                byte[] data = Files.readAllBytes(path);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentLength(data.length);
                return ResponseEntity.ok().headers(headers).body(data);
            }
            
            // Processar range request
            String range = rangeHeader.substring(6); // Remove "bytes="
            long start = 0;
            long end = fileSize - 1;
            
            String[] ranges = range.split("-");
            if (!ranges[0].isEmpty()) {
                start = Long.parseLong(ranges[0]);
            }
            if (ranges.length > 1 && !ranges[1].isEmpty()) {
                end = Long.parseLong(ranges[1]);
            }
            
            // Validar range
            if (start >= fileSize || end >= fileSize || start > end) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Range", "bytes */" + fileSize);
                return new ResponseEntity<>(headers, HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
            }
            
            // Limitar o tamanho do chunk para 1MB
            long rangeLength = end - start + 1;
            if (rangeLength > 1024 * 1024) {
                end = start + 1024 * 1024 - 1;
                rangeLength = end - start + 1;
            }
            
            // Ler apenas a parte solicitada do arquivo
            byte[] data = new byte[(int) rangeLength];
            try (RandomAccessFile file = new RandomAccessFile(path.toFile(), "r")) {
                file.seek(start);
                file.readFully(data);
            }
            
            // Configurar os headers apropriados
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(data.length);
            headers.add("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);
            headers.add("Accept-Ranges", "bytes");
            
            return new ResponseEntity<>(data, headers, HttpStatus.PARTIAL_CONTENT);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}