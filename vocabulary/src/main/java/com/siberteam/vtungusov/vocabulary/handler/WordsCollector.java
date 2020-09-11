package com.siberteam.vtungusov.vocabulary.handler;

import com.siberteam.vtungusov.vocabulary.broker.WordsBroker;
import com.siberteam.vtungusov.vocabulary.exception.FileIOException;
import com.siberteam.vtungusov.vocabulary.exception.ThreadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;

import static com.siberteam.vtungusov.vocabulary.broker.WordsBroker.THREAD_INTERRUPT;

public class WordsCollector {
    private static final String SAVED = "File was saved as";
    public static final String WRITE_ERROR = "Error during file writing ";

    private final Logger log = LoggerFactory.getLogger(WordsCollector.class);

    public void collectWords(WordsBroker broker, Set<String> vocabulary, String outputFileName) {
        broker.registerCollector();
        String word;
        do {
            word = broker.readWord();
            if (word != null) {
                vocabulary.add(word);
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new ThreadException(THREAD_INTERRUPT);
                }
            }
        } while (!broker.isTimeToEnd() || word != null);
        broker.unregisterCollector();
        saveToFile(broker, vocabulary.stream(), outputFileName);
    }

    private void saveToFile(WordsBroker broker, Stream<String> stringStream, String fileName) {
        broker.awaitCollectorsFinish();
        if (broker.trySave() && !broker.isFileSaved()) {
            try {
                Files.write(Paths.get(fileName), (Iterable<String>) stringStream::iterator);
                log.info("{} {}", SAVED, fileName);
                broker.setFileSaved();
            } catch (IOException e) {
                throw new FileIOException(WRITE_ERROR + fileName);
            }
        }
    }
}
