package com.capstoneproject.server.util;

import com.capstoneproject.server.common.constants.Constant;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dai.le-anh
 * @since 6/17/2023
 */

@Component
@RequiredArgsConstructor
@Log4j2
public class CloudinaryUtils {
    private final ServletContext context;
    private final Cloudinary cloudinary;
    public String resolveText(String description) {
        Matcher matcher = Pattern.compile(Constant.IMAGE_TAG_REGEX).matcher(description);
        while (matcher.find()) {
            String base64Image = matcher.group().split(",")[1];
            byte[] imageBytes = DatatypeConverter.parseBase64Binary(base64Image);
            try (InputStream in = new ByteArrayInputStream(imageBytes)) {
                BufferedImage image = ImageIO.read(in);
                File outPut = new File(context.getRealPath(""), "/activity" + System.currentTimeMillis() + ".png");
                ImageIO.write(image, "png", outPut);
                var uploadResult = cloudinary.uploader().upload(outPut, ObjectUtils.emptyMap());
                log.info("Upload success {}", uploadResult.size());
                description = description.replace(matcher.group(), "src=\"" + uploadResult.get("url") + "\"");
                if (outPut.delete()) {
                    log.info("Delete tmp image successfully");
                } else {
                    log.error("Failure when delete tmp image");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return description;
    }
}
