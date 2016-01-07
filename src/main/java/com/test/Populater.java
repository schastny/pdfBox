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

    private final String targetPdf;
    private final PDDocument _pdfDocument;

    public Populater(String originalPdf, String targetPdf ) throws IOException {
        this.targetPdf = targetPdf;
        InputStream in = PDDocument.class.getClassLoader().getResourceAsStream(originalPdf);
        _pdfDocument = PDDocument.load(in);
    }

    /**
     * Populate given field with the given value
     * @param fieldName
     * @param fieldValue
     * @throws IOException
     * @throws COSVisitorException
     */
    public void populateField(String fieldName, String fieldValue) throws IOException, COSVisitorException {
        _pdfDocument.getNumberOfPages();
        setField(fieldName, fieldValue);
    }

    public void saveAndClose() throws IOException, COSVisitorException {
        _pdfDocument.save(targetPdf);
        _pdfDocument.close();
    }

    private void setField(String name, String value) throws IOException {
        PDDocumentCatalog docCatalog = _pdfDocument.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();
        PDField field = acroForm.getField(name);
        if (field != null) {
            field.setValue(value);
        } else {
            System.err.println("No field found with name:" + name);
        }
    }

    /**
     * Show the fields in this document in console
     * @throws IOException
     */
    public void printFields() throws IOException {
        PDDocumentCatalog docCatalog = _pdfDocument.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();
        List fields = acroForm.getFields();
        Iterator fieldsIter = fields.iterator();

        System.out.println(Integer.toString(fields.size()) + " top-level fields were found on the form");

        while (fieldsIter.hasNext()) {
            PDField field = (PDField) fieldsIter.next();
            processField(field, "|--", field.getPartialName());
        }
    }

    private void processField(PDField field, String sLevel, String sParent) throws IOException {
        List kids = field.getKids();
        if (kids != null) {
            Iterator kidsIter = kids.iterator();
            if (!sParent.equals(field.getPartialName())) {
                sParent = sParent + "." + field.getPartialName();
            }

            System.out.println(sLevel + sParent);

            while (kidsIter.hasNext()) {
                Object pdfObj = kidsIter.next();
                if (pdfObj instanceof PDField) {
                    PDField kid = (PDField) pdfObj;
                    processField(kid, "|  " + sLevel, sParent);
                }
            }
        } else {
            String outputString = sLevel + sParent + "." + field.getPartialName() + ",  type=" + field.getClass().getName();
            System.out.println(outputString);
        }
    }
}
