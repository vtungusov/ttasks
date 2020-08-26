package com.siberteam.vtungusov.vocabulary.handler;

import com.siberteam.vtungusov.vocabulary.exception.FileIOException;
import com.siberteam.vtungusov.vocabulary.model.Order;
import com.siberteam.vtungusov.vocabulary.util.FileUtil;
import org.apache.commons.validator.routines.UrlValidator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.siberteam.vtungusov.vocabulary.handler.UrlHandler.BAD_URL;

public class VocabularyMaker {
    public static final String WRITE_ERROR = "Error during file writing ";

    public void collectVocabulary(Order order) throws IOException {
        validateFiles(order);
        try (Stream<String> lines = Files.lines(Paths.get(order.getInputFileName()))) {
            List<URL> urls = lines
                    .filter(validateString())
                    .map(convertToURL())
                    .collect(Collectors.toList());
            WordsCollector collector = new WordsCollector();
            Stream<String> wordsStream = collector.collectWordsFromURLs(urls);
            saveToFile(order.getOutputFileName(), wordsStream);
        }
    }

    private void saveToFile(String outFileName, Stream<String> stringStream) {
        try {
            Files.write(Paths.get(outFileName), (Iterable<String>) stringStream::iterator);
        } catch (IOException e) {
            throw new FileIOException(WRITE_ERROR + outFileName);
        }
    }

    private void validateFiles(Order order) throws IOException {
        FileUtil.checkInputFile(order.getInputFileName());
        FileUtil.checkOutputFile(order.getOutputFileName());
    }

    private Function<String, URL> convertToURL() {
        return s -> {
            try {
                return new URL(s);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(BAD_URL + s);
            }
        };
    }

    private Predicate<String> validateString() {
        return s -> {
            UrlValidator validator = new UrlValidator();
            return validator.isValid(s);
        };
    }
}
