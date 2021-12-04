package codily.stage1;

import lombok.Data;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FuelproviderTest {


    public static final int TIME_WAIT_PER_LITER = 1;
    private static final int NOT_ENOUGH_FUEL_RESPONSE = -1;

    private Node head = null;

    @Test
    public void testEscenarios() {

        assertEquals(8, calculateWaitingTime(new int[]{2, 8, 4, 3, 2}, 7, 11, 3));
        assertEquals(5, calculateWaitingTime(new int[]{2, 3, 5, 5, 3, 2}, 4, 6, 10));
        assertEquals(-1, calculateWaitingTime(new int[]{2, 3, 5, 7, 7, 5, 3, 2}, 1, 3, 5));

        assertEquals(0, calculateWaitingTime(new int[]{1}, 1, 1, 1));
        assertEquals(33333, calculateWaitingTime(IntStream.range(0, 100_000)
                .map(v -> 1).toArray(), 1_000_000_000, 1_000_000_000, 1_000_000_000));
        assertEquals(-1, calculateWaitingTime(IntStream.range(0, 100_000).toArray(), 1_000_000_000, 1_000_000_000, 1_000_000_000));
        assertEquals(999918232, calculateWaitingTime(IntStream.range(0, 77459).toArray(), 1_000_000_000, 1_000_000_000, 1_000_000_000));
        assertEquals(-1, calculateWaitingTime(IntStream.range(0, 77460).toArray(), 1_000_000_000, 1_000_000_000, 1_000_000_000));
        assertEquals(0, calculateWaitingTime(new int[]{1_000_000_000, 1_000_000_000, 1_000_000_000}, 1_000_000_000, 1_000_000_000, 1_000_000_000));
        assertEquals(NOT_ENOUGH_FUEL_RESPONSE, calculateWaitingTime(new int[]{5}, 4, 0, 3));

        assertEquals(1, calculateWaitingTime(new int[]{1, 2}, 3, 0, 0));
        assertEquals(1, calculateWaitingTime(new int[]{1, 2, 1}, 3, 1, 0));
    }

    private int calculateWaitingTime(final int[] carsArray,
                                     final int fuelX, final int fuelY, final int fuelZ) {
        var startTime = System.currentTimeMillis();
        LinkedList<Pump> dispensers = buildDispensers(fuelX, fuelY, fuelZ);
        buildCarsQueue(carsArray);
        int timeElaplsed = 0;
        try {
            while (head != null) {
                Node car = head;
                do {
                    if (arePumpsAvailable(dispensers, timeElaplsed)) {
                        dispenseCar(dispensers, car, timeElaplsed);
                    } else break;
                } while((car = car.getNext()) != null);
                timeElaplsed++;
            }
            return timeElaplsed - 1;
        } catch (NotEnoughFuelException e) {
            return NOT_ENOUGH_FUEL_RESPONSE;
        } finally {
            Duration duration = Duration.of(System.currentTimeMillis() - startTime, ChronoUnit.MILLIS);
            System.out.println("Duracion " + duration.toMinutesPart() + " minutos" + " " + duration.toSecondsPart() + " segundos "
                    +  duration.toMillisPart() + " milisegundos");
        }
    }

    private boolean arePumpsAvailable(final LinkedList<Pump> dispensers, final int timeElaplsed) {
        return dispensers.stream().anyMatch(d -> d.isAvailable(timeElaplsed));
    }


    private void dispenseCar(final LinkedList<Pump> dispensers, Node car, final int timeElaplsed) {
        var carFuel = car.getFuel();
        boolean avaibleFuelForCar = false;
        for (var dispenser : dispensers) {
            if (dispenser.hasEnoughFuel(carFuel)) {
                avaibleFuelForCar = true;
                if (!dispenser.isAvailable(timeElaplsed)) {
                    continue;
                }
                if (dispenser.dispenseFuel(carFuel)) {
                    Node next = car.getNext();
                    Node previous = car.getPrevious();
                    if (this.head == car) {
                        this.head = next;
                    } else {
                        previous.setNext(next);
                    }

                    if (next != null) {
                        next.setPrevious(previous);
                    }
                    break;
                }
            }
        }
        if (!avaibleFuelForCar) {
            throw new NotEnoughFuelException("No fue posible dispensar el combustible para el carro " + carFuel);
        }
    }

    private void buildCarsQueue(final int[] carsArray) {
        this.head = null;
        Node previousNodeBuilt = null;
        for (int idx = carsArray.length - 1; idx >= 0; idx--) {
            var node = new Node(carsArray[idx], previousNodeBuilt);
            if (previousNodeBuilt != null) {
                previousNodeBuilt.previous = node;
            }
            previousNodeBuilt = node;
        }
        head = previousNodeBuilt;
    }

    private LinkedList<Pump> buildDispensers(final int fuelX, final int fuelY, final int fuelZ) {
        var dispenserX = new Pump(fuelX);
        var dispenserY = new Pump(fuelY);
        var dispenserZ = new Pump(fuelZ);

        var dispensers = new LinkedList<Pump>();
        dispensers.add(dispenserX);
        dispensers.add(dispenserY);
        dispensers.add(dispenserZ);
        return dispensers;
    }

    /**
     * No thread safe code.
     **/
    static class Pump {
        private int fuel;
        private int fillingTime = 0;

        Pump(int fuel) {
            this.fuel = fuel;
        }

        public boolean dispenseFuel(int requestedFuel) {
            if (!this.hasEnoughFuel(requestedFuel)) {
                return false;
            }
            this.fuel -= requestedFuel;
            this.fillingTime += requestedFuel * TIME_WAIT_PER_LITER;
            return true;
        }

        public boolean isAvailable(final int timeElaplsed) {
            return fillingTime == 0 || fillingTime == timeElaplsed;
        }

        public boolean hasEnoughFuel(final Integer carFuel) {
            return this.fuel >= carFuel;
        }
    }

    private static class NotEnoughFuelException extends RuntimeException {
        public NotEnoughFuelException(final String message) {
            super(message);
        }
    }


    @Data
    class Node {
        public Node previous;
        private int value;
        private Node next;

        public Node(final int carGas, final Node previousNodeBuilt) {
            this.value = carGas;
            this.next = previousNodeBuilt;
        }

        public Integer getFuel() {
            return value;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "previous=" + (previous != null ? previous.getFuel() : null) +
                    ", value=" + value +
                    ", next=" + (next != null ? next.getFuel() : null) +
                    '}';
        }
    }
}
