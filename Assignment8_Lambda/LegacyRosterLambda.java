// LegacyRoster.java

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

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
        List<Student> result = new ArrayList<Student>();
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            if (s != null) {
                if (s.getGpa() >= minGpa && s.getCredits() >= minCredits) {
                    result.add(s);
                }
            }
        }
// Manual sort using anonymous Comparator
        Collections.sort(result, new Comparator<Student>() {
            public int compare(Student a, Student b) {
                int lastCmp = a.getLastName().compareToIgnoreCase(b.getLastName());
                if (lastCmp != 0) {
                    return lastCmp;
                }
                return a.getFirstName().compareToIgnoreCase(b.getFirstName());
            }
        });
        return result;
    }

    /**
     * Return emails of students in a given major, lowercased and sorted
     * alphabetically.
     */
    public static List<String> getEmailsByMajor(List<Student> students, String major) {
        List<String> emails = new ArrayList<String>();
        for (Student s : students) {
            if (s != null && s.getMajor() != null && s.getEmail() != null) {
                if (s.getMajor().equalsIgnoreCase(major)) {
                    emails.add(s.getEmail().toLowerCase());
                }
            }
        }
        Collections.sort(emails); // natural String order
        return emails;
    }

    /**
     * Return top N students by GPA (descending). Ties broken by credits
     * (descending).
     */
    public static List<Student> getTopNByGpa(List<Student> students, int n) {
        List<Student> copy = new ArrayList<Student>();
        for (Student s : students) {
            if (s != null) {
                copy.add(s);
            }
        }
        Collections.sort(copy, new Comparator<Student>() {
            public int compare(Student a, Student b) {
                if (b.getGpa() > a.getGpa()) {
                    return 1;
                }
                if (b.getGpa() < a.getGpa()) {
                    return -1;
                }
// tie-breaker by credits
                return b.getCredits() - a.getCredits();
            }
        });
        List<Student> top = new ArrayList<Student>();
        int limit = Math.min(n, copy.size());
        for (int i = 0; i < limit; i++) {
            top.add(copy.get(i));
        }
        return top;
    }

    /**
     * Compute average GPA across all students; returns 0.0 if list is empty.
     */
    public static double getAverageGpa(List<Student> students) {
        if (students == null || students.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        int count = 0;
        for (Student s : students) {
            if (s != null) {
                total += s.getGpa();
                count++;
            }
        }
        if (count == 0) {
            return 0.0;
        }
        return total / count;
    }

    /**
     * Find first student with matching ID (case-sensitive). Returns null if not
     * found.
     */
    public static Student findById(List<Student> students, String id) {
        if (id == null) {
            return null;
        }
        for (Student s : students) {
            if (s != null && s.getId() != null) {
                if (s.getId().equals(id)) {
                    return s;
                }
            }
        }
        return null;
    }

    /**
     * Return a sorted list of distinct course titles across all students.
     */
    public static List<String> getDistinctCourseTitles(List<Student> students) {
        HashSet<String> seen = new HashSet<String>();
        for (Student s : students) {
            if (s != null && s.getCourses() != null) {
                List<Course> cs = s.getCourses();
                for (int i = 0; i < cs.size(); i++) {
                    Course c = cs.get(i);
                    if (c != null && c.getTitle() != null) {
                        if (!seen.contains(c.getTitle())) {
                            seen.add(c.getTitle());
                        }
                    }
                }
            }
        }
        List<String> titles = new ArrayList<String>(seen);
        Collections.sort(titles, new Comparator<String>() {
            public int compare(String a, String b) {
                return a.compareToIgnoreCase(b);
            }
        });
        return titles;
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
        List<Student> honor = getHonorRoll(roster, 3.5, 60);
        System.out.println("Honor Roll:");
        for (Student s : honor) {
            System.out.println(" - " + s);
        }
// Emails by major (lowercased, sorted)
        List<String> csEmails = getEmailsByMajor(roster, "CS");
        System.out.println("\nCS Emails (sorted):");
        for (String e : csEmails) {
            System.out.println(" - " + e);
        }
// Top 3 by GPA
        List<Student> top3 = getTopNByGpa(roster, 3);
        System.out.println("\nTop 3 by GPA:");
        for (Student s : top3) {
            System.out.println(" - " + s);
        }
// Average GPA
        double avg = getAverageGpa(roster);
        System.out.println("\nAverage GPA: " + String.format("%.3f", avg));
// Find by ID
        Student found = findById(roster, "A003");
        System.out.println("\nfindById('A003'): " + (found == null ? "not found"
                : found.toString()));
// Distinct course titles across roster
        List<String> titles = getDistinctCourseTitles(roster);
        System.out.println("\nDistinct course titles (sorted, case-insensitive):");
        for (String t : titles) {
            System.out.println(" - " + t);
        }
    }
}
