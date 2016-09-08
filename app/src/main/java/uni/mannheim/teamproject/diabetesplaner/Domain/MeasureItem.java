package uni.mannheim.teamproject.diabetesplaner.Domain;

import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

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

    /**
     * returns the blood sugar level in mmol
     * @return
     * @author Stefan 08.09.2016
     */
    public double getMeasureValueInMol(){
        return Util.convertBSToMol(measure_value, measure_unit);
    }

    /**
     * compares two MeasureItems
     * @param o
     * @return
     * @author Stefan 08.09.2016
     */
    @Override
    public boolean equals(Object o) {
        MeasureItem mi = (MeasureItem) o;
        return this.getMeasureValueInMol() == mi.getMeasureValueInMol() && this.getTimestamp().equals(mi.getTimestamp());
    }
}
