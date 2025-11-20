package com.pinheiro.musica.service;

import com.pinheiro.musica.dtos.MusicaRequestDTO;
import com.pinheiro.musica.dtos.MusicaResponseDTO;
import com.pinheiro.musica.exception.ResourceNotFoundException;
import com.pinheiro.musica.exception.ValidationException;
import com.pinheiro.musica.model.Album;
import com.pinheiro.musica.model.Artista;
import com.pinheiro.musica.model.Musica;
import com.pinheiro.musica.repository.MusicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MusicaService {
    
    @Autowired
    private MusicaRepository musicaRepository;
    
    @Autowired
    private ArmazenamentoService armazenamentoService;
    
    @Autowired
    private AlbumService albumService;
    
    @Autowired
    private ArtistaService artistaService;
    
    public MusicaResponseDTO salvar(MusicaRequestDTO musicaRequestDTO, MultipartFile arquivo) {
        // Validações
        if (musicaRequestDTO.nome() == null || musicaRequestDTO.nome().isEmpty()) {
            throw new ValidationException("Nome da música é obrigatório");
        }
        
        if (musicaRequestDTO.albumId() == null) {
            throw new ValidationException("ID do álbum é obrigatório");
        }
        
        if (musicaRequestDTO.artistaId() == null) {
            throw new ValidationException("ID do artista é obrigatório");
        }
        
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ValidationException("Arquivo de música é obrigatório");
        }
        
        // Verificar se o álbum existe
        Album album = albumService.buscarPorIdEntidade(musicaRequestDTO.albumId());
        
        // Verificar se o artista existe
        Artista artista = artistaService.buscarPorIdEntidade(musicaRequestDTO.artistaId());
        
        // Criar a música
        Musica musica = new Musica();
        musica.setNome(musicaRequestDTO.nome());
        musica.setAlbum(album);
        musica.setArtista(artista);
        musica.setDuracaoSegundos(musicaRequestDTO.duracaoSegundos());
        musica.setDuracaoFormatada(musicaRequestDTO.duracaoFormatada());
        
        // Salvar o arquivo
        try {
            String caminhoArquivo = armazenamentoService.salvarArquivoMusica(arquivo);
            musica.setCaminhoArquivo(caminhoArquivo);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo de música", e);
        }
        
        // Salvar a música
        Musica novaMusica = musicaRepository.save(musica);
        
        return new MusicaResponseDTO(
                novaMusica.getId(),
                novaMusica.getNome(),
                "/musicas/" + novaMusica.getId() + "/stream"
        );
    }
    
    public List<MusicaResponseDTO> listarPorAlbum(Long albumId) {
        // Verificar se o álbum existe
        albumService.buscarPorIdEntidade(albumId);
        
        List<Musica> musicas = musicaRepository.findByAlbumId(albumId);
        return musicas.stream()
                .map(musica -> new MusicaResponseDTO(musica.getId(), musica.getNome(), "/musicas/" + musica.getId() + "/stream"))
                .collect(Collectors.toList());
    }
    
    public MusicaResponseDTO buscarPorId(Long id) {
        Musica musica = musicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Música não encontrada com ID: " + id));
        
        return new MusicaResponseDTO(
                musica.getId(),
                musica.getNome(),
                "/musicas/" + musica.getId() + "/stream"
        );
    }
    
    public MusicaResponseDTO atualizar(Long id, MusicaRequestDTO musicaRequestDTO) {
        Musica musica = musicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Música não encontrada com ID: " + id));
        
        // Validações
        if (musicaRequestDTO.nome() == null || musicaRequestDTO.nome().isEmpty()) {
            throw new ValidationException("Nome da música é obrigatório");
        }
        
        if (musicaRequestDTO.albumId() == null) {
            throw new ValidationException("ID do álbum é obrigatório");
        }
        
        if (musicaRequestDTO.artistaId() == null) {
            throw new ValidationException("ID do artista é obrigatório");
        }
        
        // Verificar se o álbum existe
        Album album = albumService.buscarPorIdEntidade(musicaRequestDTO.albumId());
        
        // Verificar se o artista existe
        Artista artista = artistaService.buscarPorIdEntidade(musicaRequestDTO.artistaId());
        
        // Atualizar os dados
        musica.setNome(musicaRequestDTO.nome());
        musica.setAlbum(album);
        musica.setArtista(artista);
        musica.setDuracaoSegundos(musicaRequestDTO.duracaoSegundos());
        musica.setDuracaoFormatada(musicaRequestDTO.duracaoFormatada());
        
        Musica musicaAtualizada = musicaRepository.save(musica);
        
        return new MusicaResponseDTO(
                musicaAtualizada.getId(),
                musicaAtualizada.getNome(),
                "/musicas/" + musicaAtualizada.getId() + "/stream"
        );
    }
    
    public void deletar(Long id) {
        if (!musicaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Música não encontrada com ID: " + id);
        }
        musicaRepository.deleteById(id);
    }
    
    public byte[] obterArquivoMusica(Long id) {
        Musica musica = musicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Música não encontrada com ID: " + id));
        
        if (musica.getCaminhoArquivo() != null) {
            try {
                return armazenamentoService.lerArquivo(musica.getCaminhoArquivo());
            } catch (IOException e) {
                throw new RuntimeException("Erro ao ler arquivo de música", e);
            }
        }
        return null;
    }
}