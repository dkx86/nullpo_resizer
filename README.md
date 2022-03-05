# nullpo_resizer v1.0

Console tool for resizing JPG files.

How to run:

`java -jar nullpo_resizer.jar -f big_image.jpg 512`

**Source file will be overwritten.**

### Available commands:

`-f <path/to/file> <MAX_SIDE_SIZE>` - resize one image.

`-d <path/to/directory> <MAX_SIDE_SIZE>` - resize all images in the directory.

Where `<MAX_SIDE_SIZE>` is a size of the longest side of the image, digits or alias.

### Aliases for `<MAX_SIDE_SIZE>`:

**hd**  -> 1280

**fhd** -> 1920

**uhd** -> 3048
