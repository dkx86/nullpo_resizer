/**
 * nullpo_resizer - Console tool for resizing JPG files.
 * Copyright (C) 2022  Dmitry Kuznetsov aka dkx86
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
