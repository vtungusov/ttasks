package com.siberteam.vtungusov.vocabulary.handler;

import com.siberteam.vtungusov.vocabulary.exception.FileIOException;
import com.siberteam.vtungusov.vocabulary.util.FileUtil;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.siberteam.vtungusov.vocabulary.handler.WordsCollector.WRITE_ERROR;

public class Anagramer {
    public static final String ANAGRAM_FILE_POSTFIX = "with_anagrams";
    public static final String EXTENSION_DELIMITER = ".";
    private static final String POSTFIX_DELIMITER = "_";
    public static final String ANAGRAM_DELIMITER = ", ";
    public static final String PREFIX_DELIMITER = ": ";
    public static final String SUFFIX = "";
    public static final String INFORMER_DEAD = "Now you're alone, keep on waiting";
    public static final String ANAGRAMS_SEARCHING = "Started anagrams searching";
    public static final int MESSAGE_DELAY = 3_000;
    public static final String INFORM_SYMBOL = ".";
    public static final String FILE_SAVED = "File was handled and re saved with anagrams as";

    private final Logger log = LoggerFactory.getLogger(Anagramer.class);

    public void findAnagrams(String outputFileName) throws IOException {
        startInformer();
        FileUtil.checkInputFile(outputFileName);
        final Map<String, String> vocabulary = getVocabulary(outputFileName);
        CompletableFuture.supplyAsync(() -> collectAnagrams(vocabulary))
                .thenApply(anagramMap -> getAnagrams(vocabulary, anagramMap))
                .thenAccept(anagrams -> saveToFile(outputFileName, anagrams))
                .join();
    }

    private Map<String, String> getVocabulary(String outputFileName) {
        try (final Stream<String> lines = Files.lines(Paths.get(outputFileName))) {
            return lines
                    .collect(Collectors.toMap(s -> s, this::getSortedWord, (s, s2) -> s));
        } catch (IOException e) {
            throw new FileIOException(e.getMessage());
        }
    }

    private Map<String, List<String>> collectAnagrams(Map<String, String> vocabulary) {
        Map<String, List<String>> anagrams = new HashMap<>();
        vocabulary.forEach((word, sortedWord) -> anagrams.compute(sortedWord, computeAnagramList(word)));
        return anagrams;
    }

    private BiFunction<String, List<String>, List<String>> computeAnagramList(String word) {
        return (key, list) -> {
            List<String> result;
            if (list == null) {
                result = Collections.singletonList(word);
            } else {
                final ArrayList<String> strings = new ArrayList<>(list);
                strings.add(word);
                result = strings;
            }
            return result;
        };
    }

    private List<String> getAnagrams(Map<String, String> vocabulary, Map<String, List<String>> anagrams) {
        return vocabulary.entrySet().stream()
                .map(entry -> anagrams.entrySet().stream()
                        .filter(anagramMap -> anagramMap.getKey().equals(entry.getValue()))
                        .map(Map.Entry::getValue)
                        .flatMap(Collection::stream)
                        .filter(s -> !s.equals(entry.getKey()))
                        .collect(Collectors.joining(ANAGRAM_DELIMITER, entry.getKey() + PREFIX_DELIMITER, SUFFIX))
                )
                .collect(Collectors.toList());
    }

    private String getSortedWord(String word) {
        char[] chars = word.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    private synchronized void saveToFile(String baseFileName, List<String> strings) {
        String outputFileName = addPostfix(baseFileName);
        try {
            FileUtil.checkOutputFile(outputFileName);
            Files.write(Paths.get(outputFileName), (Iterable<String>) strings.stream()::iterator);
            System.out.println();
            log.info("{} {}", FILE_SAVED, outputFileName);
        } catch (IOException e) {
            final String message = e.getMessage();
            if (message != null) {
                throw new FileIOException(message);
            } else {
                throw new FileIOException(WRITE_ERROR + outputFileName);
            }
        }
    }

    private String addPostfix(String base) {
        String result;
        String postfix = POSTFIX_DELIMITER + ANAGRAM_FILE_POSTFIX;
        if (base.contains(EXTENSION_DELIMITER)) {
            result = FilenameUtils.getBaseName(base) + postfix + EXTENSION_DELIMITER + FilenameUtils.getExtension(base);
        } else {
            result = base + postfix;
        }
        return result;
    }

    private void startInformer() {
        final Thread informer = new Thread(() -> {
            System.out.print(ANAGRAMS_SEARCHING);
            System.out.println();
            while (true) {
                try {
                    for (int i = 0; i < 50; i++) {
                        Thread.sleep(MESSAGE_DELAY);
                        System.out.print(INFORM_SYMBOL);
                    }
                    System.out.println();
                } catch (InterruptedException e) {
                    throw new RuntimeException(INFORMER_DEAD);
                }
            }
        });
        informer.setDaemon(true);
        informer.start();
    }
}
