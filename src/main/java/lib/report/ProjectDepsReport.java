package lib.report;

import java.util.ArrayList;
import java.util.List;

public class ProjectDepsReport {
    private final String projectName;
    private final List<PackageDepsReport> packageReports = new ArrayList<>();

    public ProjectDepsReport(String projectName) {
        this.projectName = projectName;
    }

    public void addPackageReport(PackageDepsReport report) {
        packageReports.add(report);
    }

    public List<PackageDepsReport> getPackageReports() {
        return packageReports;
    }

    @Override
    public String toString() {
        return "Project: " + projectName + ", Package Reports: " + packageReports;
    }
}