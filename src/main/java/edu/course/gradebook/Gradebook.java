package edu.course.gradebook;

import java.util.*;

public class Gradebook {

    private final Map<String, List<Integer>> gradesByStudent = new HashMap<>();
    private final Deque<UndoAction> undoStack = new ArrayDeque<>();
    private final LinkedList<String> activityLog = new LinkedList<>();


    public Optional<List<Integer>> findStudentGrades(String name) {

        return Optional.ofNullable(gradesByStudent.get(name));
    }

    public boolean addStudent(String name) {
        if (gradesByStudent.containsKey(name)) {
            System.out.println("We already have this student in our existing records");
            return false;
        }
        gradesByStudent.put(name, new ArrayList<>());
        System.out.println("Student " + name + " has been added");
        ArrayList<Integer> students = new ArrayList<>();
        for (int index = 0; index < gradesByStudent.get(name).size(); index++) {
            students.add(index);
        }
        return true;
    }

    public boolean addGrade(String name, int grade) {
        if (!gradesByStudent.containsKey(name)) {
            System.out.println("Student not found in our existing records");
            return false;
        }

        ArrayList<Integer> studentgrades = new ArrayList<>(gradesByStudent.get(name));
        studentgrades.add(grade);
        gradesByStudent.put(name, studentgrades);
        System.out.println("Student " + name + " has added the grade " + grade);
        undoStack.push(() -> gradesByStudent.get(name).remove((Integer) grade));
        return true;
    }

    public boolean removeStudent(String name) {
        if (!gradesByStudent.containsKey(name)) {
            System.out.println("Student not found in our existing records");
            return false;
        }
        List<Integer> oldGrades = new ArrayList<>(gradesByStudent.get(name));
        undoStack.push(() -> gradesByStudent.put(name, oldGrades));
        gradesByStudent.remove(name);
        System.out.println("Student " + name + " has been removed from our existing records");
        return true;
    }

    public Optional<Double> averageFor(String name) {
        List<Integer> studentGrades = new ArrayList<>(gradesByStudent.get(name));

        if (studentGrades.isEmpty()) {
            System.out.println("Student has no grades in our existing records");
            return Optional.empty();
        }
        int total = 0;
        for (int index : studentGrades) {
            total += index;
        }
        double average = (double) (total / studentGrades.size());
        return Optional.of(average);
    }

    public Optional<String> letterGradeFor(String name) {
        Optional<Double> average = averageFor(name);
        if (average.isEmpty()) {
            System.out.println("Student has no grades in our existing records");
            return Optional.empty();
        }
        double gotAverage = average.get();
        int switchAverage = (int) gotAverage;

        String letterGrade = switch (switchAverage / 10) {
            case 10, 9 -> "A";
            case 8 -> "B";
            case 7 -> "C";
            case 6 -> "D";
            default-> "F";
        };
        return Optional.of(letterGrade);
    }

    public Optional<Double> classAverage() {
        double classAverage = 0;
        int ctr = 0;

        for(String student : gradesByStudent.keySet()) {
            for (int grade : gradesByStudent.get(student)) {
                classAverage += grade;
                ctr++;
            }
        }

        if(ctr ==0) {
            return Optional.empty();
        }

          double average = classAverage / ctr;
        return Optional.of(average);
    }

    public boolean undo() {
        if (undoStack.isEmpty())
            return false;
        undoStack.pop().undo();
        System.out.println("Undo has been successfully undone");
        return true;
    }

    public List<String> recentLog(int maxItems) {
        maxItems = Math.min(maxItems, activityLog.size());
        return activityLog.subList(activityLog.size()-maxItems, activityLog.size());
    }

    /**added this interface because I was unclear on the lambdas, I had to do some extra reading on my own and according to what I read this was the fix needed*/
    public interface UndoAction {
        void undo();
    }
}
