/**
 * Simple pair class nothing fancy
 */
public class Pair  {

    public Pair(Object first, Object second) {
        this.first = first;
        this.second = second;
    }

    private Object first;
    private Object second;

    public Object getFirst() {
        return first;
    }

    public void setFirst(Object first) {
        this.first = first;
    }

    public Object getSecond() {
        return second;
    }

    public void setSecond(Object second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "\n{" +
                "first : " + first +
                ", second : " + second +
                '}';
    }
}
