package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

@DataJpaTest
class TeacherDashboardTest extends SpockTest {
    def teacher
    def question

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)

        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        question.setCourse(externalCourse)
        questionRepository.save(question)
    }

    def createStudentAndPersist(String name, String username, String email) {
        def student = new Student(name, username, email, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        return student
    }

    def createTeacherDashboardAndPersist() {
        def teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
        return teacherDashboard
    }

    def createQuestionStatsAndPersist(TeacherDashboard teacherDashboard) {
        def questionStats = new QuestionStats(teacherDashboard)
        questionStatsRepository.save(questionStats)
        return questionStats
    }

    def createAvailableQuestion(Integer key, String title, String content) {
        def newQuestion = new Question()
        newQuestion.setKey(key)
        newQuestion.setTitle(title)
        newQuestion.setContent(content)
        newQuestion.setStatus(Question.Status.AVAILABLE)
        newQuestion.setNumberOfAnswers(4)
        newQuestion.setNumberOfCorrect(1)
        newQuestion.setCourse(externalCourse)
        questionRepository.save(newQuestion)
        return newQuestion
    }

    def createQuizAndPersist(Integer key) {
        def quiz = new Quiz()
        quiz.setKey(key)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setAvailableDate(LOCAL_DATE_BEFORE)
        quiz.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz)
        return quiz
    }

    def createQuizQuestionAndPersist(Integer seq, Quiz quiz, Question question) {
        def quizQuestion = new QuizQuestion()
        quizQuestion.setSequence(seq)
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quizQuestionRepository.save(quizQuestion)
        return quizQuestion
    }

    def createQuizAnswerAndPersist(Student student, Quiz quiz) {
        def quizAnswer = new QuizAnswer()
        quizAnswer.setAnswerDate(DateHandler.now())
        quizAnswer.setCompleted(true)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)
        return quizAnswer
    }

    def createQuestionAnswerAndPersist(Integer seq, QuizAnswer quizAnswer, QuizQuestion quizQuestion) {
        def questionAnswer = new QuestionAnswer()
        questionAnswer.setSequence(seq)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)
        return questionAnswer
    }

    def "create an empty dashboard"() {

        when: "a new teacher dashboard is created"
        def teacherDashboard = createTeacherDashboardAndPersist()

        then: "the new teacher dashboard is correctly persisted"
        teacherDashboardRepository.count() == 1L
        def result = teacherDashboardRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacher().getId() == teacher.getId()

        and: "the teacher has a reference for the dashboard"
        teacher.getDashboards().size() == 1
        teacher.getDashboards().contains(result)
    }

    def "remove a dashboard"() {
        given: "a dashboard"
        def dashboard = createTeacherDashboardAndPersist()

        when: "the user removes the dashboard"
        dashboard.remove()

        then: "the dashboard is removed"
        teacher.getDashboards().size() == 0
    }

    def "add QuestionStats and update"() {
        when: "there is no statistics"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def questionStats = createQuestionStatsAndPersist(teacherDashboard)
        def student = createStudentAndPersist(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL)

        teacherDashboard.update()

        then: "all statistics are correct"
        for (QuestionStats qs: teacherDashboard.getQuestion()) {
            qs == questionStats
        }

        def questionTwo = createAvailableQuestion(2, QUESTION_2_TITLE, QUESTION_2_CONTENT)
        def questionThree = createAvailableQuestion(3, QUESTION_3_TITLE, QUESTION_3_CONTENT)
        def quiz = createQuizAndPersist(1)
        def quizTwo = createQuizAndPersist(2)
        def quizQuestion = createQuizQuestionAndPersist(1, quiz, question)
        def quizQuestionTwo = createQuizQuestionAndPersist(2, quiz, questionTwo)
        def quizQuestionThree = createQuizQuestionAndPersist(1, quizTwo, questionTwo)
        def quizQuestionFour = createQuizQuestionAndPersist(2, quizTwo, questionThree)

        when: "a quiz is answered by a student"
        def quizAnswer = createQuizAnswerAndPersist(student, quiz)
        def quizAnswerTwo = createQuizAnswerAndPersist(student, quizTwo)
        def questionAnswer = createQuestionAnswerAndPersist(1, quizAnswer, quizQuestion)
        def questionAnswerTwo = createQuestionAnswerAndPersist(2, quizAnswer, quizQuestionTwo)
        def questionAnswerThree = createQuestionAnswerAndPersist(1, quizAnswerTwo, quizQuestionThree)
        def questionAnswerFour = createQuestionAnswerAndPersist(2, quizAnswerTwo, quizQuestionFour)

        teacherDashboard.update()

        then: "all statistics are correct"
        for (QuestionStats qs: teacherDashboard.getQuestion()) {
            qs == questionStats
        }
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
