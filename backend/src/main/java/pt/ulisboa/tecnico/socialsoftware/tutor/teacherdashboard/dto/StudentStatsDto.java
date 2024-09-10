package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats;

public class StudentStatsDto {
    private Integer id;
    private Integer numStudents;
    private Integer numMore75CorrectQuestions;
    private Integer numAtLeast3Quizzes;

    private String courseExecutionYear;

    public StudentStatsDto() {}

    public StudentStatsDto(StudentStats studentStats) {
        setId(studentStats.getId());
        setNumStudents(studentStats.getNumStudents());
        setNumMore75CorrectQuestions(studentStats.getNumMore75CorrectQuestions());
        setNumAtLeast3Quizzes(studentStats.getNumAtLeast3Quizzes());
        setCourseExecutionYear(studentStats.getCourseExecution().getAcademicTerm());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumStudents() {
        return numStudents;
    }

    public void setNumStudents(Integer numStudents) {
        this.numStudents = numStudents;
    }

    public Integer getMore75CorrectQuestions() {
        return numMore75CorrectQuestions;
    }

    public void setNumMore75CorrectQuestions(Integer numMore75CorrectQuestions) {
        this.numMore75CorrectQuestions = numMore75CorrectQuestions;
    }

    public String getCourseExecutionYear() {
        return courseExecutionYear;
    }

    public void setCourseExecutionYear(String courseExecutionYear) {
        this.courseExecutionYear = courseExecutionYear;
    }

    public Integer getNumAtLeast3Quizzes() {
        return numAtLeast3Quizzes;
    }

    public void setNumAtLeast3Quizzes(Integer numAtLeast3Quizzes) {
        this.numAtLeast3Quizzes = numAtLeast3Quizzes;
    }

    @Override
    public String toString() {
        return "StudentStatsDto{" +
                "id=" + id +
                ", numStudents=" + this.getNumStudents() +
                ", numMore75CorrectQuestions=" + this.getMore75CorrectQuestions() +
                ", numAtLeast3Quizzes=" + this.getNumAtLeast3Quizzes() +
                "}";
    }
}
