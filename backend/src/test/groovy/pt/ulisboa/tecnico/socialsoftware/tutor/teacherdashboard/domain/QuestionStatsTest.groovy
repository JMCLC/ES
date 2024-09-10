package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

@DataJpaTest
class QuestionStatsTest extends SpockTest {
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

    def "create empty question statistics"() {
        when: "a question statistics is created"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def questionStats = createQuestionStatsAndPersist(teacherDashboard)

        then: "the new question statistics is correctly persisted"
        questionStatsRepository.count() == 1L
        def result = questionStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()

        and: "the question statistics has a reference for the teacher dashboard"
        result.getTeacherDashboard() == teacherDashboard

        and: "the teacher dashboard has a reference for the question statistics"
        teacherDashboard.getQuestion().size() == 1
        teacherDashboard.getQuestion().contains(result)
    }

    def "Add a question"() {
        when: "a question statistics is created and updated"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def questionStats = createQuestionStatsAndPersist(teacherDashboard)
        questionStats.update()

        then: "the number of available questions is correct"
        questionStats.getNumAvailable() == 1
    }

    def "Add a new question"() {
        when: "a new question is created"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def questionStats = createQuestionStatsAndPersist(teacherDashboard)
        def questionTwo = createAvailableQuestion(2, QUESTION_2_TITLE, QUESTION_2_CONTENT)
        questionStats.update()

        then: "the number of available questions is correct"
        questionStats.getNumAvailable() == 2
    }

    def "Make two updates"() {
        when: "a new question is created"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def questionStats = createQuestionStatsAndPersist(teacherDashboard)
        def questionTwo = createAvailableQuestion(2, QUESTION_2_TITLE, QUESTION_2_CONTENT)
        questionStats.update()

        then: "the number of available questions is correct"
        questionStats.getNumAvailable() == 2

        and: "the number of available questions is still correct"
        def questionThree = createAvailableQuestion(3, QUESTION_3_TITLE, QUESTION_3_CONTENT)
        questionStats.update()
        questionStats.getNumAvailable() == 3
    }

    def "one student answers 1 quiz with 1 question"() {
        def teacherDashboard = createTeacherDashboardAndPersist()
        def questionStats = createQuestionStatsAndPersist(teacherDashboard)
        def student = createStudentAndPersist(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL)
        def quiz = createQuizAndPersist(1)
        def quizQuestion = createQuizQuestionAndPersist(1, quiz, question)

        when: "a quiz is answered by a student"
        def quizAnswer = createQuizAnswerAndPersist(student, quiz)
        def questionAnswer = createQuestionAnswerAndPersist(1, quizAnswer, quizQuestion)
        questionStats.update()

        then: "the total number of unique question answered is correct"
        questionStats.getAnsweredQuestionUnique() == 1
    }

    def "one student answers 1 quiz with 2 equal questions"() {
        def teacherDashboard = createTeacherDashboardAndPersist()
        def questionStats = createQuestionStatsAndPersist(teacherDashboard)
        def student = createStudentAndPersist(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL)
        def quiz = createQuizAndPersist(1)
        def quizQuestion = createQuizQuestionAndPersist(1, quiz, question)
        def quizQuestion2 = createQuizQuestionAndPersist(2, quiz, question)

        when: "a quiz is answered by a student"
        def quizAnswer = createQuizAnswerAndPersist(student, quiz)
        def questionAnswer = createQuestionAnswerAndPersist(1, quizAnswer, quizQuestion)
        def questionAnswer2 = createQuestionAnswerAndPersist(2, quizAnswer, quizQuestion2)
        questionStats.update()

        then: "the total number of unique question answered is correct"
        questionStats.getAnsweredQuestionUnique() == 1
    }

