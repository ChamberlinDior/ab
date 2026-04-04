package com.agora.assemblee.storage.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String store(MultipartFile file, String subfolder);
    Resource loadAsResource(String path);
}
