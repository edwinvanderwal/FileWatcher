package com.edwinvanderwal.filewatcher.config;

import java.nio.file.Path;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.edwinvanderwal.filewatcher.CustomerAddFileChangeListener;
import com.edwinvanderwal.filewatcher.FileWatcherProperties;
import com.edwinvanderwal.filewatcher.JsonFileProcessor;

import jakarta.annotation.PreDestroy;

@Configuration
@EnableConfigurationProperties(FileWatcherProperties.class)
public class CustomerFileWatcherConfig {

    private Logger logger = LoggerFactory.getLogger(CustomerFileWatcherConfig.class);
    private final FileWatcherProperties properties;

    private final JsonFileProcessor fileProcessor;

    public CustomerFileWatcherConfig(FileWatcherProperties properties, JsonFileProcessor fileProcessor) {
        this.properties = properties;
        this.fileProcessor = fileProcessor;
    }

    @Bean
    FileSystemWatcher fileSystemWatcher() {
        var fileSystemWatcher = new FileSystemWatcher(
                properties.daemon(),
                Duration.ofSeconds(properties.pollInterval()),
                Duration.ofSeconds(properties.quietPeriod()));
        fileSystemWatcher.addSourceDirectory( 
            Path.of(properties.directory()).toFile());
        fileSystemWatcher.addListener(
            new CustomerAddFileChangeListener(fileProcessor));
        // fileSystemWatcher.setTriggerFilter(
        //     f -> f.toPath().endsWith(".json"));        
        fileSystemWatcher.start();
        logger.info(String.format("FileSystemWatcher initialized.Monitoring directory %s",properties.directory()));

        return fileSystemWatcher;
    }

    @PreDestroy
    public void onDestroy() throws Exception {
        logger.info("Shutting Down File System Watcher.");
        fileSystemWatcher().stop();
    }
}