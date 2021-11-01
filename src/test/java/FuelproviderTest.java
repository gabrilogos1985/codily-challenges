import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FuelproviderTest {


    public static final int TIME_WAIT_PER_LITER = 1;
    private static final int NOT_ENOUGH_FUEL_RESPONSE = -1;

    private int headIndex = 0;

    @Test
    public void testEscenarios() {

        assertEquals(8, calculateWaitingTime(new int[]{2, 8, 4, 3, 2}, 7, 11, 3));
        assertEquals(5, calculateWaitingTime(new int[]{2, 3, 5, 5, 3, 2}, 4, 6, 10));
        assertEquals(-1, calculateWaitingTime(new int[]{2, 3, 5, 7, 7, 5, 3, 2}, 1, 3, 5));

        assertEquals(0, calculateWaitingTime(new int[]{1}, 1,1,1));
//        assertEquals(33333, calculateWaitingTime(IntStream.range(0, 100_000)
//                .map(v -> 1).toArray(), 1_000_000_000, 1_000_000_000, 1_000_000_000));
        //assertEquals(-1, calculateWaitingTime(IntStream.range(0, 100_000).toArray(), 1_000_000_000, 1_000_000_000, 1_000_000_000));
       //assertEquals(-1, calculateWaitingTime(IntStream.range(0, 77469).toArray(), 1_000_000_000, 1_000_000_000, 1_000_000_000));
       assertEquals(-1, calculateWaitingTime(IntStream.range(0, 77458).toArray(), 1_000_000_000, 1_000_000_000, 1_000_000_000));
        assertEquals(0, calculateWaitingTime(new int[]{1_000_000_000, 1_000_000_000, 1_000_000_000}, 1_000_000_000, 1_000_000_000, 1_000_000_000));
        assertEquals(NOT_ENOUGH_FUEL_RESPONSE, calculateWaitingTime( new int[]{5}, 4, 0, 3));

        assertEquals(1, calculateWaitingTime(new int[]{1, 2}, 3, 0, 0));
        assertEquals(1, calculateWaitingTime(new int[]{1, 2, 1}, 3, 1, 0));
    }

    private int calculateWaitingTime(final int[] carsArray,
                                     final int fuelX, final int fuelY, final int fuelZ) {

        LinkedList<FuelDispenser> dispensers = buildDispensers(fuelX, fuelY, fuelZ);
        LinkedList<Integer> carsQueue = buildCarsQueue(carsArray);
        int timeElaplsed = 0;
        try {
        while (!carsQueue.isEmpty()) {
            int queueSize = carsQueue.size();
            for (int i = 0; i < queueSize; i++) {
                if (isDispensersAvaible(dispensers, timeElaplsed) && i == 0) {
                    break;
                }
                dispenseForCar(dispensers, carsQueue, timeElaplsed, i);
            }
            timeElaplsed++;
        }

        return dispensers.stream().map(FuelDispenser::getFinalWaitingTime)
                .mapToInt(v -> v)
                .max()
                .orElse(NOT_ENOUGH_FUEL_RESPONSE);
        } catch (NotEnoughFuelException e) {
            return  NOT_ENOUGH_FUEL_RESPONSE;
        }
    }

    private boolean isDispensersAvaible(final LinkedList<FuelDispenser> dispensers, final int timeElaplsed) {
        var currentTime = timeElaplsed;
        boolean isDispensersAvaible = !dispensers.stream().filter(d -> d.isAvailable(currentTime)).findFirst().isPresent();
        return isDispensersAvaible;
    }


    private void dispenseForCar(final LinkedList<FuelDispenser> dispensers, final LinkedList<Integer> carsQueue,
                                final int timeElaplsed, int index) {
        var carFuel = carsQueue.poll();
        //var carFuel = carsQueue. get(headIndex + index);
        boolean avaibleFuelForCar = false;
        boolean isDispatched = false;
        for (var dispenser : dispensers) {
            if (dispenser.hasEnoughFuel(carFuel)) {
                avaibleFuelForCar = true;
                if (!dispenser.isAvailable(timeElaplsed)) {
                    continue;
                }
                if (dispenser.dispenseFuel(carFuel)) {
                    isDispatched = true;
                    //carsQueue.remove(carFuel);
                    break;
                }
            }
        }
        if(!isDispatched){
            carsQueue.add(carFuel);
        }
        if (!avaibleFuelForCar) {
            throw new NotEnoughFuelException("No fue posible dispensar el combustible para el carro " + carFuel);
            // carsQueue.remove(carFuel);
        }
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
            if (!this.hasEnoughFuel(requestedFuel)) {
                return false;
            }
            this.fuel -= requestedFuel;
            this.lastWatingTime = requestedFuel;
           // System.out.printf("Dispense %sliters at time%s\n", requestedFuel,fillingTime);
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

        public boolean hasEnoughFuel(final Integer carFuel) {
            return this.fuel >= carFuel;
        }
    }

    private class NotEnoughFuelException extends RuntimeException {
        public NotEnoughFuelException(final String message) {
        }
    }


    @Data
    class Node {
        private int value;
        private Node next;
    }
}
