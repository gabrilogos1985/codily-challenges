package codily.stage1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class MeanAritmeticTest {

        static int lastfirstnumber = 0;
    @Test
    public void baseTest() {
        int[] numbers = {1, 2, 3}; //1,3 = 2,  1,2,3 = 2, 2 = 2,
        int s = 2;
       // Assertions.assertEquals(3, countArithmeticMeanOccurrences(numbers, s));
      //  Assertions.assertEquals(3, countArithmeticMeanOccurrences(new int[]{5,3,6,2}, 4));
    //    Assertions.assertEquals(51, countArithmeticMeanOccurrences(IntStream.range(1, 10).toArray(), 5));
    //    Assertions.assertEquals(117, countArithmeticMeanOccurrences(IntStream.range(1, 50).toArray(), 5));
        //Assertions.assertEquals(117, countArithmeticMeanOccurrences(IntStream.range(1, 20).map(v-> new Random().nextInt(8)).toArray(), 5));
        Assertions.assertEquals(117, countArithmeticMeanOccurrences(IntStream.range(1, 10_050).toArray(), 500));
    }

    private int countArithmeticMeanOccurrences(final int[] numbers, final int s) {
        AtomicInteger allArithNumbers = new AtomicInteger(0);
        //for (int i = 0; i < numbers.length; i++) {
          //  LinkedList<Integer> baseNumbers = new LinkedList<>(Arrays.asList(numbers[i]));
            //countIfArithNumber(baseNumbers, allArithNumbers, s);
            //calculateRecursive(baseNumbers, getTail(numbers, i + 1), allArithNumbers, s);
        Arrays.sort(numbers);
            calculateRecursive(new LinkedList<>(), numbers, allArithNumbers, s);
        //}
        return allArithNumbers.get();
    }

    private void calculateRecursive(List<Integer> baseNumbers, int[] others, AtomicInteger allArithNumbers, int expected) {
        if(!baseNumbers.isEmpty() && lastfirstnumber != baseNumbers.get(0)) {
            lastfirstnumber = baseNumbers.get(0);
            System.out.println("LAST NUMBER: " + lastfirstnumber);
        }
        Integer reduce = baseNumbers.stream().reduce(0, (a, b) -> a + b);
        if(reduce %100 == 0 && !baseNumbers.isEmpty()) System.out.println( "Sum " + reduce + " " + baseNumbers.size() + " " + baseNumbers.get(0));
        if (others.length < 1 || calculateArithMean(baseNumbers) > expected) {
            return;
        }
        for (int i = 0; i < others.length; i++) {
            var currentNumbers = new LinkedList<>(baseNumbers);
            currentNumbers.add(others[i]);
            countIfArithNumber(currentNumbers, allArithNumbers, expected);
            //System.out.println("Recursive: " + currentNumbers);

            if(i < others.length - 1) {
                var accumulatorList = new LinkedList<>(baseNumbers);
                accumulatorList.add(others[i]);
             calculateRecursive(accumulatorList, getTail(others, i + 1), allArithNumbers, expected);
            }
        }
    }

    private int[] getTail(final int[] others, int fromIndex) {
        if(others.length > fromIndex) {
            //TODO: improve copy
            return Arrays.copyOfRange(others, fromIndex, others.length);
        }
        return new int[0];
    }

    private void countIfArithNumber(final List<Integer> numbers, final AtomicInteger allArithNumbers, double expected) {
        double arithMean = calculateArithMean(numbers);
        boolean comparisom = arithMean == expected;
        if(arithMean %50 == 0) System.out.println("Mean " + arithMean);
        if (comparisom) {
            System.out.printf("Combinacion %s, %s == %s => %s \n", numbers, arithMean, expected, comparisom);
            allArithNumbers.incrementAndGet();
        }
    }

    private double calculateArithMean(List<Integer> numbers) {
        double sum = numbers.stream().reduce(0, (a, b) -> a + b);
        return sum / numbers.size();
    }
}


