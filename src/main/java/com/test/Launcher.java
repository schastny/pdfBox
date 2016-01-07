package com.test;

import org.apache.pdfbox.exceptions.COSVisitorException;

import java.io.IOException;

public class Launcher {

    public static void main(String[] args) throws IOException, COSVisitorException {
        final String originalPdf = "in.pdf";
        final String targetPdf = "out.pdf";

        System.out.printf("Filling up the form %s. Check out result in %s\n", originalPdf, targetPdf);

        Populater p = new Populater(originalPdf, targetPdf);
//        p.printFields();
        p.populateField("Last name.Last name", "LastName Filled");
        p.saveAndClose();

        System.out.println("Filling Complete");
    }

}
