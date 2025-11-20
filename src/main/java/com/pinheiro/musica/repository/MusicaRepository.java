package com.pinheiro.musica.repository;

import com.pinheiro.musica.model.Musica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicaRepository extends JpaRepository<Musica, Long> {
    List<Musica> findByAlbumId(Long albumId);
}