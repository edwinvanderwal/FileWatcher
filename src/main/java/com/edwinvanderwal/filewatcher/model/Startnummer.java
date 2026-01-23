package com.edwinvanderwal.filewatcher.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@Builder
public class Startnummer {

    public Startnummer() {
    }
    
    public Startnummer(String nummer, String chipcode) {
        this.nummer = nummer;
        this.chipcode = chipcode;
    }

    @Id
    private String nummer;

    private String chipcode;

}
