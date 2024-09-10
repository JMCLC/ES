package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats;

import java.io.Serializable;

public class QuestionStatsDto implements Serializable {

    private Integer id;

    private int numAvailable;

    private int answeredQuestionUnique;

    private float averageQuestionsAnswered;

    private String courseExecutionYear;

    public QuestionStatsDto() {
    }

    public QuestionStatsDto(QuestionStats questionStats) {
        setId(questionStats.getId());
        setNumAvailable(questionStats.getNumAvailable());
        setAnsweredQuestionUnique(questionStats.getAnsweredQuestionUnique());
        setAverageQuestionsAnswered(questionStats.getAverageQuestionsAnswered());
        setCourseExecutionYear(questionStats.getCourseExecution().getAcademicTerm());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNumAvailable() {
        return numAvailable;
    }

    public void setNumAvailable(int numAvailable) {
        this.numAvailable = numAvailable;
    }

    public int getAnsweredQuestionUnique() {
        return answeredQuestionUnique;
    }

    public void setAnsweredQuestionUnique(int answeredQuestionUnique) {
        this.answeredQuestionUnique = answeredQuestionUnique;
    }

    public float getAverageQuestionsAnswered() {
        return averageQuestionsAnswered;
    }

    public void setAverageQuestionsAnswered(float averageQuestionsAnswered) {
        this.averageQuestionsAnswered = averageQuestionsAnswered;
    }

    public String getCourseExecutionYear() {
        return courseExecutionYear;
    }

    public void setCourseExecutionYear(String courseExecutionYear) {
        this.courseExecutionYear = courseExecutionYear;
    }

    @Override
    public String toString() {
        return "QuestionStatsDto{" +
                "id=" + id +
                ", numAvailable=" + this.getNumAvailable() +
                ", answeredQuestionUnique=" + this.getAnsweredQuestionUnique() +
                ", averageQuestionsAnswered=" + this.getAverageQuestionsAnswered() +
                '}';
    }
}
