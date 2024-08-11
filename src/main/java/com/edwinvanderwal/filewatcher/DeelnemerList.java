package com.edwinvanderwal.filewatcher;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class DeelnemerList {

    List<Deelnemer> deelnemers;
}
