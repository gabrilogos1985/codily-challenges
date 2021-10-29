import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FuelproviderTest {


    public static final int TIME_WAIT_PER_LITER = 1;

    @Test
    public void testEscenarios() {
        // assertEquals(calculateWaitingTime(new int[]{1}, 1,1,1), 1);
        assertEquals(calculateWaitingTime(IntStream.range(0, 100_000)
                .map(v -> 1).toArray(), 1_000_000_000, 1_000_000_000, 1_000_000_000), 33334);
        assertEquals(calculateWaitingTime(new int[]{1_000_000_000, 1_000_000_000, 1_000_000_000}, 1_000_000_000, 1_000_000_000, 1_000_000_000), 0);
        assertEquals(calculateWaitingTime(new int[]{5}, 4, 0, 3), -1);
        assertEquals(calculateWaitingTime(new int[]{2, 8, 4, 3, 2}, 7, 11, 3), 8);
        assertEquals(calculateWaitingTime(new int[]{1, 2}, 3, 0, 0), 1);
        assertEquals(calculateWaitingTime(new int[]{1, 2, 1}, 3, 1, 0), 1);
    }

    private int calculateWaitingTime(final int[] carsArray,
                                     final int fuelX, final int fuelY, final int fuelZ) {

        LinkedList<FuelDispenser> dispensers = buildDispensers(fuelX, fuelY, fuelZ);
        LinkedList<Integer> carsQueue = buildCarsQueue(carsArray);
        int timeElaplsed = 0;
        while (!carsQueue.isEmpty()) {
            for (int i = 0; i < carsQueue.size(); i++) {
                if (isDispensersAvaible(dispensers, timeElaplsed)) {
                    break;
                }
                dispenseForCar(dispensers, carsQueue, timeElaplsed);
            }
            timeElaplsed++;
        }

        return dispensers.stream().map(FuelDispenser::getFinalWaitingTime)
                .mapToInt(v -> v)
                .max()
                .orElse(-1);
    }

    private LinkedList<Integer> buildCarsQueue(final int[] carsArray) {
        var carsQueue = Arrays.stream(carsArray)
                .boxed()
                .collect(Collectors.toCollection(LinkedList::new));
        return carsQueue;
    }

    private LinkedList<FuelDispenser> buildDispensers(final int fuelX, final int fuelY, final int fuelZ) {
        var dispenserX = new FuelDispenser(fuelX);
        var dispenserY = new FuelDispenser(fuelY);
        var dispenserZ = new FuelDispenser(fuelZ);

        var dispensers = new LinkedList<FuelDispenser>();
        dispensers.add(dispenserX);
        dispensers.add(dispenserY);
        dispensers.add(dispenserZ);
        return dispensers;
    }

    private boolean isDispensersAvaible(final LinkedList<FuelDispenser> dispensers, final int timeElaplsed) {
        var currentTime = timeElaplsed;
        boolean isDispensersAvaible = !dispensers.stream().filter(d -> d.isAvailable(currentTime)).findFirst().isPresent();
        return isDispensersAvaible;
    }

    private void dispenseForCar(final LinkedList<FuelDispenser> dispensers, final LinkedList<Integer> carsQueue, final int timeElaplsed) {
        var carFuel = carsQueue.peek();
        boolean avaibleFuelForCar = false;
        for (var dispenser : dispensers) {
            if (dispenser.getFuel() >= carFuel) {
                avaibleFuelForCar = true;
            }

            if (!dispenser.isAvailable(timeElaplsed)) {
                continue;
            }
            if (dispenser.dispenseFuel(carFuel)) {
                carsQueue.remove(carFuel);
                break;
            }
        }

        if (!avaibleFuelForCar) {
            carsQueue.remove(carFuel);
        }
    }


    /**
     * No thread safe code.
     **/
    class FuelDispenser {
        private int fuel;
        private int fillingTime = 0;
        private int lastWatingTime = 0;

        FuelDispenser(int fuel) {
            this.fuel = fuel;
        }

        public boolean dispenseFuel(int requestedFuel) {
            if (requestedFuel > this.fuel) {
                return false;
            }
            this.fuel -= requestedFuel;
            this.lastWatingTime = requestedFuel;
            this.fillingTime += requestedFuel * TIME_WAIT_PER_LITER;
            return true;
        }

        public int getFuel() {
            return fuel;
        }

        public boolean isAvailable(final int timeElaplsed) {
            return fillingTime == 0 || fillingTime == timeElaplsed;
        }

        public boolean isEmpty() {
            return fuel == 0;
        }

        public int getFinalWaitingTime() {
            return fillingTime > 0 ? this.fillingTime - this.lastWatingTime : -1;
        }

        public boolean isNotEmpty() {
            return !isEmpty();
        }
    }
}
