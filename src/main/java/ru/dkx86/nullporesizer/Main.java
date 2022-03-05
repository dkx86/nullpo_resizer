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

        return dir.listFiles(file -> file.isFile() && file.getName().endsWith("jpg"));
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
        info("Available commands:");
        info("-f <path/to/file> <MAX_SIDE_SIZE> - resize one image.");
        info("-d <path/to/directory> <MAX_SIDE_SIZE> - resize all images in the directory.");
        info("Where <MAX_SIDE_SIZE> is a size of the longest side of the image, digits or alias.");
        info("Aliases for <MAX_SIDE_SIZE>:");
        info("hd  -> 1280");
        info("fhd -> 1920");
        info("uhd -> 3048");
    }

    private static void info(@NotNull String msg) {
        System.out.println(" -INFO- " + msg);
    }

    private static void error(@NotNull String msg) {
        System.err.println(" -ERROR- " + msg);
    }
}
