package com.edwinvanderwal.filewatcher.file;

import java.nio.file.Path;

public interface FileProcessor {
    public void processDeelnemers(Path file);
    public void processTagMap(Path file);
}