package ru.dkx86.nullporesizer;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class FileResizer {

    public final static int FULL_HD = 1920;
    public final static int HD = 1280;
    public final static int UHD = 3840; //4k

    private final Path filePath;

    public FileResizer(Path filePath) {
        this.filePath = filePath;
    }

    public void resize(Path destination, int maxSide) throws IOException {
        var bufferedImage = ImageIO.read(filePath.toFile());

        var height = bufferedImage.getHeight();
        var width = bufferedImage.getWidth();


        Scalr.Mode mode = (width >= height) ? Scalr.Mode.AUTOMATIC : Scalr.Mode.FIT_TO_HEIGHT;

        var resized = resizeImage(bufferedImage, maxSide, mode);

        ImageIO.write(resized, "JPG", destination.toFile());
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetSize, Scalr.Mode mode) {
        return Scalr.resize(originalImage, Scalr.Method.ULTRA_QUALITY, mode, targetSize, targetSize, Scalr.OP_ANTIALIAS);
    }
}
