import java.util.*;

public class Elevator extends Thread {
    final int maxFloor; // How many floors
    int changeDir; // Minimum floor diff for changing direction
    ArrayList<Request> outRequest; // list for outside requests
    ArrayList<Request> inRequest; // list for inside requests
    int currentFloor; // Current floor

    boolean ageFlag = false; // becomes true when we have an aged request
    Request highAge; // keeps the aged request

    TakeInput input = new TakeInput(); // Thread for getting input from commandline
    Move state; // State of the elevator [STOP, UP, DOWN]

    Move lastMove;

    static long TRAVELING_TIME = 5000;

    public Elevator(int currentFloor, int maxFloor, int[] out, int[] in) {
        this.currentFloor = currentFloor;
        this.maxFloor = maxFloor;
        changeDir = maxFloor / 2;

        outRequest = new ArrayList<>();
        inRequest = new ArrayList<>();

        for (int i : in) {
            inRequest.add(new Request(i)); // Add initial values
        }
        for (int i : out) {
            outRequest.add(new Request(i)); // Add initial values
        }
        state = Move.STOP; // Elevator state is STOP

        Collections.sort(inRequest); // sorting inside requests
        Collections.sort(outRequest); // Sorting outside requests
    }

    public void action() { // Starts elevator
        this.start();
        input.start();
    }

    public void moveDown() {
        currentFloor--;

        try {
            Thread.sleep(TRAVELING_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moveUp() {
        currentFloor++;

        try {
            Thread.sleep(TRAVELING_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void move() {
        if (state == Move.UP) moveUp();
        else moveDown();
        lastMove = state;
        state = Move.STOP;
    }

    public void addAge() {
        for (Request r : inRequest) {
            r.age += 2;
        }
        for (Request r : outRequest) {
            r.age++;
        }
    }

    public Request highAge(Request currentGoal) {
        Request age = currentGoal;
        for (Request r : inRequest) {
            if (r.age >= age.age && r.age >= maxFloor) {
                age = r;
                ageFlag = true;
                highAge = r;
            }
        }
        for (Request r : outRequest) {
            if (r.age >= age.age && r.age >= maxFloor) {
                age = r;
                ageFlag = true;
                highAge = r;
            }
        }
        if (age.age >= maxFloor) return age;
        return currentGoal;
    }

    public void addRequest(String req) {
        String[] args = req.split(" ");
        Request r = new Request(Integer.parseInt(args[1]));
        for (Request request : inRequest) {
            if (request.floor == r.floor) return;
        }
        for (Request request : outRequest) {
            if (request.floor == r.floor) return;
        }
        if (args[0].equalsIgnoreCase("in")) {
            inRequest.add(r);
            Collections.sort(inRequest);
        } else if (args[0].equalsIgnoreCase("out")) {
            outRequest.add(r);
            Collections.sort(outRequest);
        }
    }

    @Override
    public void run() {
        while (true) {
            long startTime = System.currentTimeMillis();
            Request finalRequest = new Request(currentFloor);
            if (!ageFlag) {
                int dis = Integer.MAX_VALUE;
                for (Request request : outRequest) {
                    if (Math.abs(request.floor - currentFloor) <= dis) {
                        finalRequest = request;
                        dis = Math.abs(request.floor - currentFloor);
                    }
                }
                for (Request request : inRequest) {
                    if (Math.abs(request.floor - currentFloor) <= dis) {
                        Request temp = finalRequest;
                        finalRequest = request;
                        if ((finalRequest.floor - currentFloor < 0 && lastMove == Move.UP) || (finalRequest.floor - currentFloor > 0 && lastMove == Move.DOWN)) {
                            finalRequest = temp;
                            continue;
                        }
                        dis = Math.abs(request.floor - currentFloor);
                    }
                }
                finalRequest = highAge(finalRequest);
            } else {
                finalRequest = highAge;
            }
            System.out.println("out: " + outRequest + "\n" + " in: " + inRequest);
            System.out.println("Current floor: " + currentFloor + "   Selected floor: " + finalRequest);


            if (state == Move.STOP) {
                if (currentFloor - finalRequest.floor >= 0) {
                    state = Move.DOWN;
                } else state = Move.UP;
            } else {
                if (Math.abs(finalRequest.floor - currentFloor) >= changeDir && !ageFlag) {
                    if (state == Move.DOWN) state = Move.UP;
                    else state = Move.DOWN;
                }
            }
            if (!(inRequest.isEmpty() && outRequest.isEmpty())) move();
            else {
                try {
                    state = Move.STOP;
                    Thread.sleep(TRAVELING_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            addAge();
            if (highAge != null && currentFloor == highAge.floor) {
                ageFlag = false;
            }
            inRequest.removeIf(r -> r.floor == currentFloor);
            outRequest.removeIf(r -> r.floor == currentFloor);
            long endTime = System.currentTimeMillis();
            try {
                Thread.sleep(TRAVELING_TIME + 100 - (endTime - startTime));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class TakeInput extends Thread {
        Scanner input = new Scanner(System.in);

        @Override
        public void run() {
            while (true) addRequest(input.nextLine()); // format is "out/in {request}"
        }
    }

    public enum Move {
        UP, DOWN, STOP
    }
}
