package com.edwinvanderwal.filewatcher.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.edwinvanderwal.filewatcher.model.Deelnemer;
import com.edwinvanderwal.filewatcher.model.DeelnemerList;
import com.edwinvanderwal.filewatcher.model.Startnummer;
import com.edwinvanderwal.filewatcher.service.DeelnemerService;
import com.edwinvanderwal.filewatcher.service.TagmapService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


// FIXME Tagmap is not a JOSN, move it to csv file processor
@Component
public class JsonFileProcessor implements FileProcessor {
    public static final String OUTPUT_FOLDER = "output";
    public static final String COMMA_DELIMITER = ",";
    private static Logger logger = LoggerFactory.getLogger(JsonFileProcessor.class);

    private DeelnemerService deelnemerService;
    private TagmapService tagmapService;

     public JsonFileProcessor(DeelnemerService deelnemerService, TagmapService tagmapService) {
         this.deelnemerService = deelnemerService;
         this.tagmapService = tagmapService;
     }

     public void processDeelnemers(Path file) {
        logger.info(String.format("Init processing file %s",file.getFileName()));
        // process JSON....
        try {
            String json = Files.readString(file);
            parseDeelnemersJson(new JSONParser().parse(json).toString());
        } catch (IOException | ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        moveFile(file);
    }

     @Override
    public void processTagMap(Path file) {
        logger.info(String.format("Init processing file %s", file.getFileName()));
        try (Scanner scanner = new Scanner(file)) {
             while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                parseTagmap(line);
             }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        moveFile(file);
    }

    private void parseDeelnemersJson(String jsonString) {
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

    private void parseTagmap(String line) {
        String[] tokens = line.split(COMMA_DELIMITER);
        if (tokens.length >= 2) {
            String startnummer = tokens[0];
            String chipcode = tokens[1];
            //logger.info(String.format("Tagmap read chipcode %s for startnummer %s", chipcode, startnummer));
            tagmapService.save(Startnummer.builder().nummer(startnummer).chipcode(chipcode).build());
        }
    }

    private static void moveFile(Path file) {
        try {
            var destinationFolder = Path.of( 
                file.getParent().toString() + File.separator + OUTPUT_FOLDER );
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
