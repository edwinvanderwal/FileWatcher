package com.edwinvanderwal.filewatcher.file;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@ConfigurationProperties(prefix = "application.file.watch")
public record FileWatcherProperties(
    @NotBlank String directory,
    boolean daemon,
    @Positive Long pollInterval,
    @Positive Long quietPeriod
) {}
