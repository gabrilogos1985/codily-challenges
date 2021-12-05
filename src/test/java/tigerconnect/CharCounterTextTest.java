package tigerconnect;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CharCounterTextTest {


    @Test
    void testCharCounterText() {
        assertEquals("l1", count("l"));
        assertEquals("l1m1", count("lm"));
        assertEquals("l1m1n3t2", count("lmnnntt"));
        assertEquals("l1m2n3t2l4", count("lmmnnnttllll"));
        assertEquals("l5m2n3t2l4", count("lllllmmnnnttllll"));
        assertEquals("l5m2n3t2l4c1", count("lllllmmnnnttllllc"));
        assertEquals("l5 2m2n3 1t2l4 3c1", count("lllll  mmnnn ttllll   c"));
        assertEquals("l5 1m2n3 2t2l4 3c1 1", count("lllll mmnnn  ttllll   c "));
        assertEquals("", count(""));
    }

    private String countStream(final String text) {
        StringBuilder sb = new StringBuilder();
        final StringBuilder current = new StringBuilder();
        final AtomicInteger counter = new AtomicInteger(0);
        text.chars()
                .forEach(c -> {
                    char[] str = {(char) c};
                    if (sb.length() == 0) {
                        current.append(str);
                        sb.append(str);
                    } else {
                        if (current.charAt(0) != c) {
                            current.insert(0, str);
                            sb.append(counter.get()).append(str);
                            counter.getAndSet(0);
                        }
                    }
                    counter.incrementAndGet();
                });
        if (!text.isEmpty()) {
            sb.append(counter.get());
        }
        return sb.toString();
    }


    private String count(final String text) {
        return Pattern.compile("(.)\\1*").matcher(text).results()
                .map(m -> m.group(1) + m.group().length())
                .collect(Collectors.joining());
    }

    private String countUsingFor(final String text) {
        StringBuilder counterText = new StringBuilder();
        int counter = 0;
        for (int index = 0; index < text.length(); index++) {
            char current = text.charAt(index);
            counter++;
            if (index == text.length() - 1 || current != text.charAt(index + 1)) {
                counterText.append(current).append(counter);
                counter = 0;
            }

        }
        return counterText.toString();
    }
}
