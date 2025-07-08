package com.edwinvanderwal.filewatcher;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.devtools.filewatch.ChangedFile;
import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.filewatch.FileChangeListener;



public class CustomerAddFileChangeListener implements FileChangeListener {
    private static Logger logger = LoggerFactory.getLogger( 
        CustomerAddFileChangeListener.class);

    private JsonFileProcessor fileProcessor;

    public CustomerAddFileChangeListener(
        JsonFileProcessor fileProcessor) {
        this.fileProcessor = fileProcessor;
    }

    @Override
    public void onChange(Set<ChangedFiles> changeSet) {
        logger.debug("File changed");
        for(ChangedFiles files : changeSet)
            for(ChangedFile file: files.getFiles()) {
                logger.info("{} {}", file.getFile().getAbsolutePath() , file.getType());
                if (file.getType().equals(ChangedFile.Type.ADD))
                    fileProcessor.process(file.getFile().toPath());
            }
    }
}