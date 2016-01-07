package com.test;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class Populater {

    private static PDDocument _pdfDocument;

    public static void main(String[] args) throws IOException, COSVisitorException {
        String originalPdf = "in.pdf";
        String targetPdf = "out.pdf";
        populateAndCopy(originalPdf, targetPdf);
        System.out.println("Complete");
    }

    private static void populateAndCopy(String originalPdf, String targetPdf) throws IOException, COSVisitorException {
        InputStream in = PDDocument.class.getClassLoader().getResourceAsStream(originalPdf);
        populateAndCopy(in, targetPdf);
    }

    private static void populateAndCopy(InputStream in, String targetPdf) throws IOException, COSVisitorException {
        _pdfDocument = PDDocument.load(in);
        _pdfDocument.getNumberOfPages();
        //printFields();  //Uncomment to see the fields in this document in console
        setField("Last name.Last name", "LastName Filled");
        _pdfDocument.save(targetPdf);
        _pdfDocument.close();
    }

    public static void setField(String name, String value ) throws IOException {
        PDDocumentCatalog docCatalog = _pdfDocument.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();
        PDField field = acroForm.getField( name );
        if( field != null ) {
            field.setValue(value);
        }
        else {
            System.err.println( "No field found with name:" + name );
        }
    }

    @SuppressWarnings("rawtypes")
    public static void printFields() throws IOException {
        PDDocumentCatalog docCatalog = _pdfDocument.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();
        List fields = acroForm.getFields();
        Iterator fieldsIter = fields.iterator();

        System.out.println(new Integer(fields.size()).toString() + " top-level fields were found on the form");

        while( fieldsIter.hasNext()) {
            PDField field = (PDField)fieldsIter.next();
            processField(field, "|--", field.getPartialName());
        }
    }

    @SuppressWarnings("rawtypes")
    private static void processField(PDField field, String sLevel, String sParent) throws IOException
    {
        List kids = field.getKids();
        if(kids != null) {
            Iterator kidsIter = kids.iterator();
            if(!sParent.equals(field.getPartialName())) {
                sParent = sParent + "." + field.getPartialName();
            }

            System.out.println(sLevel + sParent);

            while(kidsIter.hasNext()) {
                Object pdfObj = kidsIter.next();
                if(pdfObj instanceof PDField) {
                    PDField kid = (PDField)pdfObj;
                    processField(kid, "|  " + sLevel, sParent);
                }
            }
        }
        else {
            String outputString = sLevel + sParent + "." + field.getPartialName() + ",  type=" + field.getClass().getName();
            System.out.println(outputString);
        }
    }
}
