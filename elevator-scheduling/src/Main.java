import java.util.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ArrayList<Request> requests = new ArrayList<>();
        requests.add(new Request(2));
        requests.add(new Request(5));
        requests.add(new Request(0));
        requests.add(new Request(15));

        int[] out = {14};
        int[] in = {1,2};
        Elevator elevator = new Elevator(3, 15, out, in);
        Thread.sleep(5000);
        while (true){
            int num = 0;
            elevator.addRequest("out "+num);
            Thread.sleep(2000);
        }
    }
}