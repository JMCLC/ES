package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;

import javax.persistence.*;

@Entity
public class StudentStats implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private TeacherDashboard teacherDashboard;

    @OneToOne
    private CourseExecution courseExecution;

    private Integer numStudents = 0;

    private Integer numMore75CorrectQuestions = 0;

    private Integer numAtLeast3Quizzes = 0;

    public StudentStats() {}

    public StudentStats(TeacherDashboard teacherDashboard) {
        setTeacherDashboard(teacherDashboard);
        setCourseExecution(teacherDashboard.getCourseExecution());
    }

    public StudentStats(TeacherDashboard teacherDashboard, CourseExecution courseExecution) {
        this.teacherDashboard = teacherDashboard;
        this.courseExecution = courseExecution;
    }
    
    public void remove() {
        courseExecution = null;
        teacherDashboard.getStudentStats().remove(this);
        teacherDashboard = null;
    }

    public Integer getId() {
        return id;
    }

    public TeacherDashboard getTeacherDashboard() {
        return teacherDashboard;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public Integer getNumStudents() {
        return numStudents;
    }

    public Integer getNumMore75CorrectQuestions() {
        return numMore75CorrectQuestions;
    }

    public Integer getNumAtLeast3Quizzes() {
        return numAtLeast3Quizzes;
    }
    
    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public void setTeacherDashboard(TeacherDashboard teacherDashboard) {
        this.teacherDashboard = teacherDashboard;
        this.teacherDashboard.addStudentStats(this);
    }

    public void setNumStudents(Integer numStudents) {
        this.numStudents = numStudents;
    }

    public void setNumMore75CorrectQuestions(Integer numMore75CorrectQuestions) {
        this.numMore75CorrectQuestions = numMore75CorrectQuestions;
    }

    public void setNumAtLeast3Quizzes(Integer numAtLeast3Quizzes) {
        this.numAtLeast3Quizzes = numAtLeast3Quizzes;
    }

    public void update() {
        setNumStudents(this.courseExecution.getNumberOfActiveStudents());
        Integer numMore75CorrectQuestions = 0;
        Integer numAtLeast3Quizzes = 0;
        for (Student student: this.courseExecution.getStudents()) {
            Integer totalNumberOfQuestions = 0;
            Integer totalNumberOfCorrectQuestions = 0;
            Integer numberOfQuizzes = 0;
            for (QuizAnswer answer: student.getQuizAnswers()) {
                if (answer.getQuiz().getCourseExecution().getId() == this.courseExecution.getId()) {
                    totalNumberOfCorrectQuestions += (int) answer.getNumberOfCorrectAnswers();
                    totalNumberOfQuestions += answer.getQuiz().getQuizQuestionsNumber();
                    numberOfQuizzes++;
                }
            }
            if (totalNumberOfQuestions > 0) {
                if ((totalNumberOfCorrectQuestions / totalNumberOfQuestions) >= 0.75) {
                    numMore75CorrectQuestions++;
                }                
            }
            if (numberOfQuizzes >= 3) {
                numAtLeast3Quizzes++;
            }
        }
        setNumMore75CorrectQuestions(numMore75CorrectQuestions);
        setNumAtLeast3Quizzes(numAtLeast3Quizzes);
    }

    public void accept(Visitor visitor) {
        // Only to generate XML
    }

    @Override
    public String toString() {
        return "StudentStats{" +
                "id=" + id +
                ", numStudents=" + numStudents +
                ", numMore75CorrectQuestions=" + numMore75CorrectQuestions +
                ", numAtLeast3Quizzes=" + numAtLeast3Quizzes +
                '}';
    }
}