package com.pinheiro.musica.service;

import com.pinheiro.musica.dtos.AlbumNomeDTO;
import com.pinheiro.musica.dtos.AlbumNomeCapaDTO;
import com.pinheiro.musica.dtos.AlbumRequestDTO;
import com.pinheiro.musica.dtos.AlbumResponseDTO;
import com.pinheiro.musica.dtos.ArtistaResponseDTO;
import com.pinheiro.musica.dtos.MusicaResponseDTO;
import com.pinheiro.musica.exception.ResourceNotFoundException;
import com.pinheiro.musica.exception.ValidationException;
import com.pinheiro.musica.model.Album;
import com.pinheiro.musica.model.Artista;
import com.pinheiro.musica.model.Musica;
import com.pinheiro.musica.repository.AlbumRepository;
import com.pinheiro.musica.repository.MusicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumService {
    
    @Autowired
    private AlbumRepository albumRepository;
    
    @Autowired
    private MusicaRepository musicaRepository;
    
    @Autowired
    private ArmazenamentoService armazenamentoService;
    
    @Autowired
    private ArtistaService artistaService;
    
    public AlbumResponseDTO salvar(AlbumRequestDTO albumRequestDTO) {
        if (albumRequestDTO.nome() == null || albumRequestDTO.nome().isEmpty()) {
            throw new ValidationException("Nome do álbum é obrigatório");
        }
        
        if (albumRequestDTO.artistaId() == null) {
            throw new ValidationException("ID do artista é obrigatório");
        }
        
        Artista artista = artistaService.buscarPorIdEntidade(albumRequestDTO.artistaId());
        
        Album album = new Album();
        album.setNome(albumRequestDTO.nome());
        album.setAnoLancamento(albumRequestDTO.anoLancamento());
        album.setArtista(artista);
        
        Album novoAlbum = albumRepository.save(album);
        
        ArtistaResponseDTO artistaDTO = new ArtistaResponseDTO(
                novoAlbum.getArtista().getId(),
                novoAlbum.getArtista().getNome()
        );
        
        return new AlbumResponseDTO(
                novoAlbum.getId(),
                novoAlbum.getNome(),
                artistaDTO,
                novoAlbum.getAnoLancamento(),
                null,
                null
        );
    }
    
    public Page<AlbumResponseDTO> listarTodos(Pageable pageable) {
        Page<Album> albuns = albumRepository.findAll(pageable);
        
        List<AlbumResponseDTO> albunsDTO = albuns.getContent().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(albunsDTO, pageable, albuns.getTotalElements());
    }
    
    public AlbumResponseDTO buscarPorId(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado com ID: " + id));
        
        return converterParaDTO(album);
    }
    
    public AlbumResponseDTO atualizar(Long id, AlbumRequestDTO albumRequestDTO) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado com ID: " + id));
        
        if (albumRequestDTO.nome() == null || albumRequestDTO.nome().isEmpty()) {
            throw new ValidationException("Nome do álbum é obrigatório");
        }
        
        if (albumRequestDTO.artistaId() == null) {
            throw new ValidationException("ID do artista é obrigatório");
        }
        
        Artista artista = artistaService.buscarPorIdEntidade(albumRequestDTO.artistaId());
        
        album.setNome(albumRequestDTO.nome());
        album.setAnoLancamento(albumRequestDTO.anoLancamento());
        album.setArtista(artista);
        
        Album albumAtualizado = albumRepository.save(album);
        
        return converterParaDTO(albumAtualizado);
    }
    
    public AlbumResponseDTO atualizarNome(Long id, AlbumNomeDTO albumNomeDTO) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado com ID: " + id));
        
        if (albumNomeDTO.nome() == null || albumNomeDTO.nome().isEmpty()) {
            throw new ValidationException("Nome do álbum é obrigatório");
        }
        
        album.setNome(albumNomeDTO.nome());
        Album albumAtualizado = albumRepository.save(album);
        
        return converterParaDTO(albumAtualizado);
    }
    
    public AlbumResponseDTO atualizarCapa(Long id, MultipartFile capa) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado com ID: " + id));
        
        // Remover a capa anterior, se existir
        if (album.getCaminhoCapa() != null && !album.getCaminhoCapa().isEmpty()) {
            try {
                Path caminhoCapaAntiga = Paths.get(album.getCaminhoCapa());
                if (Files.exists(caminhoCapaAntiga)) {
                    Files.delete(caminhoCapaAntiga);
                }
            } catch (IOException e) {
                System.err.println("Erro ao deletar capa anterior do álbum: " + album.getCaminhoCapa());
            }
        }
        
        try {
            String caminhoCapa = armazenamentoService.salvarCapaAlbum(capa);
            album.setCaminhoCapa(caminhoCapa);
            Album albumAtualizado = albumRepository.save(album);
            
            return converterParaDTO(albumAtualizado);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar capa do álbum", e);
        }
    }
    
    public AlbumResponseDTO atualizarNomeECapa(Long id, AlbumNomeCapaDTO albumNomeCapaDTO) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado com ID: " + id));
        
        if (albumNomeCapaDTO.nome() == null || albumNomeCapaDTO.nome().isEmpty()) {
            throw new ValidationException("Nome do álbum é obrigatório");
        }
        
        // Remover a capa anterior, se existir
        if (album.getCaminhoCapa() != null && !album.getCaminhoCapa().isEmpty()) {
            try {
                Path caminhoCapaAntiga = Paths.get(album.getCaminhoCapa());
                if (Files.exists(caminhoCapaAntiga)) {
                    Files.delete(caminhoCapaAntiga);
                }
            } catch (IOException e) {
                System.err.println("Erro ao deletar capa anterior do álbum: " + album.getCaminhoCapa());
            }
        }
        
        try {
            String caminhoCapa = armazenamentoService.salvarCapaAlbum(albumNomeCapaDTO.capa());
            album.setCaminhoCapa(caminhoCapa);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar capa do álbum", e);
        }
        
        album.setNome(albumNomeCapaDTO.nome());
        Album albumAtualizado = albumRepository.save(album);
        
        return converterParaDTO(albumAtualizado);
    }
    
    public void deletar(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado com ID: " + id));

        List<Musica> musicas = musicaRepository.findByAlbumId(id);
        for (Musica musica : musicas) {
            if (musica.getCaminhoArquivo() != null && !musica.getCaminhoArquivo().isEmpty()) {
                try {
                    Path caminhoArquivo = Paths.get(musica.getCaminhoArquivo());
                    if (Files.exists(caminhoArquivo)) {
                        Files.delete(caminhoArquivo);
                    }
                } catch (IOException e) {
                    System.err.println("Erro ao deletar arquivo de música: " + musica.getCaminhoArquivo());
                }
            }
            musicaRepository.deleteById(musica.getId());
        }

        if (album.getCaminhoCapa() != null && !album.getCaminhoCapa().isEmpty()) {
            try {
                Path caminhoCapa = Paths.get(album.getCaminhoCapa());
                if (Files.exists(caminhoCapa)) {
                    Files.delete(caminhoCapa);
                }
            } catch (IOException e) {
                System.err.println("Erro ao deletar capa do álbum: " + album.getCaminhoCapa());
            }
        }

        albumRepository.deleteById(id);
    }
    
    public AlbumResponseDTO uploadCapa(Long id, MultipartFile arquivo) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado com ID: " + id));
        
        // Remover a capa anterior, se existir
        if (album.getCaminhoCapa() != null && !album.getCaminhoCapa().isEmpty()) {
            try {
                Path caminhoCapaAntiga = Paths.get(album.getCaminhoCapa());
                if (Files.exists(caminhoCapaAntiga)) {
                    Files.delete(caminhoCapaAntiga);
                }
            } catch (IOException e) {
                System.err.println("Erro ao deletar capa anterior do álbum: " + album.getCaminhoCapa());
            }
        }
        
        try {
            String caminhoCapa = armazenamentoService.salvarCapaAlbum(arquivo);
            album.setCaminhoCapa(caminhoCapa);
            Album albumAtualizado = albumRepository.save(album);
            
            return converterParaDTO(albumAtualizado);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar capa do álbum", e);
        }
    }
    
    public byte[] obterCapaAlbum(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado com ID: " + id));
        
        if (album.getCaminhoCapa() != null) {
            try {
                return armazenamentoService.lerArquivo(album.getCaminhoCapa());
            } catch (IOException e) {
                throw new RuntimeException("Erro ao ler capa do álbum", e);
            }
        }
        return null;
    }
    
    public Album buscarPorIdEntidade(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado com ID: " + id));
    }
    
    private AlbumResponseDTO converterParaDTO(Album album) {
        List<Musica> musicas = musicaRepository.findByAlbumId(album.getId());
        List<MusicaResponseDTO> musicasDTO = musicas.stream()
                .map(musica -> new MusicaResponseDTO(musica.getId(), musica.getNome(), "/musicas/" + musica.getId() + "/stream"))
                .collect(Collectors.toList());
        
        ArtistaResponseDTO artistaDTO = new ArtistaResponseDTO(
                album.getArtista().getId(),
                album.getArtista().getNome()
        );
        
        return new AlbumResponseDTO(
                album.getId(),
                album.getNome(),
                artistaDTO,
                album.getAnoLancamento(),
                album.getCaminhoCapa() != null ? "/albuns/" + album.getId() + "/capa" : null,
                musicasDTO
        );
    }
}