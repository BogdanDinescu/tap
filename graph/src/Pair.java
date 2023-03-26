import java.util.Objects;

public class Pair implements Comparable {
    private Integer a;
    private Integer b;

    public Pair(Integer a, Integer b) {
        this.a = a;
        this.b = b;
    }

    public Integer getA() {
        return a;
    }

    public void setA(Integer a) {
        this.a = a;
    }

    public Integer getB() {
        return b;
    }

    public void setB(Integer b) {
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return a.equals(pair.a) && b.equals(pair.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }

    @Override
    public String toString() {
        return "{" + a.toString() + " " + b.toString() + "}";
    }

    @Override
    public int compareTo(Object o) {
        Pair p = (Pair) o;
        return p.b - this.b;
    }
}
