package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;

import java.util.ArrayList;
import java.util.List;

public class TeacherDashboardDto {
    private Integer id;
    private Integer numberOfStudents;

    private List<QuestionStatsDto> dashboardQuestionStats;

    private List<QuizStatsDto> dashboardQuizStats;
    
    private List<StudentStatsDto> dashboardStudentStats;

    public TeacherDashboardDto() {
    }

    public TeacherDashboardDto(TeacherDashboard teacherDashboard) {
        this.id = teacherDashboard.getId();
        // For the number of students, we consider only active students
        this.numberOfStudents = teacherDashboard.getCourseExecution().getNumberOfActiveStudents();
        this.dashboardQuestionStats = new ArrayList<>();
        this.dashboardQuizStats = new ArrayList<>();
        this.dashboardStudentStats = new ArrayList<>();
        teacherDashboard.getQuestion().stream().forEach(questionStats -> dashboardQuestionStats.add(new QuestionStatsDto(questionStats)));
        teacherDashboard.getStudentStats().stream().forEach(studentStats -> dashboardStudentStats.add(new StudentStatsDto(studentStats)));
        teacherDashboard.getQuizStats().stream().forEach(quizStats -> dashboardQuizStats.add(new QuizStatsDto(quizStats)));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(Integer numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public List<QuestionStatsDto> getDashboardQuestionStats() {
        return dashboardQuestionStats;
    }

    public void setDashboardQuestionStats(List<QuestionStatsDto> dashboardQuestionStats) {
        this.dashboardQuestionStats = dashboardQuestionStats;
    }

    public List<QuizStatsDto> getDashboardQuizStats() {
        return dashboardQuizStats;
    }

    public void setDashboardQuizStats(List<QuizStatsDto> dashboardQuizStats) {
        this.dashboardQuizStats = dashboardQuizStats;
    }

    public List<StudentStatsDto> getDashboardStudentStats() {
        return dashboardStudentStats;
    }

    public void setDashboardStudentStats(List<StudentStatsDto> dashboardStudentStats) {
        this.dashboardStudentStats = dashboardStudentStats;
    }

    @Override
    public String toString() {
        return "TeacherDashboardDto{" +
                "id=" + id +
                ", numberOfStudents=" + this.getNumberOfStudents() +
                "}";
    }
}
