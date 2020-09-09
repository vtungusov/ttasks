package com.siberteam.vtungusov.vocabulary.handler;

import com.siberteam.vtungusov.vocabulary.exception.FileIOException;
import com.siberteam.vtungusov.vocabulary.util.FileUtil;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.siberteam.vtungusov.vocabulary.handler.WordsCollector.WRITE_ERROR;

public class Anagramer {
    private static final String ANAGRAM_FILE_POSTFIX = "with_anagrams";
    private static final String EXTENSION_DELIMITER = ".";
    private static final String POSTFIX_DELIMITER = "_";
    private static final String ANAGRAM_DELIMITER = ", ";
    private static final String PREFIX_DELIMITER = ": ";
    private static final String SUFFIX = "";
    private static final String FILE_SAVED = "File was handled and re saved with anagrams as";

    private final Logger log = LoggerFactory.getLogger(Anagramer.class);

    public void findAnagrams(String outputFileName) throws IOException {
        FileUtil.checkInputFile(outputFileName);
        final Map<String, List<String>> anagramMap = collectAnagrams(outputFileName);
        final Stream<String> anagrams = getAnagrams(anagramMap);
        saveToFile(outputFileName, anagrams);
    }

    private Map<String, List<String>> collectAnagrams(String outputFileName) {
        try (final Stream<String> lines = Files.lines(Paths.get(outputFileName))) {
            return lines.collect(Collectors.groupingBy(this::getSortedWord));
        } catch (IOException e) {
            throw new FileIOException(e.getMessage());
        }
    }

    private Stream<String> getAnagrams(Map<String, List<String>> anagrams) {
        return anagrams.values().stream()
                .filter(list -> list.size() > 1)
                .flatMap(list -> list.stream()
                        .map(getAnagramCombination(list))
                );
    }

    private Function<String, String> getAnagramCombination(List<String> list) {
        return anagram ->
                list.stream()
                        .filter(s -> !s.equals(anagram))
                        .collect(Collectors.joining(ANAGRAM_DELIMITER, anagram + PREFIX_DELIMITER, SUFFIX));
    }

    private String getSortedWord(String word) {
        char[] chars = word.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    private synchronized void saveToFile(String baseFileName, Stream<String> strings) {
        String outputFileName = addPostfix(baseFileName);
        try {
            FileUtil.checkOutputFile(outputFileName);
            Files.write(Paths.get(outputFileName), (Iterable<String>) strings::iterator);
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
}
