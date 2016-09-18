package uni.mannheim.teamproject.diabetesplaner.Domain;

import uni.mannheim.teamproject.diabetesplaner.Utility.Util;

/**
 * Created by Ivo on 4/18/2016.
 */
public class MeasureItem {
    public static final String MEASURE_KIND_BLOODSUGAR = "bloodsugar";
    public static final String MEASURE_KIND_INSULIN = "insulin";

    public static final String UNIT_PERCENT = "%";
    public static final String UNIT_MMOL = "mmol/l";
    public static final String UNIT_MG = "mg/dl";

    private Long timestamp;
    private double measure_value;
    private String measure_unit;


    private String measure_kind;

    public MeasureItem(Long timestamp, double measure_value, String measure_unit, String measure_kind) {
        this.timestamp = timestamp;
        this.measure_value = measure_value;
        this.measure_unit = measure_unit;
        this.measure_kind = measure_kind;
    }

    public MeasureItem(Long timestamp, double measure_value, String measure_unit) {
        this.timestamp = timestamp;
        this.measure_value = measure_value;
        this.measure_unit = measure_unit;
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

    public String getMeasure_kind() {
        return this.measure_kind;
    }

    /**
     * returns the blood sugar level in mmol
     *
     * @return
     * @author Stefan 08.09.2016
     */
    public double getMeasureValueInMol() {
        return Util.convertBSToMol(measure_value, measure_unit);
    }

    /**
     * returns the blood sugar level in mg/dl
     *
     * @return
     * @author Stefan 08.09.2016
     */
    public double getMeasureValueInMG() {
        return Util.convertBSToMG(measure_value, measure_unit);
    }

    /**
     * compares two MeasureItems
     *
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
