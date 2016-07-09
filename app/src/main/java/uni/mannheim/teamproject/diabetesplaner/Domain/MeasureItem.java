package uni.mannheim.teamproject.diabetesplaner.Domain;

/**
 * Created by Ivo on 4/18/2016.
 */
public class MeasureItem {
    public static final String MEASURE_KIND_BLOODSUGAR = "bloodsugar";
    public static final String MEASURE_KIND_INSULIN = "insulin";

    private Long timestamp;
    private double measure_value;
    private String measure_unit;

    public MeasureItem(Long timestamp, double measure_value, String measure_unit){
        this.timestamp=timestamp;
        this.measure_value=measure_value;
        this.measure_unit=measure_unit;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public double getMeasure_value() {
        return measure_value;
    }

    public void setMeasure_value(double measure_value) {
        this.measure_value = measure_value;
    }

    public String getMeasure_unit() {
        return measure_unit;
    }

    public void setMeasure_unit(String measure_unit) {
        this.measure_unit = measure_unit;
    }
}
