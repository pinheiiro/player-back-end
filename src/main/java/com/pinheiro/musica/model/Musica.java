package com.pinheiro.musica.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "musicas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Musica {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artista_id", nullable = false)
    private Artista artista;
    
    @Column(name = "duracao_segundos")
    private Integer duracaoSegundos;
    
    @Column(name = "duracao_formatada")
    private String duracaoFormatada;
    
    @Column(name = "caminho_arquivo")
    private String caminhoArquivo;
}