package com.ErropaDenda.app.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ErropaDenda.app.modelo.Erabiltzailea;
import com.ErropaDenda.app.modelo.Erropa;
import com.ErropaDenda.app.repository.ErabiltzaileaRepository;
import com.ErropaDenda.app.repository.ProduktuaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Controller
@RequestMapping("/produktua")
public class ProduktuaController {
	@Autowired
	private ProduktuaRepository prodRepo;

	@Autowired
	private ErabiltzaileaRepository erabRepository;

	@PersistenceContext
	private EntityManager entityManager;
	

	@GetMapping("/gehitu")
	public String showAddProductForm(Model model) {
		model.addAttribute("produktua", new Erropa());
		return "/formularioa/produktuaForm";

	}

	@PostMapping("erropa/gehitu")
	public String produktuaGehitu(@ModelAttribute("produktua") Erropa producto,
			@RequestParam("file") MultipartFile irudia, Model model) {
		
		try {

			if (!irudia.isEmpty()) {
				Path uploadDir = Paths.get("src//main//resources//static/uploads");
				String rutaAbsoluta = uploadDir.toFile().getAbsolutePath();
				byte[] bytesImg = irudia.getBytes();
				Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + irudia.getOriginalFilename());
				Files.write(rutaCompleta, bytesImg);
				producto.setIrudiaUrl(irudia.getOriginalFilename());
				prodRepo.save(producto);
				return "redirect:/produktua/produktuak";
			}else {
				
				model.addAttribute("error", "Ez duzu argazkirik gehitu");
				return "formularioa/produktuaForm.html";
			}
			
			
		} catch (IOException e) {

			e.printStackTrace();

			return "error"; // Si ocurre un error, mostramos una página de error

		}
	}

	@GetMapping("/produktuak")
	public String ikusiProduktuakUser(@RequestParam(value = "tipo", required = false) String tipo, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();

		Erabiltzailea erabiltzailea = erabRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
		model.addAttribute("erabiltzailea", erabiltzailea);

		String rol = auth.getAuthorities().stream().map(authority -> authority.getAuthority()).findFirst().orElse(null);
		model.addAttribute("rola", rol);
		List<Erropa> prod = prodRepo.findAll();
		if (prodRepo.findAll().isEmpty()) {
			model.addAttribute("produktuak", null);
		} else {

			if (tipo != null && !tipo.isEmpty()) {
				prod = prod.stream().filter(p -> p.getTipo().equalsIgnoreCase(tipo)).collect(Collectors.toList());
			}
			if (prod.isEmpty()) {
				model.addAttribute("produktuak", null);
			} else {
				model.addAttribute("produktuak", prod);
			}
		}
		return "taulak/produktuaTaula";
	}
	
	
	@GetMapping("/ezabatu/{id}") 
	public String eliminarProducto(@PathVariable Long id,  Model model) { 
		 
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();

		Erabiltzailea erabiltzailea = erabRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
		model.addAttribute("erabiltzailea", erabiltzailea);

		String rol = auth.getAuthorities().stream().map(authority -> authority.getAuthority()).findFirst().orElse(null);
		model.addAttribute("rola", rol);
		
		Optional<Erropa> produktoa =prodRepo.findById(id);
		   prodRepo.delete(produktoa.get());
		   return "redirect:/produktua/produktuak";
	   }

	@GetMapping("/sortu/outfit")
	public String sortuOutfita(@RequestParam(value = "parte", required = false) String parte,Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();

		Erabiltzailea erabiltzailea = erabRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
		model.addAttribute("erabiltzailea", erabiltzailea);

		String rol = auth.getAuthorities().stream().map(authority -> authority.getAuthority()).findFirst().orElse(null);
		model.addAttribute("rola", rol);
		
		
		
		if (parte != null) {
			List<String> tiposDeParte = categoriaGeneralPorTipo.entrySet().stream()
					.filter(entry -> entry.getValue().equals(parte)).map(Map.Entry::getKey)
					.collect(Collectors.toList());

			List<Erropa> prendas = prodRepo.findByTipoInIgnoreCase(tiposDeParte);
			model.addAttribute("prendas", prendas);
			model.addAttribute("parteSeleccionada", parte);
		}

		return "taulak/outfit";
	}


	private static final Map<String, String> categoriaGeneralPorTipo = Map.of("camiseta", "parteSuperior", "sudadera",
			"parteSuperior", "chaqueta", "parteSuperior", "pantalon", "parteInferior", "falda", "parteInferior",
			"zapatilla", "calzado", "camisa", "parteSuperior", "bolso", "accesorios", "gorra", "accesorios"
	
	);

}
