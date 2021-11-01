import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MeanAritmeticTest {


    @Test
    public void baseTest() {

        int[] numbers = {1, 2, 3, 4, 5};
        int s = 3;
        AtomicInteger allArithNumbers = new AtomicInteger(0);
        for (int i = 0; i < numbers.length; i++) {
            countIfArithNumber(List.of(numbers[i]), allArithNumbers, s);
        }
        allArithNumbers.get();
    }

    private void calculateRecursive(int number, int[] others, AtomicInteger allArithNumbers, int expected) {
        if (others.length > 0) {
            return;
        }
        for (int i = 0; i < others.length; i++) {
            countIfArithNumber(List.of(number, others[i]), allArithNumbers, expected);
            //TODO: improve copy
            calculateRecursive(others[i], Arrays.copyOfRange(others, 1, others.length), allArithNumbers,expected);
        }
    }

    private void countIfArithNumber(final List<Integer> numbers, final AtomicInteger allArithNumbers, int expected) {
        if (calculateArithMean(numbers) == expected) {
            allArithNumbers.incrementAndGet();
        }
    }

    private int calculateArithMean(List<Integer> numbers) {
        var sum = numbers.stream().reduce(0, (a, b) -> a + b);
        return sum / numbers.size();
    }
}


