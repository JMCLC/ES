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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

import spock.lang.Unroll

@DataJpaTest
    class UpdateAllTeacherDashboardsTest extends SpockTest {
    def teacher
    def teacher2
    def teacher3
    def teacherDashboards = []

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, false)
        teacher2 = new Teacher(USER_2_NAME, false)
        teacher3 = new Teacher(USER_3_NAME, false)
        userRepository.save(teacher)
        userRepository.save(teacher2)
        userRepository.save(teacher3)
        teacher.addCourse(externalCourseExecution)
        teacher2.addCourse(externalCourseExecution)
        teacher3.addCourse(externalCourseExecution)
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId()).getId()
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher2.getId()).getId()
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher3.getId()).getId()
        def studentStats
        def quizStats
        def currentDashboard
        for (int i = 0; i < 3; i++) {
            currentDashboard = teacherDashboardRepository.findAll()[i]
            teacherDashboards.add(currentDashboard)
            studentStats = new StudentStats(currentDashboard)
            quizStats = new QuizStats(currentDashboard)
            studentStatsRepository.save(studentStats)
            quizStatsRepository.save(quizStats)
            teacherDashboardRepository.save(currentDashboard)
        }
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

    def "updating all dashboards when number of students changes"() {
        when: "a dashboard is created"
        createStudent("student1")

        and: "update teacher dashboard"
        teacherDashboardService.updateAllTeacherDashboards()

        then: "the number of students should have increased"
        teacherDashboardRepository.count() == 3L
        def total = 0
        for (int i = 0; i < 3; i++) {
            total += teacherDashboards[i].getStudentStats()[0].getNumStudents()
        }
        total == 3
    }

    def "updating all dashboards when number of quizzes changes"() {
        when: "a dashboard is created"
        createQuiz("quiz1")
        createQuiz("quiz2")

        and: "update teacher dashboard"
        teacherDashboardService.updateAllTeacherDashboards()

        then: "the number of quizzes should have increased"
        teacherDashboardRepository.count() == 3L
        def total = 0
        for (int i = 0; i < 3; i++) {
            total += teacherDashboards[i].getQuizStats()[0].getNumQuizzes()
        }
        total == 6
    }

    def "updating all dashboards when stats changes"() {
        when: "a dashboard is created"
        createStudent("student1")
        createQuiz("quiz1")

        and: "update teacher dashboard"
        teacherDashboardService.updateAllTeacherDashboards()

        then: "all stats should have increased"
        teacherDashboardRepository.count() == 3L
        def totalQuizStats = 0
        def totalStudentStats = 0
        def totalQuestionStats = 0
        for (int i = 0; i < 3; i++) {
            totalStudentStats += teacherDashboards[i].getStudentStats()[0].getNumStudents()
            totalQuizStats += teacherDashboards[i].getQuizStats()[0].getNumQuizzes()
            totalQuestionStats += teacherDashboards[i].getQuestion()[0].getNumAvailable()
        }
        totalStudentStats == 3
        totalQuizStats == 3
        totalQuestionStats == 3
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}