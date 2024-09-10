package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;


@Entity
public class TeacherDashboard implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private Teacher teacher;

    @OneToMany
    private List<QuestionStats> question = new ArrayList<QuestionStats>();
    
    @OneToMany
    private List<QuizStats> quizStats = new ArrayList<QuizStats>();

    @OneToMany
    private List<StudentStats> studentStats = new ArrayList<StudentStats>();

    public TeacherDashboard() {
    }

    public TeacherDashboard(CourseExecution courseExecution, Teacher teacher) {
        setCourseExecution(courseExecution);
        setTeacher(teacher);
    }

    public void remove() {
        teacher.getDashboards().remove(this);
        teacher = null;
        quizStats = null;
        question = null;
        studentStats = null;
    }

    public Integer getId() {
        return id;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
        this.teacher.addDashboard(this);
    }

    public List<QuestionStats> getQuestion () { return question; }

    public void addQuestionStats(QuestionStats questionStats) {
        question.add(questionStats);
    }
            
    public List<QuizStats> getQuizStats () { 
        return quizStats; 
    }

    public void addQuizStats(QuizStats quizStats) {
        this.quizStats.add(quizStats);
    }
    
    public List<StudentStats> getStudentStats() {return studentStats; }

    public void addStudentStats(StudentStats studentStats) {
        this.studentStats.add(studentStats);
    }

    public void update() {
        quizStats.forEach(q -> {
            q.update();
        });
        question.forEach(q -> {
            q.update();
        });
        studentStats.forEach(s -> {
            s.update();
        });
    }

    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    @Override
    public String toString() {
        return "Dashboard{" +
                "id=" + id +
                ", courseExecution=" + courseExecution +
                ", teacher=" + teacher +
                '}';
    }

}