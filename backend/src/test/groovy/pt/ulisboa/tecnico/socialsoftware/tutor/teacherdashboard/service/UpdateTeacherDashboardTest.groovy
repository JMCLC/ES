package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
    class UpdateTeacherDashboardTest extends SpockTest {
    def teacher
    def teacherDashboard

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)
        teacher.addCourse(externalCourseExecution)
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())
        teacherDashboard = teacherDashboardRepository.findAll()[0]
        def studentStats = new StudentStats(teacherDashboard)
        studentStatsRepository.save(studentStats)
        def quizStats = new QuizStats(teacherDashboard)
        quizStatsRepository.save(quizStats)
        def questionStats = new QuestionStats(teacherDashboard)
        questionStatsRepository.save(questionStats)
        teacherDashboardRepository.save(teacherDashboard)
    }

    def createStudent(username) {
        def student = new Student(USER_1_USERNAME, username, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        return student;
    }

    def createQuiz(String quizTitle) {
        // Add Quiz
        def quiz = new Quiz()
        quiz.setTitle(quizTitle)
        quiz.setType(Quiz.QuizType.IN_CLASS.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setCreationDate(DateHandler.now())
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)

        // Add Question
        def question = createQuestion()
        def quizQuestion = createQuizQuestion(quiz, question)

        return quiz
    }

    def createQuestion() {
        def newQuestion = new Question()
        newQuestion.setTitle(QUESTION_1_TITLE)
        newQuestion.setCourse(externalCourse)
        newQuestion.setStatus(Question.Status.AVAILABLE)
        def questionDetails = new MultipleChoiceQuestion()
        newQuestion.setQuestionDetails(questionDetails)
        questionRepository.save(newQuestion)

        def option = new Option()
        option.setContent(OPTION_1_CONTENT)
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestionDetails(questionDetails)
        optionRepository.save(option)
        def optionKO = new Option()
        optionKO.setContent(OPTION_2_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        return newQuestion;
    }

    def createQuizQuestion(quiz, question) {
        def quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)
        return quizQuestion
    }

    def "updating dashboard when number of students changes"() {
        when: "a dashboard is created"
        createStudent("student1")

        and: "update teacher dashboard"
        teacherDashboardService.updateTeacherDashboard(teacherDashboard.getId())

        then: "the number of students should have increased"
        teacherDashboardRepository.count() == 1L
        teacherDashboard.getStudentStats()[0].getNumStudents() == 1
    }

    def "updating dashboard when number of quizzes changes"() {
        when: "a dashboard is created"
        createQuiz("quiz1")
        createQuiz("quiz2")

        and: "update teacher dashboard"
        teacherDashboardService.updateTeacherDashboard(teacherDashboard.getId())

        then: "the number of quizzes should have increased"
        teacherDashboardRepository.count() == 1L
        teacherDashboard.getQuizStats()[0].getNumQuizzes() == 2
    }

    def "updating dashboard when all stats changes"() {
        when: "a dashboard is created"
        createStudent("student1")
        createQuiz("quiz1")

        and: "update teacher dashboard"
        teacherDashboardService.updateTeacherDashboard(teacherDashboard.getId())

        then: "all stats should have increased"
        teacherDashboardRepository.count() == 1L
        teacherDashboard.getStudentStats()[0].getNumStudents() == 1
        teacherDashboard.getQuizStats()[0].getNumQuizzes() == 1
        teacherDashboard.getQuestion()[0].getNumAvailable() == 1
    }

    @Unroll
    def "cannot update a dashboard with an id=#dashboardId"() {
        when: "a dashboard is created"
        teacherDashboardService.updateTeacherDashboard(dashboardId)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DASHBOARD_NOT_FOUND

        where:
        dashboardId << [10, 100]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}