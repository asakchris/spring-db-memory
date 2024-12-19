package com.example.controller;

import com.example.repository.CustomDataRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/data")
public class DataController {
    private static final Logger logger = LoggerFactory.getLogger(DataController.class);
    private static final String GZIP_ENCODING = "gzip";
    
    private final CustomDataRepository dataRepository;
    private final EntityManager entityManager;

    public DataController(CustomDataRepository dataRepository, EntityManager entityManager) {
        this.dataRepository = dataRepository;
        this.entityManager = entityManager;
    }

    @GetMapping(value = "/{id}")
    @Transactional(readOnly = true, timeout = 30)
    public void getJsonData(@PathVariable Long id, HttpServletResponse response) throws Exception {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Encoding", GZIP_ENCODING);
        
        try (OutputStream outputStream = new GZIPOutputStream(response.getOutputStream(), 8192)) {
            dataRepository.streamJsonToOutputStream(id, outputStream);
        } catch (Exception e) {
            logger.error("Error streaming JSON data for id: {}", id, e);
            if (!response.isCommitted()) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            throw e;
        }
    }
} 