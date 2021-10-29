import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LessIntegetTest {



    @Test
    public void baseTest() {
            Assertions.assertEquals(calculte(new int[]{1, 3, 6, 4, 1, 2}), 5);
    }

    private int calculte(final int[] ints) {
        var integers = new TreeSet<Integer>();
        Stream.of(ints).collect(Collectors.toSet());
        int i = integers.last() - 1;
        return i;
    }
}
