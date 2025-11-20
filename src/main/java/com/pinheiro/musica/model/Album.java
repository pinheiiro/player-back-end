package com.pinheiro.musica.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "albuns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Album {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artista_id", nullable = false)
    private Artista artista;
    
    @Column(name = "ano_lancamento")
    private Integer anoLancamento;
    
    @Column(name = "caminho_capa")
    private String caminhoCapa;
    
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Musica> musicas;
}