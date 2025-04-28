package com.ErropaDenda.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ErropaDenda.app.modelo.Erabiltzailea;
import com.ErropaDenda.app.repository.ErabiltzaileaRepository;

@Controller
@RequestMapping("/erabiltzaileak")
public class ErabiltzaileakController {

	@Autowired
	private ErabiltzaileaRepository erabiltzaileaRepository;

	@GetMapping("/ikusi")
	public String ikusiErabiltzaileak(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();

		Erabiltzailea erabiltzailea = erabiltzaileaRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
		model.addAttribute("erabiltzailea", erabiltzailea);

		String rol = auth.getAuthorities().stream().map(authority -> authority.getAuthority()).findFirst().orElse(null);
		model.addAttribute("rola", rol);
		Iterable<Erabiltzailea> erabiltzaileak = erabiltzaileaRepository.findAll();

		model.addAttribute("usuarios", erabiltzaileak);

		return "taulak/erabiltzaileakTaula";
	}
}
