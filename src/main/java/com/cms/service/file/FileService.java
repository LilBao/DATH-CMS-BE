package com.cms.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileService {
    public List<Map> uploadFiles(List<MultipartFile> files, String folderName);
    public List<Map> uploadVideos(List<MultipartFile> files, String folderName);
}