    def "one student answers 1 quiz with 2 different questions"() {
        def teacherDashboard = createTeacherDashboardAndPersist()
        def questionStats = createQuestionStatsAndPersist(teacherDashboard)
        def student = createStudentAndPersist(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL)
        def questionTwo = createAvailableQuestion(2, QUESTION_2_TITLE, QUESTION_2_CONTENT)
        def quiz = createQuizAndPersist(1)
        def quizQuestion = createQuizQuestionAndPersist(1, quiz, question)
        def quizQuestion2 = createQuizQuestionAndPersist(2, quiz, questionTwo)

        when: "a quiz is answered by a student"
        def quizAnswer = createQuizAnswerAndPersist(student, quiz)
        def questionAnswer = createQuestionAnswerAndPersist(1, quizAnswer, quizQuestion)
        def questionAnswer2 = createQuestionAnswerAndPersist(2, quizAnswer, quizQuestion2)
        questionStats.update()

        then: "the total number of unique question answered is correct"
        questionStats.getAnsweredQuestionUnique() == 2
    }

    def "one student answers 2 quizzes with 2 questions each"() {
        def teacherDashboard = createTeacherDashboardAndPersist()
        def questionStats = createQuestionStatsAndPersist(teacherDashboard)
        def student = createStudentAndPersist(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL)
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

        questionStats.update()

        then: "the total number of unique question answered is correct"
        questionStats.getAnsweredQuestionUnique() == 3

        and: "the average unique questions answered per student is correct"
        questionStats.getAverageQuestionsAnswered() == 3
    }

    def "two students, one answers 2 quizzes with 3 questions each, and one answers 1 quiz with 3 questions"() {
        def teacherDashboard = createTeacherDashboardAndPersist()
        def questionStats = createQuestionStatsAndPersist(teacherDashboard)
        def student = createStudentAndPersist(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL)
        def studentTwo = createStudentAndPersist(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL)
        def questionTwo = createAvailableQuestion(2, QUESTION_2_TITLE, QUESTION_2_CONTENT)
        def questionThree = createAvailableQuestion(3, QUESTION_3_TITLE, QUESTION_3_CONTENT)
        def questionFour = createAvailableQuestion(4, QUESTION_4_TITLE, QUESTION_4_CONTENT)
        def questionFive = createAvailableQuestion(5, QUESTION_5_TITLE, QUESTION_5_CONTENT)
        def quiz = createQuizAndPersist(1)
        def quizTwo = createQuizAndPersist(2)
        def quizThree = createQuizAndPersist(3)

        def quizQuestion = createQuizQuestionAndPersist(1, quiz, question)
        def quizQuestionTwo = createQuizQuestionAndPersist(2, quiz, questionTwo)
        def quizQuestionThree = createQuizQuestionAndPersist(3, quiz, questionThree)

        def quizTwoQuestion = createQuizQuestionAndPersist(1, quizTwo, questionTwo)
        def quizTwoQuestionTwo = createQuizQuestionAndPersist(2, quizTwo, questionTwo)
        def quizTwoQuestionThree = createQuizQuestionAndPersist(3, quizTwo, question)

        def quizThreeQuestion = createQuizQuestionAndPersist(1, quizThree, questionFour)
        def quizThreeQuestionTwo = createQuizQuestionAndPersist(2, quizThree, questionFive)
        def quizThreeQuestionThree = createQuizQuestionAndPersist(3, quizThree, questionThree)


        when: "a quiz is answered by a student"
        def quizAnswer = createQuizAnswerAndPersist(student, quiz)
        def quizAnswerTwo = createQuizAnswerAndPersist(student, quizTwo)
        def quizAnswerThree = createQuizAnswerAndPersist(studentTwo, quizThree)

        def questionAnswer = createQuestionAnswerAndPersist(1, quizAnswer, quizQuestion)
        def questionAnswerTwo = createQuestionAnswerAndPersist(2, quizAnswer, quizQuestionTwo)
        def questionAnswerThree = createQuestionAnswerAndPersist(3, quizAnswer, quizQuestionThree)

        def questionAnswerFour = createQuestionAnswerAndPersist(1, quizAnswerTwo, quizTwoQuestion)
        def questionAnswerFive = createQuestionAnswerAndPersist(2, quizAnswerTwo, quizTwoQuestionTwo)
        def questionAnswerSix = createQuestionAnswerAndPersist(3, quizAnswerTwo, quizTwoQuestionThree)

        def questionAnswerSeven = createQuestionAnswerAndPersist(1, quizAnswerThree, quizThreeQuestion)
        def questionAnswerEight = createQuestionAnswerAndPersist(2, quizAnswerThree, quizThreeQuestionTwo)
        def questionAnswerNine = createQuestionAnswerAndPersist(3, quizAnswerThree, quizThreeQuestionThree)

        questionStats.update()

        then: "the total number of unique question answered is correct"
        questionStats.getAnsweredQuestionUnique() == 5

        and: "the average unique questions answered per student is correct"
        questionStats.getAverageQuestionsAnswered() == 3
    }

