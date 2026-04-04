package com.agora.assemblee.storage.service;

import com.agora.assemblee.config.AppProperties;
import com.agora.assemblee.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalStorageService implements StorageService {
    private final AppProperties properties;

    @Override
    public String store(MultipartFile file, String subfolder) {
        try {
            Path root = Path.of(properties.getStorage().getRoot(), subfolder).toAbsolutePath().normalize();
            Files.createDirectories(root);
            String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
            Path target = root.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return target.toString();
        } catch (IOException ex) {
            throw new BusinessException("Impossible de stocker le fichier: " + ex.getMessage());
        }
    }

    @Override
    public Resource loadAsResource(String path) {
        return new FileSystemResource(path);
    }
}
