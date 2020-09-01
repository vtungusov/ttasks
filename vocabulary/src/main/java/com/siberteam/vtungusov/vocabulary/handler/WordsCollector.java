package com.siberteam.vtungusov.vocabulary.handler;

import com.siberteam.vtungusov.vocabulary.broker.WordsBroker;
import com.siberteam.vtungusov.vocabulary.exception.FileIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;

import static com.siberteam.vtungusov.vocabulary.broker.WordsBroker.POISON_PILL;

public class WordsCollector {
    private static final String SAVED = "File was saved as";
    private static final String WRITE_ERROR = "Error during file writing ";
    private final Logger logger = LoggerFactory.getLogger(WordsCollector.class);

    public void collectWords(WordsBroker broker, Set<String> vocabulary, String outputFileName) {
        do {
            String word = broker.readWord();
            if (word.equals(POISON_PILL)) {
                break;
            }
            vocabulary.add(word);
        } while (true);
        if (!broker.isFileSaved()) {
            saveToFile(broker, vocabulary.stream(), outputFileName);
            broker.setFileSaved();
        }
        broker.putWord(POISON_PILL);
    }

    private synchronized void saveToFile(WordsBroker broker, Stream<String> stringStream, String fileName) {
        try {
            Files.write(Paths.get(fileName), (Iterable<String>) stringStream::iterator);
            logger.info("{} {}", SAVED, fileName);
            broker.setFileSaved();
        } catch (IOException e) {
            throw new FileIOException(WRITE_ERROR + fileName);
        }
    }
}
