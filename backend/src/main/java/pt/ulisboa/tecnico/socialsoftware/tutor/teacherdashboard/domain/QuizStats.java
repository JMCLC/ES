package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;



import javax.persistence.*;
import java.util.Set;
import java.util.HashSet;


@Entity
public class QuizStats implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int numQuizzes = 0;

    private int uniqueQuizzesSolved = 0;

    private float averageQuizzesSolved;

    @ManyToOne
    private TeacherDashboard teacherDashboard;

    @OneToOne
    private CourseExecution courseExecution;

    public QuizStats() {
    }

    public QuizStats(TeacherDashboard teacherDashboard) {
        setTeacherDashboard(teacherDashboard);
        setCourseExecution(teacherDashboard.getCourseExecution());
    }

    public QuizStats(TeacherDashboard teacherDashboard, CourseExecution courseExecution) {
        this.teacherDashboard = teacherDashboard;
        this.courseExecution = courseExecution;

    }

    public Integer getId() { return id; }

    public int getNumQuizzes() { return numQuizzes; }

    public void setNumQuizzes(int numQuizzes) { 
        this.numQuizzes = numQuizzes; 
    }

    public int getUniqueQuizzesSolved() { return uniqueQuizzesSolved; }

    public void setUniqueQuizzesSolved(int uniqueQuizzesSolved) {
        this.uniqueQuizzesSolved = uniqueQuizzesSolved;
    }

    public float getAverageQuizzesSolved() { return averageQuizzesSolved; }

    public void setAverageQuizzesSolved(float averageQuizzesSolved) {
        this.averageQuizzesSolved = averageQuizzesSolved;
    }

    public TeacherDashboard getTeacherDashboard() { 
        return teacherDashboard; 
    }

    public void setTeacherDashboard(TeacherDashboard teacherDashboard) { 
        this.teacherDashboard = teacherDashboard; 
        this.teacherDashboard.addQuizStats(this);
    }

    public CourseExecution getCourseExecution() { 
        return courseExecution; 
    }

    public void setCourseExecution(CourseExecution courseExecution) { 
        this.courseExecution = courseExecution; 
    }

    public void remove() {
        teacherDashboard.getQuizStats().remove(this);
        courseExecution = null;
        teacherDashboard = null;
    }

    public void update() {
        setNumQuizzes(this.courseExecution.getQuizzes().size());
        setUniqueQuizzesSolved(getNumberOfSolvedQuizzes(courseExecution));
        if (courseExecution.getStudents().stream().count() != 0)
            setAverageQuizzesSolved((getAverageQuizzesSolvedPerStudent(courseExecution))/courseExecution.
            getStudents().stream().count());
    }

    public int getNumberOfSolvedQuizzes(CourseExecution courseExecution) {
        Set<Integer> quizzesId = new HashSet<>();

        for (Student student : this.courseExecution.getStudents()) {
            for (QuizAnswer quizAnswer : student.getQuizAnswers()) {
                quizzesId.add(quizAnswer.getQuiz().getKey());
            }
        }

        return quizzesId.size();
    }

    public float getAverageQuizzesSolvedPerStudent(CourseExecution courseExecution) {
        int numOfStudents = 0;
        int numberOfUniqueQuizzes = 0;
    
        for (Student student : this.courseExecution.getStudents()) {
            Set<Integer> quizzesKey = new HashSet<>();
            for (QuizAnswer quizAnswer : student.getQuizAnswers()) {
                quizzesKey.add(quizAnswer.getQuiz().getKey());
            }
            numOfStudents++;
            numberOfUniqueQuizzes += quizzesKey.size();
        }
        
        return (numberOfUniqueQuizzes/numOfStudents);
    }

    @Override
    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    @Override
    public String toString() {
        return "QuizStats{" +
                "id=" + id +
                ", numAvailable=" + numQuizzes +
                ", answeredQuestionUnique=" + uniqueQuizzesSolved +
                ", averageQuestionsAnswered=" + averageQuizzesSolved +
                '}';
    }
}