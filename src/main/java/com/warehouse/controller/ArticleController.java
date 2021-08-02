package com.warehouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.exception.custom.FileProcessException;
import com.warehouse.model.Articles;
import com.warehouse.model.entity.Article;
import com.warehouse.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * This is the controller layer of Articles.
 * 7/29/21
 *
 * @author ahmetcetin
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/articles")
@Slf4j
public class ArticleController {
    private final ArticleService articleService;
    private final ObjectMapper objectMapper;

    @GetMapping
    @Operation(summary = "Get all articles")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Found articles"))
    public ResponseEntity<List<Article>> get() {
        return ResponseEntity.ok(articleService.getAll());
    }

    // It is a PUT, because we both create or update the Article is exists. It is Idempotent.
    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Add new articles into the inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Added articles"),
            @ApiResponse(responseCode = "400", description = "Couldn't process the file"),
            @ApiResponse(responseCode = "415", description = "Unsupported media type")
    })
    public ResponseEntity<List<Article>> save(@Parameter(description = "File which contains articles to add")
                                              @RequestParam("articlesFile") MultipartFile articlesFile) {
        try {
            String articlesAsString = new String(articlesFile.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            Articles articles = objectMapper.readValue(articlesAsString, Articles.class);
            return ResponseEntity.ok(articleService.add(articles.getArticles()));
        } catch (IOException e) {
            log.error("Couldn't process inventory file: {}!", articlesFile.getName(), e);
            throw new FileProcessException("Couldn't process inventory file: " + articlesFile.getName() + "!");
        }
    }
}
