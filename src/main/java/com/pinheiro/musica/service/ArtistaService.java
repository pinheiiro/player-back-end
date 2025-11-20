package com.pinheiro.musica.service;

import com.pinheiro.musica.dtos.ArtistaRequestDTO;
import com.pinheiro.musica.dtos.ArtistaResponseDTO;
import com.pinheiro.musica.exception.ResourceNotFoundException;
import com.pinheiro.musica.exception.ValidationException;
import com.pinheiro.musica.model.Artista;
import com.pinheiro.musica.repository.ArtistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtistaService {
    
    @Autowired
    private ArtistaRepository artistaRepository;
    
    @Autowired
    private ArmazenamentoService armazenamentoService;
    
    public ArtistaResponseDTO salvar(ArtistaRequestDTO artistaRequestDTO) {
        if (artistaRequestDTO.nome() == null || artistaRequestDTO.nome().isEmpty()) {
            throw new ValidationException("Nome do artista é obrigatório");
        }
        
        Artista artista = new Artista();
        artista.setNome(artistaRequestDTO.nome());
        Artista novoArtista = artistaRepository.save(artista);
        
        return new ArtistaResponseDTO(
                novoArtista.getId(),
                novoArtista.getNome()
        );
    }
    
    public List<ArtistaResponseDTO> listarTodos() {
        List<Artista> artistas = artistaRepository.findAll();
        return artistas.stream()
                .map(a -> new ArtistaResponseDTO(a.getId(), a.getNome()))
                .collect(Collectors.toList());
    }
    
    public ArtistaResponseDTO buscarPorId(Long id) {
        Artista artista = artistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artista não encontrado com ID: " + id));
        
        return new ArtistaResponseDTO(
                artista.getId(),
                artista.getNome()
        );
    }
    
    public ArtistaResponseDTO atualizar(Long id, ArtistaRequestDTO artistaRequestDTO) {
        Artista artista = artistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artista não encontrado com ID: " + id));
        
        if (artistaRequestDTO.nome() == null || artistaRequestDTO.nome().isEmpty()) {
            throw new ValidationException("Nome do artista é obrigatório");
        }
        
        artista.setNome(artistaRequestDTO.nome());
        Artista artistaAtualizado = artistaRepository.save(artista);
        
        return new ArtistaResponseDTO(
                artistaAtualizado.getId(),
                artistaAtualizado.getNome()
        );
    }
    
    public void deletar(Long id) {
        if (!artistaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Artista não encontrado com ID: " + id);
        }
        artistaRepository.deleteById(id);
    }
    
    public ArtistaResponseDTO uploadFoto(Long id, MultipartFile arquivo) {
        Artista artista = artistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artista não encontrado com ID: " + id));
        
        try {
            String caminhoFoto = armazenamentoService.salvarFotoArtista(arquivo);
            artista.setCaminhoFoto(caminhoFoto);
            Artista artistaAtualizado = artistaRepository.save(artista);
            
            return new ArtistaResponseDTO(
                    artistaAtualizado.getId(),
                    artistaAtualizado.getNome()
            );
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar foto do artista", e);
        }
    }
    
    public Artista buscarPorIdEntidade(Long id) {
        return artistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artista não encontrado com ID: " + id));
    }
}