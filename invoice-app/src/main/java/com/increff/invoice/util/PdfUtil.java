package com.increff.invoice.util;

import org.apache.fop.apps.*;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

public class PdfUtil {

    public static File generatePdfFromTemplate(String xmlFilePath, String xslFilePath) throws IOException, TransformerException, FOPException {
        // Temporary pdf file name
        String pdfName = "invoice";

        // Load the XSL template file
        File xslFile = new File(xslFilePath);

        // Create a temporary output file for the PDF
        File pdfFile = File.createTempFile(pdfName, ".pdf");

        // Configure FOP
        FopFactory fopFactory = FopFactory.newInstance();
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

        // Create the output stream for the PDF
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(pdfFile));

        try {
            // Create the FOP transformation
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, outputStream);

            // Load the XML data as a StreamSource
            StreamSource xmlSource = new StreamSource(new File(xmlFilePath));

            // Create a transformer for the XSL template
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(xslFile));

            // Perform the transformation and generate the PDF
            Result result = new SAXResult(fop.getDefaultHandler());
            transformer.transform(xmlSource, result);
        } finally {
            outputStream.close();
        }

        return pdfFile;
    }
}
