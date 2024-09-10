package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import javax.persistence.*;
import java.util.Set;
import java.util.HashSet;
import java.util.List;


@Entity
public class QuestionStats implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int numAvailable = 0;

    private int answeredQuestionUnique = 0;

    private float averageQuestionsAnswered = 0.0f;

    @ManyToOne
    private TeacherDashboard teacherDashboard;

    @OneToOne
    private CourseExecution courseExecution;

    public QuestionStats() {
    }

    public QuestionStats(TeacherDashboard teacherDashboard) {
        setTeacherDashboard(teacherDashboard);
        setCourseExecution(teacherDashboard.getCourseExecution());
    }

    public QuestionStats(TeacherDashboard teacherDashboard, CourseExecution courseExecution) {
        this.teacherDashboard = teacherDashboard;
        this.courseExecution = courseExecution;
    }

    public Integer getId() { return id; }

    public int getNumAvailable() { return numAvailable; }

    public void setNumAvailable(int numAvailable) { this.numAvailable = numAvailable; }

    public int getAnsweredQuestionUnique() { return answeredQuestionUnique; }

    public void setAnsweredQuestionUnique(int answeredQuestionUnique) {
        this.answeredQuestionUnique = answeredQuestionUnique;
    }

    public float getAverageQuestionsAnswered() { return averageQuestionsAnswered; }

    public void setAverageQuestionsAnswered(float averageQuestionsAnswered) {
        this.averageQuestionsAnswered = averageQuestionsAnswered;
    }

    public TeacherDashboard getTeacherDashboard() { return teacherDashboard; }

    public void setTeacherDashboard(TeacherDashboard teacherDashboard) {
        this.teacherDashboard = teacherDashboard;
        this.teacherDashboard.addQuestionStats(this);
    }

    public CourseExecution getCourseExecution() { return courseExecution; }

    public void setCourseExecution(CourseExecution courseExecution) { this.courseExecution = courseExecution; }

    public void remove () {
        courseExecution = null;
        teacherDashboard.getQuestion().remove(this);
        teacherDashboard = null;
    }

    public void update() {
        // number of available questions
        setNumAvailable((int) this.courseExecution.getCourse().getQuestions().stream()
                .filter(question -> question.getStatus().equals(Question.Status.AVAILABLE)).count());


        // number of answered questions at least once
        this.answeredQuestionUnique = (int) courseExecution.getQuizzes().stream()
                .flatMap(q -> q.getQuizAnswers() .stream()
                        .flatMap(qa -> qa.getQuestionAnswers().stream()
                                .map(QuestionAnswer::getQuestion)))
                .distinct()
                .count();

        // number of students
        int students = courseExecution.getStudents().size();

        long uniqueAllStudents = courseExecution.getStudents().stream().mapToLong(student ->
                student.getQuizAnswers().stream().flatMap(
                        qa -> qa.getQuestionAnswers().stream().map(QuestionAnswer::getQuestion)
                ).distinct().count()).sum();

        // average
        this.averageQuestionsAnswered = students > 0 ? (float) uniqueAllStudents / students : 0.0f;
    }

    public int getNumberOfAnsweredQuestions(CourseExecution courseExecution) {
        Set<Question> uniqueQuestions = new HashSet<>();
        Set<Quiz> quizzes = courseExecution.getQuizzes();

        for (Quiz quiz : quizzes) {
            Set<QuizAnswer> quizAnswers = quiz.getQuizAnswers();

            for (QuizAnswer quizAnswer : quizAnswers) {
                List<QuestionAnswer> questionAnswers = quizAnswer.getQuestionAnswers();

                for (QuestionAnswer questionAnswer : questionAnswers) {
                    Question question = questionAnswer.getQuestion();
                    uniqueQuestions.add(question);
                }

            }

        }

        return uniqueQuestions.size();
    }

    public float getTotalNumberOfAnsweredQuestionsPerStudent(CourseExecution courseExecution) {
        int totalUniqueQuestions = 0;

        for (Student student : courseExecution.getStudents()) {
            Set<Question> uniqueQuestions = new HashSet<>();

            for (QuizAnswer quizAnswers : student.getQuizAnswers()) {

                for (QuestionAnswer questionAnswers : quizAnswers.getQuestionAnswers()) {
                    uniqueQuestions.add(questionAnswers.getQuestion());
                }

            }

            totalUniqueQuestions += uniqueQuestions.size();
        }

        return (float) totalUniqueQuestions;
    }

    @Override
    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    @Override
    public String toString() {
        return "QuestionStats{" +
                "id=" + id +
                ", numAvailable=" + numAvailable +
                ", answeredQuestionUnique=" + answeredQuestionUnique +
                ", averageQuestionsAnswered=" + averageQuestionsAnswered +
                ", teacherDashboard=" + teacherDashboard +
                ", courseExecution=" + courseExecution +
                '}';
    }
}
