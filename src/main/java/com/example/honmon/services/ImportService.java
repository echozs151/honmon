package com.example.honmon.services;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class ImportService {

    private static final Logger log = LoggerFactory.getLogger(ImportService.class);
    

    public ImportService() {

    }

    public static void importBooks(String path)
    {
        String sysPath = FilenameUtils.separatorsToSystem(path);
        log.debug("Testing log " + sysPath);

        return;

    }
}
