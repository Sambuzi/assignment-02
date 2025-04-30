package lib.report;

import java.util.ArrayList;
import java.util.List;

public class PackageDepsReport {
    private final String packageName;
    private final List<ClassDepsReport> classReports = new ArrayList<>();

    public PackageDepsReport(String packageName) {
        this.packageName = packageName;
    }

    public void addClassReport(ClassDepsReport report) {
        classReports.add(report);
    }

    public List<ClassDepsReport> getClassReports() {
        return classReports;
    }

    @Override
    public String toString() {
        return "Package: " + packageName + ", Class Reports: " + classReports;
    }
}