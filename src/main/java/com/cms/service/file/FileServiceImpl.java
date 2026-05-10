package com.cms.service.file;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService{
    private final Cloudinary cloudinary;

    @Override
    public List<Map> uploadFiles(List<MultipartFile> files, String folderName) {
        List<Map> results = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                Map result = cloudinary.uploader().upload(file.getBytes(),
                        ObjectUtils.asMap(
                                "folder", folderName
                        ));
                results.add(result);
            } catch (IOException exception) {
                results.add(ObjectUtils.asMap(
                        "error", true,
                        "file", file.getOriginalFilename(),
                        "message", exception.getMessage()
                ));
            }
        }
        return results;
    }
    
    @Override
    public List<Map> uploadVideos(List<MultipartFile> files, String folderName){
        List<Map> results = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                Map result = cloudinary.uploader().upload(file.getBytes(),
                        ObjectUtils.asMap(
                                "resource_type", "video",
                                "folder", folderName
                        ));
                results.add(result);
            } catch (IOException exception) {
                results.add(ObjectUtils.asMap(
                        "error", true,
                        "file", file.getOriginalFilename(),
                        "message", exception.getMessage()
                ));
            }
        }
        return results;
    }
}
