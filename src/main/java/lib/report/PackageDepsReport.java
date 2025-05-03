package lib.report;

import java.util.ArrayList;
import java.util.List;
/**
 * A report class for storing dependencies of a Java package.
 * This class contains the package name and a list of class dependency reports associated with it.
 * It provides methods to add class reports, retrieve them, and generate a string representation of the report.
 */
public class PackageDepsReport {
    private final String packageName;
    private final List<ClassDepsReport> classReports = new ArrayList<>();
    /**
     * Constructs a new PackageDepsReport for the specified package.
     *
     * @param packageName the name of the package being analyzed
     */
    public PackageDepsReport(String packageName) {
        this.packageName = packageName;
    }
    /**
     * Adds a class report to the package report.
     *
     * @param report the ClassDepsReport object to add
     */
    public void addClassReport(ClassDepsReport report) {
        classReports.add(report);
    }
    /**
     * Retrieves the list of class reports associated with the package.
     *
     * @return a list of ClassDepsReport objects representing the class reports
     */
    public List<ClassDepsReport> getClassReports() {
        return classReports;
    }
    /**
     * Generates a string representation of the package dependencies report.
     * The output includes the package name and a list of its class reports.
     *
     * @return a formatted string representation of the report
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Package: ").append(packageName).append("\n");
        sb.append("Class Reports:\n");
        for (ClassDepsReport report : classReports) {
            sb.append(report).append("\n");
        }
        return sb.toString();
    }
}