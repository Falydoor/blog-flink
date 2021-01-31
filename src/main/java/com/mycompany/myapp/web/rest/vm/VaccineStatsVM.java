package com.mycompany.myapp.web.rest.vm;

public class VaccineStatsVM {
    private String state;
    private String moderna;
    private String pfizer;
    private Long cases;
    private Long death;

    public String getState() {
        return state;
    }

    public VaccineStatsVM state(String state) {
        this.state = state;
        return this;
    }

    public String getModerna() {
        return moderna;
    }

    public VaccineStatsVM moderna(String moderna) {
        this.moderna = moderna;
        return this;
    }

    public String getPfizer() {
        return pfizer;
    }

    public VaccineStatsVM pfizer(String pfizer) {
        this.pfizer = pfizer;
        return this;
    }

    public Long getCases() {
        return cases;
    }

    public VaccineStatsVM cases(Long cases) {
        this.cases = cases;
        return this;
    }

    public Long getDeath() {
        return death;
    }

    public VaccineStatsVM death(Long death) {
        this.death = death;
        return this;
    }

    @Override
    public String toString() {
        return "CovidVM{" +
            "state='" + state + '\'' +
            ", moderna='" + moderna + '\'' +
            ", pfizer='" + pfizer + '\'' +
            ", cases=" + cases +
            ", death=" + death +
            '}';
    }
}
