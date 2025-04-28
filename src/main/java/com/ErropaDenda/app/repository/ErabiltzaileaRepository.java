package com.ErropaDenda.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ErropaDenda.app.modelo.Erabiltzailea;
import com.ErropaDenda.app.modelo.Erropa;

import java.util.List;

public interface ErabiltzaileaRepository extends JpaRepository<Erabiltzailea, Long> {
	Optional<Erabiltzailea> findByEmail(String email);

	List<Erabiltzailea> findByErropak(Erropa produktuak);
}
