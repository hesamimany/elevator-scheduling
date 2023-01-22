import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Elevator extends Thread {
    final int maxFloor;
    List<Integer> outRequest;
    List<Integer> inRequest;
    int currentFloor;
    List<Integer> aboveCurrent;
    List<Integer> belowCurrent;
    TakeInput input = new TakeInput();
    Move state;


    public Elevator(List<Integer> outRequest, List<Integer> inRequest, int currentFloor, int maxFloor) {
        this.outRequest = outRequest;
        this.inRequest = inRequest;
        this.currentFloor = currentFloor;
        this.maxFloor = maxFloor;

        if (currentFloor <= maxFloor / 2) state = Move.DOWN;
        else state = Move.UP;

        aboveCurrent = Collections.synchronizedList(new ArrayList<>());
        belowCurrent = Collections.synchronizedList(new ArrayList<>());

        for (int i : outRequest) {
            if (i >= currentFloor) aboveCurrent.add(i);
            else belowCurrent.add(i);
        }
        for (int i : inRequest) {
            if (i >= currentFloor) aboveCurrent.add(i);
            else belowCurrent.add(i);
        }
        Collections.sort(aboveCurrent);
        Collections.sort(belowCurrent);
        input.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.start();
    }

    public void moveDown() {
        currentFloor--;
        if (currentFloor == 0 || belowCurrent.isEmpty()) state = Move.UP;
        System.out.format("Floor: %d, Moving: %s\n", currentFloor, state);
        System.out.println(aboveCurrent);
        System.out.println(belowCurrent);
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moveUp() {
        currentFloor++;
        if (currentFloor == maxFloor - 1 || aboveCurrent.isEmpty()) state = Move.DOWN;
        System.out.format("Floor: %d, Moving: %s\n", currentFloor, state);
        System.out.println(aboveCurrent);
        System.out.println(belowCurrent);
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addRequest(int floor) {
        if (floor >= currentFloor && !aboveCurrent.contains(floor)) {
            aboveCurrent.add(floor);
            Collections.sort(aboveCurrent);
        } else if (floor < currentFloor && !belowCurrent.contains(floor)) {
            belowCurrent.add(floor);
            Collections.sort(belowCurrent);
        }


    }

    @Override
    public void run() {
        while (true) {
            if (state == Move.DOWN) {
                while (!belowCurrent.isEmpty()) {
                    if (currentFloor == belowCurrent.get(belowCurrent.size() - 1))
                        belowCurrent.remove(belowCurrent.size() - 1);
                    moveDown();
                }
            }
            else if (state == Move.UP) {
                while (!aboveCurrent.isEmpty()) {
                    if (currentFloor == aboveCurrent.get(0))
                        aboveCurrent.remove(0);
                    moveUp();
                }
            }
        }
    }

    private class TakeInput extends Thread {
        Scanner input = new Scanner(System.in);

        @Override
        public void run() {
            while (true) addRequest(input.nextInt());
        }
    }

    public enum Move {
        UP, DOWN;
    }
}