    def "three students, each answers 1 quiz with 3 questions" () {
        def teacherDashboard = createTeacherDashboardAndPersist()
        def questionStats = createQuestionStatsAndPersist(teacherDashboard)
        def student = createStudentAndPersist(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL)
        def studentTwo = createStudentAndPersist(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL)
        def studentThree = createStudentAndPersist(USER_3_NAME, USER_3_USERNAME, USER_3_EMAIL)
        def questionTwo = createAvailableQuestion(2, QUESTION_2_TITLE, QUESTION_2_CONTENT)
        def questionThree = createAvailableQuestion(3, QUESTION_3_TITLE, QUESTION_3_CONTENT)
        def questionFour = createAvailableQuestion(4, QUESTION_3_TITLE, QUESTION_2_CONTENT)
        def quiz = createQuizAndPersist(1)
        def quizTwo = createQuizAndPersist(2)
        def quizThree = createQuizAndPersist(3)

        def quizQuestion = createQuizQuestionAndPersist(1, quiz, question)
        def quizQuestionTwo = createQuizQuestionAndPersist(2, quiz, questionTwo)
        def quizQuestionThree = createQuizQuestionAndPersist(3, quiz, questionThree)

        def quizTwoQuestion = createQuizQuestionAndPersist(1, quizTwo, questionTwo)
        def quizTwoQuestionTwo = createQuizQuestionAndPersist(2, quizTwo, questionTwo)
        def quizTwoQuestionThree = createQuizQuestionAndPersist(3, quizTwo, question)

        def quizThreeQuestion = createQuizQuestionAndPersist(1, quizThree, question)
        def quizThreeQuestionTwo = createQuizQuestionAndPersist(2, quizThree, questionTwo)
        def quizThreeQuestionThree = createQuizQuestionAndPersist(3, quizThree, questionThree)

        when: "a quiz is answered by a student"
        def quizAnswer = createQuizAnswerAndPersist(student, quiz)
        def quizAnswerTwo = createQuizAnswerAndPersist(studentTwo, quizTwo)
        def quizAnswerThree = createQuizAnswerAndPersist(studentThree, quizThree)

        def questionAnswer = createQuestionAnswerAndPersist(1, quizAnswer, quizQuestion)
        def questionAnswerTwo = createQuestionAnswerAndPersist(2, quizAnswer, quizQuestionTwo)
        def questionAnswerThree = createQuestionAnswerAndPersist(3, quizAnswer, quizQuestionThree)

        def questionAnswerFour = createQuestionAnswerAndPersist(1, quizAnswerTwo, quizTwoQuestion)
        def questionAnswerFive = createQuestionAnswerAndPersist(2, quizAnswerTwo, quizTwoQuestionTwo)
        def questionAnswerSix = createQuestionAnswerAndPersist(3, quizAnswerTwo, quizTwoQuestionThree)

        def questionAnswerSeven = createQuestionAnswerAndPersist(1, quizAnswerThree, quizThreeQuestion)
        def questionAnswerEight = createQuestionAnswerAndPersist(2, quizAnswerThree, quizThreeQuestionTwo)
        def questionAnswerNine = createQuestionAnswerAndPersist(3, quizAnswerThree, quizThreeQuestionThree)

        questionStats.update()

        then: "the total number of unique questions is correct"
        questionStats.getNumAvailable() == 4

        and: "the total number of unique question answered is correct"
        questionStats.getAnsweredQuestionUnique() == 3

        and: "the average unique questions answered per student is correct"
        Float.compare(questionStats.getAverageQuestionsAnswered(), 8/3) == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}