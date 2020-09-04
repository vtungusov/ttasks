package com.siberteam.vtungusov.vocabulary.handler;

import com.siberteam.vtungusov.vocabulary.exception.FileIOException;
import com.siberteam.vtungusov.vocabulary.model.Word;
import com.siberteam.vtungusov.vocabulary.util.FileUtil;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
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
        CompletableFuture.supplyAsync(() -> getVocabulary(outputFileName))
                .thenApply(this::getAnagrams)
                .thenAccept(anagrams -> saveToFile(outputFileName, anagrams))
                .join();
    }

    private CopyOnWriteArrayList<Word> getVocabulary(String outputFileName) {
        try (final Stream<String> lines = Files.lines(Paths.get(outputFileName))) {
            return lines
                    .map(Word::new)
                    .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
        } catch (IOException e) {
            throw new FileIOException(e.getMessage());
        }
    }

    private List<String> getAnagrams(List<Word> vocabulary) {
        return vocabulary.stream()
                .map(getWordAnagram(vocabulary))
                .collect(Collectors.toList());
    }

    private Function<Word, String> getWordAnagram(List<Word> vocabulary) {
        return word -> {
            if (!word.isHandled()) {
                word.setHandled();
                vocabulary.stream()
                        .filter(byWordLength(word))
                        .filter(notHandled())
                        .filter(byThemSelf(word))
                        .forEach(wordAnagram(word));
            }
            final List<Word> anagrams = word.getAnagrams();
            String result = word.getWord();
            if (!anagrams.isEmpty()) {
                result = anagrams.stream()
                        .map(Word::getWord)
                        .collect(Collectors.joining(ANAGRAM_DELIMITER, result + PREFIX_DELIMITER, SUFFIX));
            }
            return result;
        };
    }

    private Consumer<Word> wordAnagram(Word word) {
        return comparableWord -> {
            boolean result;
            result = isAnagram(word, comparableWord);
            if (result) {
                comparableWord.addAnagram(word);
                comparableWord.addAnagram(word.getAnagrams());
                word.getAnagrams()
                        .forEach(anagram -> anagram.addAnagram(comparableWord));
                comparableWord.setHandled();
                word.addAnagram(comparableWord);
            }
        };
    }

    private boolean isAnagram(Word base, Word comparable) {
        Map<Character, Integer> charFrequency = new HashMap<>();
        base.getWord().chars()
                .forEach(charI -> charFrequency.compute((char) charI, (k, v) -> (v == null) ? 1 : v + 1));
        comparable.getWord().chars()
                .forEach(charI -> charFrequency.compute((char) charI, (k, v) -> (v == null) ? 16 : v - 1));
        return charFrequency.values().stream()
                .noneMatch(v -> v > 0);
    }

    private Predicate<Word> byWordLength(Word word) {
        return comparingWord -> comparingWord.getWord().length() == word.getWord().length();
    }

    private Predicate<Word> notHandled() {
        return comparingWord -> !comparingWord.isHandled();
    }

    private Predicate<Word> byThemSelf(Word word) {
        return obj -> !word.equals(obj);
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
            while (true) {
                try {
                    for (int i = 0; i < 50; i++) {
                        Thread.sleep(MESSAGE_DELAY);
                        System.out.println();
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
