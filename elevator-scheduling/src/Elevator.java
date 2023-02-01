import java.util.*;

public class Elevator extends Thread {
    final int maxFloor;
    final int changeDir;
    List<Request> outRequest;
    List<Request> inRequest;
    int currentFloor;

    boolean ageFlag = false;
    Request highAge;

    TakeInput input = new TakeInput();
    Move state;


    public Elevator(int currentFloor, int maxFloor, int[] out, int[] in) {
        this.currentFloor = currentFloor;
        this.maxFloor = maxFloor;
        changeDir = maxFloor / 2;
        outRequest = new ArrayList<>();
        inRequest = new ArrayList<>();
        for (int i : in) {
            inRequest.add(new Request(i));
        }
        for (int i : out) {
            outRequest.add(new Request(i));
        }
        state = Move.STOP;

        Collections.sort(inRequest);
        Collections.sort(outRequest);
        this.start();
        input.start();
    }

    public void moveDown() {
        currentFloor--;

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moveUp() {
        currentFloor++;

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void move() {
        if (state == Move.UP) moveUp();
        else moveDown();
        state = Move.STOP;
    }

    public void addAge() {
        for (Request r : inRequest) {
            r.age++;
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
                        finalRequest = request;
                        dis = Math.abs(request.floor - currentFloor);
                    }
                }
                finalRequest = highAge(finalRequest);
                System.out.println("out: " + outRequest + "\n" + " in: " + inRequest);
                System.out.println("Current floor: " + currentFloor + "   Selected floor: " + finalRequest);
            } else {
                finalRequest = highAge;
                System.out.println("out: " + outRequest + "\n" + " in: " + inRequest);
                System.out.println("Current floor: " + currentFloor + "   Selected floor: " + finalRequest);
            }


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
                    Thread.sleep(5000);
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
