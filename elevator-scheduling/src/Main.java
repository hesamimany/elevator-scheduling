import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> in = new ArrayList<>();
        in.add(5);
        in.add(12);
        in.add(9);
        List<Integer> out = new ArrayList<>();
        out.add(2);
        out.add(10);
        out.add(8);
        out.add(4);
        Elevator elevator = new Elevator(out, in, 8, 15);

        System.out.println(elevator.state);
        while (true) {
            try {
//                System.out.println(elevator.aboveCurrent);
//                System.out.println(elevator.belowCurrent);
//                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}