package ru.dkx86.nullporesizer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;

public class Main {

    public static void main(String[] args) {
        if (args.length != 3) {
            printHelp();
            return;
        }
        var cmd = args[0];
        var path = args[1];
        Integer size;
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
                resizeOneFile(path, size);
                break;
            case "-d":
                resizeDirectory(path, size);
                break;
            default:
                printHelp();
        }
    }

    private static void resizeDirectory(String path, int size) {
        // TODO
    }

    private static void resizeOneFile(String path, int size) {
        var source = Paths.get(path);
        var dir = source.getParent();
        var fileName = source.getFileName();

        final FileResizer resizer = new FileResizer(source);

        var destination = dir.resolve("p_" + fileName);

        try {
            resizer.resize(destination, size);
            System.out.println("Saved to '" + destination + "'");
        } catch (IOException e) {
            System.err.println("Cannot resize file: '" + path + "' Reason:" + e.getMessage());
        }
    }

    private static void printHelp() {
        System.out.println("Incorrect arguments!");
        System.out.println("Try one of these:");
        System.out.println("-f <path/to/file> <MAX_SIDE_SIZE> - to resize one image.");
        System.out.println("-d <path/to/directory> <MAX_SIDE_SIZE> - to resize all images in the directory.");
        System.out.println("Where is <MAX_SIDE_SIZE> - is size of longest side of the image, digits or aliases.");
        System.out.println("Aliases for <MAX_SIDE_SIZE>:");
        System.out.println("hd  -> 1280");
        System.out.println("fhd -> 1920");
        System.out.println("uhd -> 3048");
    }
}
