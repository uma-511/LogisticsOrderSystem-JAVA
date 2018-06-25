package cn.wizzer.modules.controllers.open.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

public class ZxingUtil {

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    public static void genQrCode(String text, int width, int height, String format, OutputStream out) {
        try {
            BitMatrix bitMatrix = getBitMatrix(text, width, height);
            writeToStream(bitMatrix, format, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void genOrCode(String text, int width, int height, String format, File file) {
        try {
            BitMatrix bitMatrix = getBitMatrix(text, width, height);
            writeToFile(bitMatrix, format, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage genQrCode(String text, int width, int height) {
        BitMatrix bitMatrix = getBitMatrix(text, width, height);
        BufferedImage image = toBufferedImage(bitMatrix);
        return image;
    }

    private static BitMatrix getBitMatrix(String text, int width, int height) {
        BitMatrix bitMatrix = null;
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitMatrix;
    }

    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int _width = matrix.getWidth();
        int _height = matrix.getHeight();
        BufferedImage image = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < _width; x++) {
            for (int y = 0; y < _height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    public static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }

    public static void writeToStream(BitMatrix matrix, String format, OutputStream stream) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, stream)) {
            throw new IOException("Could not write an image of format " + format);
        }
    }
}
