package com.ErropaDenda.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ErropaDenda.app.modelo.Erropa;

import java.util.List;
import java.util.Optional;

public interface ProduktuaRepository extends JpaRepository<Erropa, Long> {
	Optional<Erropa> findById(Long id);
	List<Erropa> findByTipo(String tipo);
	
	List<Erropa> findByTipoInIgnoreCase(List<String> tipos);
}
