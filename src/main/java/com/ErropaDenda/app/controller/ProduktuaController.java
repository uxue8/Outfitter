package com.ErropaDenda.app.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
import org.springframework.web.multipart.MultipartFile;


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
/*
	@GetMapping("/admin/produktuak")
	public String ikusiProduktuak(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();

		Erabiltzailea erabiltzailea = erabRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
		model.addAttribute("erabiltzailea", erabiltzailea);

		String rol = auth.getAuthorities().stream().map(authority -> authority.getAuthority()).findFirst().orElse(null);
		model.addAttribute("rola", rol);
		Iterable<Erropa> prod = prodRepo.findAll();
		if (prodRepo.findAll().isEmpty()) {
			model.addAttribute("produktuak", null);
		} else {
			
			model.addAttribute("produktuak", prod);
		}

		return "taulak/produktuaTaula";
	}

	@GetMapping("/admin/gehitu")
	public String showAddProductForm(Model model) {
		model.addAttribute("produktua", new Erropa());
		return "/formularioa/produktuaForm";

	}



	@GetMapping("/admin/ezabatu/{id}")
	public String eliminarProducto(@PathVariable Long id) {
		Optional<Erropa> produktoa = prodRepo.findById(id);

		if (produktoa.isPresent()) {
			Erropa produktua = produktoa.get();

			List<Cesta> cestas = cestaRepository.findByProduktuak(produktua);
			for (Cesta cesta : cestas) {
				cesta.getProduktuak().remove(produktua);

				cestaRepository.save(cesta);
			}
			List<Erabiltzailea> erab = erabRepository.findByErropak(produktua);
			for (Erabiltzailea erabiltzailea : erab) {
				erabiltzailea.getErropak().remove(produktua);
				erabRepository.save(erabiltzailea);
			}
			prodRepo.delete(produktua);
		} else {
			System.out.println("Producto no encontrado con id: " + id);
		}

		return "redirect:/produktua/admin/produktuak";
	}*/

	@GetMapping("/admin/editatu/{id}")
	public String aldatuProd(@PathVariable Long id, Model model) {
		Optional<Erropa> prod = prodRepo.findById(id);
		if (prod.isPresent()) {
			model.addAttribute("produktua", prod.get());
		} else {
			return "error/notFound";
		}
		return "formularioa/aldatuProd";
	}

	@PostMapping("/admin/editatu/{id}")
	public String aldatuProduktua(@ModelAttribute("produktua") Erropa producto,
			@RequestParam("file") MultipartFile irudia, Model model) {
		try {
			if (!irudia.isEmpty()) {
				Path uploadDir = Paths.get("src/main/resources/static/uploads");
				String rutaAbsoluta = uploadDir.toFile().getAbsolutePath();
				byte[] bytesImg = irudia.getBytes();
				Path rutaCompleta = Paths.get(rutaAbsoluta + "/" + irudia.getOriginalFilename());
				Files.write(rutaCompleta, bytesImg);
				producto.setIrudiaUrl(irudia.getOriginalFilename());
			}

			prodRepo.save(producto);

			return "redirect:/produktua/admin/produktuak";
		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", e.getMessage());
			return "error";
		}
	}
	
	@GetMapping("/gehitu")
	public String showAddProductForm(Model model) {
		model.addAttribute("produktua", new Erropa());
		return "/formularioa/produktuaForm";

	}
	
	
	@PostMapping("erropa/gehitu")
	public String produktuaGehitu(@ModelAttribute("produktua") Erropa producto,
			@RequestParam("file") MultipartFile irudia, Model model) {
		boolean produktuaExistitzenDa = false;
		try {

			if (!irudia.isEmpty()) {
				Path uploadDir = Paths.get("src//main//resources//static/uploads");
				String rutaAbsoluta = uploadDir.toFile().getAbsolutePath();
				byte[] bytesImg = irudia.getBytes();
				Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + irudia.getOriginalFilename());
				Files.write(rutaCompleta, bytesImg);
				producto.setIrudiaUrl(irudia.getOriginalFilename());
			}
			List<Erropa> produktuLista = prodRepo.findAll();
			for (Erropa produktua : produktuLista) {
				if (produktua.getTipo().equals(producto.getTipo())) {
					model.addAttribute("error", producto.getTipo());
					produktuaExistitzenDa = true;
				}
			}

			if (!produktuaExistitzenDa) {
				prodRepo.save(producto);
				return "redirect:/produktua/produktuak";
			} else {
				return "formularioa/produktuaForm.html";
			}

		} catch (IOException e) {

			e.printStackTrace();

			return "error"; // Si ocurre un error, mostramos una página de error

		}
	}

	@GetMapping("/produktuak")
	public String ikusiProduktuakUser( @RequestParam(value = "tipo", required = false) String tipo,
                                              Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();

		Erabiltzailea erabiltzailea = erabRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
		model.addAttribute("erabiltzailea", erabiltzailea);

		String rol = auth.getAuthorities().stream().map(authority -> authority.getAuthority()).findFirst().orElse(null);
		model.addAttribute("rola", rol);
		List<Erropa> prod = prodRepo.findAll();
		if ( prodRepo.findAll().isEmpty()) {
			model.addAttribute("produktuak", null);
		} else {
			
		 
			 if (tipo != null && !tipo.isEmpty()) {
	               prod = prod.stream()
	                                    .filter(p -> p.getTipo().equalsIgnoreCase(tipo))
	                                    .collect(Collectors.toList());
	           }
	           if (prod.isEmpty()) {
	               model.addAttribute("produktuak", null);
	           } else {
	               model.addAttribute("produktuak", prod);
	           }
		}
		return "taulak/produktuaTaula";
	}
	



/*
	@GetMapping("/produktuak/erosi/{id}")
	public String produktuaErosiUser(@PathVariable Long id, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();

		Erabiltzailea erabiltzailea = erabRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
		model.addAttribute("erabiltzailea", erabiltzailea);

		Optional<Cesta> cestaGuztiak = cestaRepository.findByErabiltzailea(erabiltzailea);

		Cesta cesta = cestaGuztiak.get();

		if (cesta.getProduktuak() == null) {
			cesta.setProduktuak(new HashSet<>());
		}
		Optional<Erropa> producto = prodRepo.findById(id);
		cesta.getProduktuak().add(producto.get());
		cestaRepository.save(cesta);
		erabiltzailea.getErropak().add(producto.get());
		erabRepository.save(erabiltzailea);
		//para saber si esta comprando en gizona o emakumea
		
			return "redirect:/produktua/produktuak";
		
		
	}*/

	
}
