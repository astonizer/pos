package com.increff.pos.util;

import com.increff.pos.model.data.OrderInvoiceDetails;
import com.increff.pos.service.exception.ApiException;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

public class FileUtil {

    private static final String INVOICE_PDF_DIRECTORY = "src/main/resources/invoice/";

    public static boolean doesInvoicePdfExist(Integer orderId) {
        String pdfFileName = "invoice_" + orderId + ".pdf";
        File file = new File(INVOICE_PDF_DIRECTORY + pdfFileName);
        return file.exists();
    }

    public static ResponseEntity<byte[]> downloadInvoice(OrderInvoiceDetails orderInvoiceDetails) throws ApiException {
        Integer orderId = orderInvoiceDetails.getId();

        try {
            byte[] pdfBytes = FileUtil.readInvoicePdf(orderId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice_" + orderId + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public static ResponseEntity<byte[]> saveAndDownloadInvoicePdf(String base64Str, OrderInvoiceDetails orderInvoiceDetails) throws ApiException {
        Integer orderId = orderInvoiceDetails.getId();
        saveInvoicePdf(base64Str, orderId);
        return downloadInvoice(orderInvoiceDetails);
    }

    private static byte[] readInvoicePdf(Integer orderId) throws IOException {
        String pdfFileName = "invoice_" + orderId + ".pdf";
        File file = new File(INVOICE_PDF_DIRECTORY + pdfFileName);
        return Files.readAllBytes(file.toPath());
    }

    private static byte[] decodeBase64ToTemporaryPdf(String base64Str) throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Str);

        // Create a temporary PDF file
        File tempFile = File.createTempFile("/invoice", ".pdf");
        Path tempFilePath = tempFile.toPath();

        // Write the decoded bytes to the temporary file
        Files.write(tempFilePath, decodedBytes, StandardOpenOption.CREATE);

        // Read the contents of the temporary file
        byte[] pdfBytes = Files.readAllBytes(tempFilePath);

        // Delete the temporary file
        FileUtils.forceDelete(tempFile);

        return pdfBytes;
    }

    private static void saveInvoicePdf(String base64Str, Integer orderId) throws ApiException {
        try {
            byte[] pdfBytes = FileUtil.decodeBase64ToTemporaryPdf(base64Str);
            String pdfFileName = "invoice_" + orderId + ".pdf";
            try (FileOutputStream fos = new FileOutputStream(INVOICE_PDF_DIRECTORY + pdfFileName)) {
                fos.write(pdfBytes);
            }
        } catch (IOException e) {
            throw new ApiException(e.getMessage());
        }
    }

}
