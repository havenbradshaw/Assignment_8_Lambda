// LegacyRoster.java

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class LegacyRosterLambda {
// --- Simple domain model ---

    static class Course {

        private final String code; // e.g., "CSC-101"
        private final String title; // e.g., "Intro to CS"

        public Course(String code, String title) {
            this.code = code;
            this.title = title;
        }

        public String getCode() {
            return code;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public String toString() {
            return code + " - " + title;
        }
    }

    static class Student {

        private final String id;
        private final String firstName;
        private final String lastName;
        private final String major; // e.g., "CS", "SE", "IT"
        private final double gpa; // 0.0 - 4.0
        private final int credits; // accumulated credits
        private final String email;
        private final List<Course> courses; // enrolled courses

        public Student(String id, String firstName, String lastName,
                String major, double gpa, int credits, String email,
                List<Course> courses) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.major = major;
            this.gpa = gpa;
            this.credits = credits;
            this.email = email;
// Intentionally store a *mutable* list to highlight refactor goals
            this.courses = new ArrayList<Course>(courses);
        }

        public String getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getMajor() {
            return major;
        }

        public double getGpa() {
            return gpa;
        }

        public int getCredits() {
            return credits;
        }

        public String getEmail() {
            return email;
        }

        public List<Course> getCourses() {
            return courses;
        }

        @Override
        public String toString() {
            return String.format("%s %s (ID:%s, %s) GPA: %.2f, Credits: %d",
                    firstName, lastName, id, major, gpa, credits);
        }
    }
// --- Legacy utility methods students will refactor ---

    /**
     * Return students with GPA >= minGpa AND credits >= minCredits, sorted by
     * last, then first name.
     */
        public static List<Student> getHonorRoll(List<Student> students, double minGpa,
        int minCredits) {
            return students.stream()
            .filter(student -> student.gpa >= minGpa && student.credits >= minCredits)
            .sorted(Comparator.comparing((Student s) -> s.getLastName(), String.CASE_INSENSITIVE_ORDER)
            .thenComparing(s -> s.getFirstName(), String.CASE_INSENSITIVE_ORDER))
            .collect(Collectors.toList());
        }

    /**
     * Return emails of students in a given major, lowercased and sorted
     * alphabetically.
     */
    public static List<String> getEmailsByMajor(List<Student> students, String major) {
       return students.stream()
       .filter(student -> student.major.equals(major))
       .map(student -> student.getEmail().toLowerCase())
       .sorted()
       .collect(Collectors.toList());
    }

    /**
     * Return top N students by GPA (descending). Ties broken by credits
     * (descending).
     */
    public static List<Student> getTopNByGpa(List<Student> students, int n) {
        return students.stream()
        .filter(student -> student.gpa > 0)
        .sorted(Comparator.comparingDouble(Student :: getGpa)
        .thenComparing(Student :: getCredits)
        .reversed())
        .limit(n)
        .collect(Collectors.toList());
    }
// tie-breaker by credits
    /**
     * Compute average GPA across all students; returns 0.0 if list is empty.
     */
    public static double getAverageGpa(List<Student> students) {
        return students.stream()
        .mapToDouble(Student :: getGpa)
        .average()
        .orElse(0.0);
    }

    /**
     * Find first student with matching ID (case-sensitive). Returns null if not
     * found.
     */
    public static Student findById(List<Student> students, String id) {
        return students.stream()
       .filter(student -> student != null && student.getId() != null)
       .filter(student -> student.getId().equals(id))
       .findFirst()
       .orElse(null);
    }
    
    /**
     * Return a sorted list of distinct course titles across all students.
     */
    public static List<String> getDistinctCourseTitles(List<Student> students) {
        return students.stream()
        .filter(student -> student != null && student.getCourses() != null)
        .flatMap(student -> student.getCourses().stream())
        .filter(course -> course != null && course.getTitle() != null)
        .map(Course :: getTitle)
        .distinct()
        .sorted().collect(Collectors.toList());
    }
// --- Demo data and output ---

    public static void main(String[] args) {
// Sample courses
        Course csc101 = new Course("CSC-101", "Intro to CS");
        Course csc225 = new Course("CSC-225", "Data Structures");
        Course csc372 = new Course("CSC-372", "Generative AI");
        Course mat250 = new Course("MAT-250", "Discrete Math");
        Course se310 = new Course("SE-310", "Software Engineering");
// Sample students
        List<Student> roster = new ArrayList<Student>();
        List<Course> aCourses = new ArrayList<Course>();
        aCourses.add(csc101);
        aCourses.add(csc225);
        aCourses.add(mat250);
        List<Course> bCourses = new ArrayList<Course>();
        bCourses.add(csc225);
        bCourses.add(se310);
        List<Course> cCourses = new ArrayList<Course>();
        cCourses.add(csc101);
        cCourses.add(csc372);
        cCourses.add(se310);
        cCourses.add(csc225);
        roster.add(new Student("A001", "Emma", "Rowe", "CS", 3.82, 78,
                "emma.rowe@univ.edu", aCourses));
        roster.add(new Student("A002", "Noah", "Turner", "SE", 3.10, 45,
                "noah.turner@univ.edu", bCourses));
        roster.add(new Student("A003", "Xander", "Wood", "CS", 3.95, 90,
                "xander.wood@univ.edu", cCourses));
        roster.add(new Student("A004", "Ethan", "Sexton", "IT", 2.75, 30,
                "ethan.sexton@univ.edu", aCourses));
        roster.add(new Student("A005", "Jolie", "Barger", "CS", 3.35, 60,
                "jolie.barger@univ.edu", bCourses));
        roster.add(new Student("A006", "Sravani", "Kadiyala", "SE", 3.55, 72,
                "sravani.k@univ.edu", cCourses));

// Honor roll (GPA >= 3.5 and credits >= 60), sorted by last/first
        System.out.println("Honor Roll:");
        getHonorRoll(roster, 3.5, 60).forEach(s -> System.out.println(" - " + s));
// Emails by major (lowercased, sorted)
        System.out.println("\nCS Emails (sorted):");
        getEmailsByMajor(roster, "CS").forEach(e -> System.out.println(" - " + e));
// Top 3 by GPA
        System.out.println("\nTop 3 by GPA:");
        getTopNByGpa(roster, 3).forEach(s -> System.out.println(" - " + s));
// Average GPA
        double avg = getAverageGpa(roster);
        System.out.println("\nAverage GPA: " + String.format("%.3f", avg));
// Find by ID
        Student found = findById(roster, "A003");
        System.out.println("\nfindById('A003'): " + (found == null ? "not found" : found.toString()));
// Distinct course titles across roster
        System.out.println("\nDistinct course titles (sorted, case-insensitive):");
        getDistinctCourseTitles(roster).forEach(t -> System.out.println(" - " + t));
    }
}
