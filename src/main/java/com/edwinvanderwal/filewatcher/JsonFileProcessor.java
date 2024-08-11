package com.edwinvanderwal.filewatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.edwinvanderwal.filewatcher.service.DeelnemerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Component
public class JsonFileProcessor implements FileProcessor {
    public static final String OUTPUT_FOLDER = "\\output";
    private static Logger logger = LoggerFactory.getLogger(JsonFileProcessor.class);

     private DeelnemerService deelnemerService;

     public JsonFileProcessor(
        DeelnemerService deelnemerService) {
         this.deelnemerService = deelnemerService;
     }

     public void process(Path file) {
        logger.info(String.format("Init processing file %s",file.getFileName()));
        // process JSON....
        try {
            String json = Files.readString(file);
            parseJson(new JSONParser().parse(json).toString());
        } catch (IOException | ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        moveFile(file);
    }

   

    private void parseJson(String jsonString) {
       ObjectMapper om = new ObjectMapper();
    try {
        DeelnemerList deelnemers =  om.readValue(jsonString, DeelnemerList.class);
        logger.info(String.format("Inlezen %s deelnemers", deelnemers.getDeelnemers().size()));
        for (Deelnemer deelnemer : deelnemers.getDeelnemers()) {
            
            deelnemerService.save(deelnemer);
        }
    } catch (JsonMappingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (JsonProcessingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }


    }

    private static void moveFile(Path file) {
        try {
            var destinationFolder = Path.of( 
                file.getParent().toString() + OUTPUT_FOLDER );
            Files.move(
                file, 
                destinationFolder.resolve(file.getFileName()), 
                REPLACE_EXISTING);
            logger.info(String.format(
                 "File %s has been moved to %s",file.getFileName(), 
                 destinationFolder));
        } catch (IOException e) {
            logger.error("Unable to move file "+ file, e);
        }
    }
}
