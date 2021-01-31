package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.FlinkJob;
import com.mycompany.myapp.web.rest.vm.VaccineStatsVM;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CovidResource {

    @GetMapping("/covid")
    public ResponseEntity<List<VaccineStatsVM>> getVaccineStats() throws Exception {
        return ResponseEntity.ok(FlinkJob.run());
    }

}
