package com.increff.invoice.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StringUtil {

    public static String encode(File pdfFile) throws IOException {
        // Read the PDF file and encode it as a base64 string
        byte[] pdfBytes = readBytesFromFile(pdfFile);
        String base64String = encodeBytesToBase64(pdfBytes);

        // Delete the temporary PDF file
        pdfFile.delete();

        return base64String;
    }

    private static byte[] readBytesFromFile(File file) throws IOException {
        byte[] buffer = new byte[(int) file.length()];
        try (InputStream inputStream = new FileInputStream(file)) {
            inputStream.read(buffer);
        }
        return buffer;
    }

    private static String encodeBytesToBase64(byte[] bytes) {
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }
}
