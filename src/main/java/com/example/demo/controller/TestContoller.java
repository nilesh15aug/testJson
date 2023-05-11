package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.catalina.startup.ClassLoaderFactory.Repository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestContoller {

	@Value("${git.repository.path}")
    private String repositoryPath;

	@GetMapping("/all")
	public String all() {
		return "data";

	}
	
	@PostMapping("/commit")
    public ResponseEntity<String> commitJsonData(@RequestBody String jsonData) {
        try (org.eclipse.jgit.lib.Repository repository = FileRepositoryBuilder.create(new File(repositoryPath))) {
            Git git = new Git(repository);
            repository.getWorkTree().mkdirs();
            File file = new File(repository.getWorkTree(), "data.json");
            Files.write(file.toPath(), jsonData.getBytes());
            git.add().addFilepattern("data.json").call();
            git.commit().setMessage("Committing JSON data").call();
        } catch (RepositoryNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Git repository not found");
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error committing JSON data");
        }

        return ResponseEntity.ok("JSON data committed successfully");
    }	 
}
