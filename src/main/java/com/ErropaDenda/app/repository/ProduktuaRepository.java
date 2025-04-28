package com.ErropaDenda.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ErropaDenda.app.modelo.Erropa;

import java.util.List;
import java.util.Optional;

public interface ProduktuaRepository extends JpaRepository<Erropa, Long> {
	Optional<Erropa> findById(Long id);
}
