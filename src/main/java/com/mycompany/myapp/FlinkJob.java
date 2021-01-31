package com.mycompany.myapp;

import com.mycompany.myapp.domain.State;
import com.mycompany.myapp.domain.StatsByStatePojo;
import com.mycompany.myapp.domain.VaccinePojo;
import com.mycompany.myapp.web.rest.vm.VaccineStatsVM;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FilterOperator;
import org.apache.flink.api.java.tuple.Tuple2;

import java.util.List;

public class FlinkJob {

    public static List<VaccineStatsVM> run() throws Exception {
        // Connect to remote Flink and provide the Jar to be shipped
        ExecutionEnvironment env = ExecutionEnvironment.createRemoteEnvironment("localhost", 8081, "target/jhipster-0.0.1-SNAPSHOT.jar.original");

        // Read Moderna data
        DataSource<VaccinePojo> moderna = env
            .readCsvFile("s3a://tlebrun-playground/csv/COVID-19_Vaccine_Moderna.csv")
            .ignoreFirstLine()
            .parseQuotedStrings('"')
            .includeFields("1000001") // Fields "Jurisdiction" and "Doses allocated for distribution week of 01/04"
            .pojoType(VaccinePojo.class, "state", "allocatedDosesModerna");

        // Read Pfizer data
        DataSource<VaccinePojo> pfizer = env
            .readCsvFile("s3a://tlebrun-playground/csv/COVID-19_Vaccine_Pfizer.csv")
            .ignoreFirstLine()
            .parseQuotedStrings('"')
            .includeFields("100000001") // Fields "Jurisdiction" and "Doses allocated for distribution week of 01/04"
            .pojoType(VaccinePojo.class, "state", "allocatedDosesPfizer");

        // Read stats by state data
        DataSource<StatsByStatePojo> statsByStates = env
            .readCsvFile("s3a://tlebrun-playground/csv/COVID-19_Cases_and_Deaths_by_State.csv")
            .ignoreFirstLine()
            .parseQuotedStrings('"')
            .includeFields("11100001")
            .pojoType(StatsByStatePojo.class, "date", "state", "cases", "death");

        // Filter stats on 01/04/2021
        FilterOperator<StatsByStatePojo> dayStatsByStates = statsByStates
            .filter(statsByState -> "01/04/2021".equals(statsByState.getDate()));

        // Join the vaccine data and cases using state as key
        // Sort by number of death
        return moderna
            .join(pfizer)
            .where("state")
            .equalTo("state")
            .map(new MapToVaccineFunction())
            .join(dayStatsByStates)
            .where(new IdSelectorStateCode())
            .equalTo("state")
            .map(new MapToCovidFunction())
            .sortPartition(new IdSelectorDeath(), Order.DESCENDING)
            .collect();
    }

    private static class MapToVaccineFunction implements MapFunction<Tuple2<VaccinePojo, VaccinePojo>, VaccinePojo> {
        @Override
        public VaccinePojo map(Tuple2<VaccinePojo, VaccinePojo> value) {
            VaccinePojo pojo = new VaccinePojo();
            pojo.setState(value.f0.getState());
            pojo.setAllocatedDosesModerna(value.f0.getAllocatedDosesModerna());
            pojo.setAllocatedDosesPfizer(value.f1.getAllocatedDosesPfizer());
            return pojo;
        }
    }

    private static class MapToCovidFunction implements MapFunction<Tuple2<VaccinePojo, StatsByStatePojo>, VaccineStatsVM> {
        @Override
        public VaccineStatsVM map(Tuple2<VaccinePojo, StatsByStatePojo> value) {
            return new VaccineStatsVM()
                .moderna(value.f0.getAllocatedDosesModerna())
                .pfizer(value.f0.getAllocatedDosesPfizer())
                .state(value.f1.getState())
                .cases(value.f1.getCases())
                .death(value.f1.getDeath());
        }
    }

    private static class IdSelectorStateCode implements KeySelector<VaccinePojo, String> {
        @Override
        public String getKey(VaccinePojo value) {
            return State.valueOfName(value.getState()).getStateCode();
        }
    }

    private static class IdSelectorDeath implements KeySelector<VaccineStatsVM, Long> {
        @Override
        public Long getKey(VaccineStatsVM value) {
            return value.getDeath();
        }
    }
}
