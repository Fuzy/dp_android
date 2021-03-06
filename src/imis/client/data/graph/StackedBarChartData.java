package imis.client.data.graph;

import java.util.List;

/**
 * Class storing data for use in stacked bar chart.
 */
public class StackedBarChartData {
    private long minDay;
    private long maxDay;
    private double yMax;
    private String topLabel;
    private String leftLabel;
    private String bottomLabel;
    private List<double[]> values;
    private String[] titles;
    private int[] colors;

    public StackedBarChartData() {
        minDay = Long.MAX_VALUE;
        maxDay = Long.MIN_VALUE;
        yMax = Double.MIN_VALUE;
    }

    public long getMinDay() {
        return minDay;
    }

    public void setMinDay(long minDay) {
        this.minDay = minDay;
    }

    public long getMaxDay() {
        return maxDay;
    }

    public void setMaxDay(long maxDay) {
        this.maxDay = maxDay;
    }

    public String getTopLabel() {
        return topLabel;
    }

    public void setTopLabel(String topLabel) {
        this.topLabel = topLabel;
    }

    public String getLeftLabel() {
        return leftLabel;
    }

    public void setLeftLabel(String leftLabel) {
        this.leftLabel = leftLabel;
    }

    public String getBottomLabel() {
        return bottomLabel;
    }

    public void setBottomLabel(String bottomLabel) {
        this.bottomLabel = bottomLabel;
    }

    public List<double[]> getValues() {
        return values;
    }

    public void setValues(List<double[]> values) {
        this.values = values;
    }

    public String[] getTitles() {
        return titles;
    }

    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    public int[] getColors() {
        return colors;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public double getyMax() {
        return yMax;
    }

    public void setyMax(double yMax) {
        this.yMax = yMax;
    }

}
