package ru.student.qa;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public final class MetricsUpdater {
    private MetricsUpdater() {}
    public static void main(String[] args) throws Exception {
        Path csv=Path.of("build/reports/jacoco/test/jacocoTestReport.csv");
        if(!Files.exists(csv)){System.out.println("JaCoCo CSV not found: "+csv); return;}
        double missed=0, covered=0;
        try(BufferedReader r=Files.newBufferedReader(csv)){
            r.readLine(); String line;
            while((line=r.readLine())!=null){ String[] p=line.split(",",-1); if(p.length<6) continue; missed+=Double.parseDouble(p[3]); covered+=Double.parseDouble(p[4]); }
        }
        double percent=(covered+missed)==0?0:covered/(covered+missed)*100.0;
        ResultDatabase.initialize(); ResultDatabase.updateCoverage(percent);
        System.out.printf("Coverage saved to DB: %.2f%%%n",percent);
    }
}
