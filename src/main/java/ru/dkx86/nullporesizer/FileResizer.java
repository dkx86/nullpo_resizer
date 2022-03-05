package ru.dkx86.nullporesizer;

import org.imgscalr.Scalr;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileResizer {

    public final static int FULL_HD = 1920;
    public final static int HD = 1280;
    public final static int UHD = 3840; //4k

    @NotNull
    private final File sourceFile;

    public FileResizer(@NotNull File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public void resize(@NotNull File destinationFile, int maxSide) throws IOException {
        var bufferedImage = ImageIO.read(sourceFile);

        var height = bufferedImage.getHeight();
        var width = bufferedImage.getWidth();

        Scalr.Mode mode = (width >= height) ? Scalr.Mode.AUTOMATIC : Scalr.Mode.FIT_TO_HEIGHT;

        var resized = resizeImage(bufferedImage, maxSide, mode);

        ImageIO.write(resized, "JPG", destinationFile);
    }

    private BufferedImage resizeImage(@NotNull BufferedImage originalImage, int targetSize, @NotNull Scalr.Mode mode) {
        return Scalr.resize(originalImage, Scalr.Method.ULTRA_QUALITY, mode, targetSize, targetSize, Scalr.OP_ANTIALIAS);
    }
}
