package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats;

import java.io.Serializable;

public class QuizStatsDto implements Serializable {

    private Integer id;

    private int numQuizzes;

    private int uniqueQuizzesSolved;

    private float averageQuizzesSolved;

    private String courseExecutionYear;

    public QuizStatsDto() {
    }

    public QuizStatsDto(QuizStats quizStats) {
        setId(quizStats.getId());
        setNumQuizzes(quizStats.getNumQuizzes());
        setUniqueQuizzesSolved(quizStats.getUniqueQuizzesSolved());
        setAverageQuizzesSolved(quizStats.getAverageQuizzesSolved());
        setCourseExecutionYear(quizStats.getCourseExecution().getAcademicTerm());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNumQuizzes() {
        return numQuizzes;
    }

    public void setNumQuizzes(int numQuizzes) {
        this.numQuizzes = numQuizzes;
    }

    public int getUniqueQuizzesSolved() {
        return uniqueQuizzesSolved;
    }

    public void setUniqueQuizzesSolved(int uniqueQuizzesSolved) {
        this.uniqueQuizzesSolved = uniqueQuizzesSolved;
    }

    public float getAverageQuizzesSolved() {
        return averageQuizzesSolved;
    }

    public void setAverageQuizzesSolved(float averageQuizzesSolved) {
        this.averageQuizzesSolved = averageQuizzesSolved;
    }

    public String getCourseExecutionYear() {
        return courseExecutionYear;
    }

    public void setCourseExecutionYear(String courseExecutionYear) {
        this.courseExecutionYear = courseExecutionYear;
    }

    @Override
    public String toString() {
        return "QuizStatsDto{" +
                "id=" + id +
                ", numQuizzes=" + this.getNumQuizzes() +
                ", uniqueQuizzesSolved=" + this.getUniqueQuizzesSolved() +
                ", averageQuizzesSolved=" + this.getAverageQuizzesSolved() +
                "}";
    }

}
