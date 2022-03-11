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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        if (args.length != 3) {
            printHelp();
            return;
        }
        var cmd = args[0];
        var pathStr = args[1];
        final Integer size;
        switch (args[2].toLowerCase(Locale.ROOT)) {
            case "hd":
                size = FileResizer.HD;
                break;
            case "fhd":
                size = FileResizer.FULL_HD;
                break;
            case "uhd":
                size = FileResizer.UHD;
                break;
            default:
                size = Integer.getInteger(args[2]);
        }

        if (size == null) {
            printHelp();
            return;
        }

        switch (cmd) {
            case "-f":
                resizeOneFile(pathStr, size);
                break;
            case "-d":
                resizeDirectory(pathStr, size);
                break;
            default:
                printHelp();
        }
    }

    private static void resizeDirectory(@NotNull String path, int size) {
        var files = getFiles(path);
        if (files == null) {
            error("'" + path + "' is not a directory");
            return;
        }

        info("Start processing " + files.length + " files.");
        var executor = Executors.newCachedThreadPool();
        for (var file : files) {
            executor.execute(() -> resizeOneFile(file, size));
        }

        final var start = System.currentTimeMillis();
        executor.shutdown();
        try {
            //noinspection ResultOfMethodCallIgnored
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
            final var end = System.currentTimeMillis();
            var totalTime = getTime(start, System.currentTimeMillis());
            info("Processed " + files.length + " files. Finished at " + totalTime);
        } catch (InterruptedException e) {
            error(e.getMessage());
        }
    }

    @NotNull
    private static String getTime(long start, long end) {
        var total = end - start;

        var minutes = total / (1000 * 60);
        var seconds = (total % (1000 * 60)) / 1000;
        var millis = total % 1000;

        return minutes + "m " + seconds + "s " + millis + "ms";
    }

    @Nullable
    private static File[] getFiles(@NotNull String path) {
        var dir = Paths.get(path).toFile();
        if (!dir.isDirectory())
            return null;

        return dir.listFiles(file -> file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith("jpg"));
    }

    private static void resizeOneFile(@NotNull String sourcePath, int size) {
        resizeOneFile(Paths.get(sourcePath).toFile(), size);
    }

    private static void resizeOneFile(@NotNull File source, int size) {

        final FileResizer resizer = new FileResizer(source);
        try {
            resizer.resize(source, size);
            info("Resized file saved to '" + source + "'");
        } catch (IOException e) {
            error("Cannot resize file: '" + source.getAbsolutePath() + "' Reason:" + e.getMessage());
        }
    }

    private static void printHelp() {
        print("nullpo_resizer v1.0  Copyright (C) 2022  Dmitry Kuznetsov aka dkx86");
        print("This program comes with ABSOLUTELY NO WARRANTY; for details read `show w'.");
        print("This is free software, and you are welcome to redistribute it");
        print("under certain conditions; read LICENSE file or visit <https://www.gnu.org/licenses/> for details.\n");

        print("Available commands:");
        print("-f <path/to/file> <MAX_SIDE_SIZE> - resize one image.");
        print("-d <path/to/directory> <MAX_SIDE_SIZE> - resize all images in the directory.");
        print("Where <MAX_SIDE_SIZE> is a size of the longest side of the image, digits or alias.\n");
        print("Aliases for <MAX_SIDE_SIZE>:");
        print("hd  -> 1280");
        print("fhd -> 1920");
        print("uhd -> 3048");


    }

    private static void print(@NotNull String msg) {
        System.out.println(msg);
    }

    private static void info(@NotNull String msg) {
        System.out.println(" -INFO- " + msg);
    }

    private static void error(@NotNull String msg) {
        System.err.println(" -ERROR- " + msg);
    }
}
