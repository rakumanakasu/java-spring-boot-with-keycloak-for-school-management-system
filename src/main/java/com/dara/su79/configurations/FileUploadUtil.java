
package com.dara.su79.configurations;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {
    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath, new LinkOption[0])) {
                Files.createDirectories(uploadPath);
            }

            InputStream inputStream = multipartFile.getInputStream();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
        } catch (IOException var6) {
            throw new IOException("Could not save image file: " + fileName, var6);
        }
    }

    public static void removePhoto(String uploadDir, String fileName) throws IOException {
        Path uploadPath = Paths.get(uploadDir + "/" + fileName);

        try {
            if (Files.exists(uploadPath, new LinkOption[0])) {
                Files.delete(uploadPath);
            }

        } catch (IOException var4) {
            throw new IOException("Could not save file: " + fileName, var4);
        }
    }
}
