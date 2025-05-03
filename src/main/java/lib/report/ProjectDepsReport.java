package lib.report;

import java.util.ArrayList;
import java.util.List;
/**
 * A report class for storing dependencies of a Java project.
 * This class contains the project name and a list of package dependency reports associated with it.
 * It provides methods to add package reports, retrieve them, and generate a string representation of the report.
 */
public class ProjectDepsReport {
    private final String projectName;
    private final List<PackageDepsReport> packageReports = new ArrayList<>();
    /**
     * Constructs a new ProjectDepsReport for the specified project.
     *
     * @param projectName the name of the project being analyzed
     */
    public ProjectDepsReport(String projectName) {
        this.projectName = projectName;
    }
    /**
     * Adds a package report to the project report.
     *
     * @param report the PackageDepsReport object to add
     */
    public void addPackageReport(PackageDepsReport report) {
        packageReports.add(report);
    }
    /**
     * Retrieves the list of package reports associated with the project.
     *
     * @return a list of PackageDepsReport objects representing the package reports
     */
    public List<PackageDepsReport> getPackageReports() {
        return packageReports;
    }
    /**
     * Generates a string representation of the project dependencies report.
     * The output includes the project name and a list of its package reports.
     *
     * @return a formatted string representation of the report
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Project: ").append(projectName).append("\n");
        sb.append("Package Reports:\n");
        for (PackageDepsReport report : packageReports) {
            sb.append(report).append("\n");
        }
        return sb.toString();
    }
}