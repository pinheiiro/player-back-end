package com.pinheiro.musica.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ArmazenamentoService {
    
    private static final String DIRETORIO_UPLOADS = "uploads/";
    private static final String DIRETORIO_MUSICAS = DIRETORIO_UPLOADS + "musicas/";
    private static final String DIRETORIO_CAPAS_ALBUM = DIRETORIO_UPLOADS + "capas-album/";
    private static final String DIRETORIO_FOTOS_ARTISTA = DIRETORIO_UPLOADS + "fotos-artista/";
    
    public ArmazenamentoService() {
        criarDiretoriosSeNaoExistirem();
    }
    
    private void criarDiretoriosSeNaoExistirem() {
        criarDiretorioSeNaoExistir(DIRETORIO_UPLOADS);
        criarDiretorioSeNaoExistir(DIRETORIO_MUSICAS);
        criarDiretorioSeNaoExistir(DIRETORIO_CAPAS_ALBUM);
        criarDiretorioSeNaoExistir(DIRETORIO_FOTOS_ARTISTA);
    }
    
    private void criarDiretorioSeNaoExistir(String caminho) {
        try {
            Path path = Paths.get(caminho);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao criar diretório: " + caminho, e);
        }
    }
    
    public String salvarArquivoMusica(MultipartFile arquivo) throws IOException {
        return salvarArquivo(arquivo, DIRETORIO_MUSICAS);
    }
    
    public String salvarCapaAlbum(MultipartFile arquivo) throws IOException {
        return salvarArquivo(arquivo, DIRETORIO_CAPAS_ALBUM);
    }
    
    public String salvarFotoArtista(MultipartFile arquivo) throws IOException {
        return salvarArquivo(arquivo, DIRETORIO_FOTOS_ARTISTA);
    }
    
    private String salvarArquivo(MultipartFile arquivo, String diretorio) throws IOException {
        String nomeArquivo = UUID.randomUUID().toString() + "-" + arquivo.getOriginalFilename();
        Path caminhoArquivo = Paths.get(diretorio, nomeArquivo);
        Files.copy(arquivo.getInputStream(), caminhoArquivo, StandardCopyOption.REPLACE_EXISTING);
        return caminhoArquivo.toString();
    }
    
    public byte[] lerArquivo(String caminho) throws IOException {
        Path path = Paths.get(caminho);
        return Files.readAllBytes(path);
    }
}