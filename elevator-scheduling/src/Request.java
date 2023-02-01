public class Request implements Comparable {
    int floor;
    int age = 0;

    public Request(int floor) {
        this.floor = floor;
    }

    @Override
    public String toString() {
        return "["+floor+", "+age+"]";
    }

    @Override
    public int compareTo(Object o) {
        return this.floor - ((Request) o).floor;
    }
}
