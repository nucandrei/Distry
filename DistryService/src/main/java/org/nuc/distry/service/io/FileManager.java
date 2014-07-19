package org.nuc.distry.service.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class FileManager {
    private static final Object NEW_LINE = "\n";

    public Document loadXMLDocument(String documentName) throws JDOMException, IOException {
        final File file = new File(documentName);
        final SAXBuilder builder = new SAXBuilder();
        return builder.build(file);
    }

    public String loadTextFile(String filepath) throws IOException {
        final File file = new File(filepath);
        try (final BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            final StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(NEW_LINE);
            }
            return stringBuilder.toString();
        }
    }
}
