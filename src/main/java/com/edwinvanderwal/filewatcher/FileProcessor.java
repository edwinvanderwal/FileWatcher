package com.edwinvanderwal.filewatcher;

import java.nio.file.Path;

public interface FileProcessor {
    public void process(Path file);
}