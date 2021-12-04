package codily.stage1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class BinarytTreeTest {


    @Test
    public void baseTest() {
//        int[] numbers = {1, 2, 3}; //1,3 = 2,  1,2,3 = 2, 2 = 2,
//        int s = 2;
//        Assertions.assertEquals(3, countArithmeticMeanOccurrences(numbers, s));
//        Assertions.assertEquals(3, countArithmeticMeanOccurrences(new int[]{5,3,6,2}, 4));
        Assertions.assertEquals(51, countArithmeticMeanOccurrences(IntStream.range(1, 10).toArray(), 5));
//        Assertions.assertEquals(117, countArithmeticMeanOccurrences(IntStream.range(1, 50).toArray(), 5));
//       Assertions.assertEquals(117, countArithmeticMeanOccurrences(IntStream.range(1, 20).map(v-> new Random().nextInt(8)).toArray(), 5));
//        Assertions.assertEquals(117, countArithmeticMeanOccurrences(IntStream.range(1, 10_050).toArray(), 10000));
        //countArithmeticMeanOccurrences(IntStream.range(1, 100_050).toArray(), 100_000);
    }

    private int countArithmeticMeanOccurrences(final int[] arr, final int s) {

        int r =  arr.length;
        int n = arr.length;



//        AtomicInteger allArithNumbers = new AtomicInteger(0);
        //for (int i = 0; i < numbers.length; i++) {
          //  LinkedList<Integer> baseNumbers = new LinkedList<>(Arrays.asList(numbers[i]));
            //countIfArithNumber(baseNumbers, allArithNumbers, s);
            //calculateRecursive(baseNumbers, getTail(numbers, i + 1), allArithNumbers, s);
//        Arrays.sort(numbers);
//            calculateRecursive(new LinkedList<>(), numbers, allArithNumbers, s);
        //}
        return returnSubsequenceCount(arr,s);
    }


    public static int returnSubsequenceCount(int[] X, int S) {
        int counter = 0;

        for (int i = 0; i < X.length; i++) {
            int[] dpSum = new int[X.length];
            Map<Integer, List<Integer>> dpNums = new HashMap<>();

            dpSum[i] = X[i];
            dpNums.put(i, new LinkedList<>(Arrays.asList(X[i])) );

            if (X[i] == S) {
                System.out.println("Solution:  " + X[i]);
                counter++;
            }

            for (int j = i + 1; j < X.length; j++) {
                int sum = dpSum[j - 1] + X[j];

                List<Integer> integers = dpNums.get(j-1);
                integers = integers == null ? new LinkedList<>() : integers;
                integers.add(X[j]);
                dpNums.put(j, integers);

                dpSum[j] = sum;

                if ((double) sum / (j - i + 1) == S) {
                    System.out.println("Solution:  " + integers);
                    counter++;
                }
            }
        }
        return counter;
    }
}


