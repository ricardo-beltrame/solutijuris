package com.solutijuris.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String upload(MultipartFile file, String pasta) throws IOException {
        Map<String, Object> params = ObjectUtils.asMap(
                "folder", "solutijuris/" + pasta,
                "transformation", ObjectUtils.asMap(
                        "width", 300,
                        "height", 300,
                        "crop", "fill",
                        "gravity", "face"
                )
        );

        Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), params);
        return (String) result.get("secure_url");
    }

    public void delete(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    public String extractPublicId(String url) {
        if (url == null || url.isEmpty()) return null;
        int lastSlash = url.lastIndexOf("/");
        int lastDot = url.lastIndexOf(".");
        if (lastSlash == -1 || lastDot == -1) return null;
        return url.substring(lastSlash + 1, lastDot);
    }
}