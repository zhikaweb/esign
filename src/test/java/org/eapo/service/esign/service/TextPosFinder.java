package org.eapo.service.esign.service;

import org.eapo.service.esign.service.stamper.TextPositionFinder;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TextPosFinder {

    @Ignore
    @Test
    public void find() throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("document.pdf").getFile());

        byte[] pdf = Files.readAllBytes(file.toPath());

        TextPositionFinder textPositionFinder = new TextPositionFinder();

        TextPositionFinder.Position position = textPositionFinder.position(pdf, "факса");
    }
}
