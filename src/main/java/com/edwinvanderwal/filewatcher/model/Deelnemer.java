package com.edwinvanderwal.filewatcher.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Deelnemer {

    @Id
    @NotNull(message = "referentie field should not be null")
    private Long referentie;

    private String naam;
    private String startnummer;
    private String chipcode;
    private String vereniging;
    private String woonplaats;
    private String onderdeel;
    
    @Override
    public String toString() {
        return "" + startnummer + "" + naam + " " + vereniging + " " + woonplaats + " " + onderdeel + "";
    }

}
