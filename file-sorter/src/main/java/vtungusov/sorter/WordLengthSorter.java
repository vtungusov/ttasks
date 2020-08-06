package vtungusov.sorter;

import vtungusov.comparator.WordLengthComparator;

import java.util.Comparator;
import java.util.stream.Stream;

public class WordLengthSorter implements Sorter {
    private final Comparator<String> comparator = new WordLengthComparator();

    @Override
    public Stream<String> sort(Stream<String> wordStream) {
        return wordStream
                .distinct()
                .sorted(comparator);
    }
}
