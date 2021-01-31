package com.mycompany.myapp.domain;

public class VaccinePojo {
    private String state;
    private String allocatedDosesModerna;
    private String allocatedDosesPfizer;

    public VaccinePojo() {
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAllocatedDosesModerna() {
        return allocatedDosesModerna;
    }

    public void setAllocatedDosesModerna(String allocatedDosesModerna) {
        this.allocatedDosesModerna = allocatedDosesModerna;
    }

    public String getAllocatedDosesPfizer() {
        return allocatedDosesPfizer;
    }

    public void setAllocatedDosesPfizer(String allocatedDosesPfizer) {
        this.allocatedDosesPfizer = allocatedDosesPfizer;
    }
}
