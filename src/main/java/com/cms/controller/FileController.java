package com.cms.controller;

import com.cms.common.response.ApiResponse;
import com.cms.service.file.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Tag(name = "File", description = "Các API quản lý upload file và video (Cloudinary, etc)")
@RequiredArgsConstructor
@RestController
@RequestMapping("${server.api-prefix}/files")
public class FileController {
    private final FileService fileService;

    @Operation(summary = "Upload tệp", description = "Tải lên một hoặc nhiều tệp tin (hình ảnh, tài liệu) vào một thư mục cụ thể.")
    @PostMapping("/upload")
    public ApiResponse<?> uploadFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam String folderName) {

        List<Map> results = fileService.uploadFiles(files, folderName);
        return ApiResponse.ok("Upload files success", results);
    }

    @Operation(summary = "Upload video", description = "Tải lên một hoặc nhiều tệp video vào một thư mục cụ thể.")
    @PostMapping("/upload-video")
    public ApiResponse<?> uploadVideos(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam String folderName) {

        List<Map> results = fileService.uploadVideos(files, folderName);
        return ApiResponse.ok("Upload videos success", results);
    }
}
