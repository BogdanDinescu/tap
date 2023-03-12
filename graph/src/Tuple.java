import java.util.Objects;

public class Tuple {
    private Integer n;
    private Integer g;
    private Integer f;

    public Tuple(Integer n, Integer g, Integer f) {
        this.n = n;
        this.g = g;
        this.f = f;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public Integer getG() {
        return g;
    }

    public void setG(Integer g) {
        this.g = g;
    }

    public Integer getF() {
        return f;
    }

    public void setF(Integer f) {
        this.f = f;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple tuple = (Tuple) o;
        return Objects.equals(n, tuple.n) && Objects.equals(g, tuple.g) && Objects.equals(f, tuple.f);
    }

    @Override
    public int hashCode() {
        return Objects.hash(n, g, f);
    }

    @Override
    public String toString() {
        return "{" + n.toString() + " " + g.toString() + " " + f.toString() + "}";
    }
}
