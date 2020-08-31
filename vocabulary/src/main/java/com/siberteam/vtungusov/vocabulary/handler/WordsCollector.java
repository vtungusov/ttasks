package com.siberteam.vtungusov.vocabulary.handler;

import com.siberteam.vtungusov.vocabulary.exception.FileIOException;
import com.siberteam.vtungusov.vocabulary.model.Environment;
import com.siberteam.vtungusov.vocabulary.mqbroker.MqBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class WordsCollector {
    public static final String SAVED = "File was saved as";
    private static final String WRITE_ERROR = "Error during file writing ";
    private final Logger logger = LoggerFactory.getLogger(WordsCollector.class);
    private final Environment env;

    public WordsCollector(Environment environment) {
        this.env = environment;
    }

    public void collectWords() {
        MqBroker mqBroker = env.getMqBroker();
        do {
            mqBroker.waitData();
            env.getQueue().drainTo(env.getVocabulary());
            mqBroker.sayNotFull();
            mqBroker.sayEmpty();
        } while (!mqBroker.producersFinished());
        if (!mqBroker.isFileSaved()) {
            saveToFile(env.getOutputFileName(), env.getVocabulary().stream(), mqBroker);
        }
    }

    private synchronized void saveToFile(String outFileName, Stream<String> stringStream, MqBroker mqBroker) {
        try {
            Files.write(Paths.get(outFileName), (Iterable<String>) stringStream::iterator);
            logger.info("{} {}", SAVED, outFileName);
            mqBroker.sayFileSaved();
        } catch (IOException e) {
            throw new FileIOException(WRITE_ERROR + outFileName);
        }
    }
}
