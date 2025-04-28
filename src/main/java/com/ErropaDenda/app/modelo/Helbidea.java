package com.ErropaDenda.app.modelo;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Helbidea {

	private String kalea;
	private String postaKodea;
	private String hiria;
	private String herrialdea;

}
