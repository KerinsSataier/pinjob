package ru.pinjob.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@ConfigurationProperties(prefix="pinjob-settings")
@RequestMapping("/api/v1")
public class FileUploadResource {

    private String pathToSaveDirectory;
    private String saveDirectory;

    private final Logger log = LoggerFactory.getLogger(FileUploadResource.class);

    @RequestMapping(value = "/savefiles", method = RequestMethod.POST)
    public ResponseEntity<List<String>> saveFiles(@RequestParam("file") List<MultipartFile> uploadedFiles,
                                                  Model map) throws IllegalStateException, IOException {
        log.debug("REST request to save files : {}", uploadedFiles);

        List<MultipartFile> files = uploadedFiles;
        List<String> fileUrls = new ArrayList<>();
        File imgFolder = new File(pathToSaveDirectory + saveDirectory);

        if(!imgFolder.exists()){
            imgFolder.mkdirs();
        }

        if (null != files && files.size() > 0) {
            for (MultipartFile multipartFile : files) {

                String fileName = multipartFile.getOriginalFilename();
                if (!"".equalsIgnoreCase(fileName)) {
                    multipartFile.transferTo(new File(pathToSaveDirectory + saveDirectory + fileName));
                    fileUrls.add(saveDirectory+fileName);
                }
            }
        }

        map.addAttribute("fileUrls", fileUrls);
        return new ResponseEntity<>(fileUrls, HttpStatus.OK);
    }

    public String getPathToSaveDirectory() {
        return pathToSaveDirectory;
    }

    public void setPathToSaveDirectory(String pathToSaveDirectory) {
        this.pathToSaveDirectory = pathToSaveDirectory;
    }

    public String getSaveDirectory() {
        return saveDirectory;
    }

    public void setSaveDirectory(String saveDirectory) {
        this.saveDirectory = saveDirectory;
    }
}
