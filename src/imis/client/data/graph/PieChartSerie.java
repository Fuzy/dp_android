package imis.client.data.graph;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 10.4.13
 * Time: 17:45
 */
public class PieChartSerie {
    private String label;
    private Double amount;
    private String time;
    private int percent;
    private int color;

    public PieChartSerie(String label, Double amount) {
        this.label = label;
        this.amount = amount;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    @Override
    public String toString() {
        return "PieChartSerie{" +
                "label='" + label + '\'' +
                ", amount=" + amount +
                ", time='" + time + '\'' +
                ", percent=" + percent +
                ", color=" + color +
                '}';
    }
}
