package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStatsTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option



@DataJpaTest
class QuizStatsTest extends SpockTest {
    def teacher
    def quiz
    def quiz1
    def quiz2
    def student1
    def student2
    def student3
    def question1
    def question2
    def question3
    def question4
    def courseDto
    def option
    def other_option
   


    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)

        Quiz quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)

        question1 = new Question()
        question1.setKey(1)
        question1.setCourse(externalCourse)
        question1.setContent("Question Content")
        question1.setTitle("Question Title")
        questionRepository.save(question1)

        question2 = new Question()
        question2.setKey(2)
        question2.setCourse(externalCourse)
        question2.setContent("Question Content")
        question2.setTitle("Question Title")
        questionRepository.save(question2)

        question3 = new Question()
        question3.setKey(3)
        question3.setCourse(externalCourse)
        question3.setContent("Question Content")
        question3.setTitle("Question Title")
        questionRepository.save(question3)

        question4 = new Question()
        question4.setKey(4)
        question4.setCourse(externalCourse)
        question4.setContent("Question Content")
        question4.setTitle("Question Title")
        questionRepository.save(question4)

        def questionDetails = new MultipleChoiceQuestion();
        question1.setQuestionDetails(questionDetails);
        question2.setQuestionDetails(questionDetails);
        question3.setQuestionDetails(questionDetails);
        questionDetailsRepository.save(questionDetails);

        option = new Option()
        option.setContent("Option Content")
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestionDetails(questionDetails)
        optionRepository.save(option)

    }

    def createStudentAndPersist(String name, String username, String email) {
        def student = new Student(name, username, email, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        return student
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

    def createTeacherDashboardAndPersist() {
        def teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
        return teacherDashboard
    }

    def createQuiz(Integer key) {
        def quiz = new Quiz()
        quiz.setKey(key)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quiz.setAvailableDate(LOCAL_DATE_BEFORE)
        quiz.setConclusionDate(LOCAL_DATE_TOMORROW)
        quiz.setResultsDate(null)
        quiz.setCourseExecution(externalCourseExecution)
        return quiz
    }

    def createQuizQuestion(Quiz quiz) {
        def quizQuestion = new QuizQuestion()
        quizQuestion.setSequence(1)
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question1)
        return quizQuestion
    }

    def createQuizAnswer(Student student, Quiz quiz) {
        def quizAnswer = new QuizAnswer()
        quizAnswer.setAnswerDate(DateHandler.now())
        quizAnswer.setCompleted(true)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(quiz)
        return quizAnswer
    }

    def createQuizStatsAndPersist(TeacherDashboard teacherDashboard) {
        def quizStats = new QuizStats(teacherDashboard)
        quizStatsRepository.save(quizStats)
        return quizStats
    }


    def createQuestionAnswer(QuizAnswer quizAnswer, QuizQuestion quizQuestion) {
        def questionAnswer = new QuestionAnswer()
        questionAnswer.setSequence(0)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        return questionAnswer
    }

    def createAnswerDetails(QuestionAnswer questionAnswer, Option option) {
        def answerDetails = new MultipleChoiceAnswer(questionAnswer, option);
        questionAnswer.setAnswerDetails(answerDetails);
        return answerDetails       
    }

    def saveQuiz(Quiz quiz, QuizAnswer quizAnswer, QuestionAnswer questionAnswer, MultipleChoiceAnswer answerDetails) {
        quizRepository.save(quiz)
        quizAnswerRepository.save(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)        
    }

    def createAndAnswerQuizOne(Student student, Option option){
        quiz1 = createQuiz(1)
        quiz1Question = createQuizQuestion(quiz1)
        def quiz1Answer = createQuizAnswer(student, quiz1)
        def question1Answer = createQuestionAnswer(quiz1Answer, quiz1Question)
        def answer1Details = createAnswerDetails(question1Answer, option)
        saveQuiz(quiz1, quiz1Answer, question1Answer, answer1Details)
    }


    def "create empty quiz statistics"() {

        when: "a quiz statistics is created"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def quizStats = createQuizStatsAndPersist(teacherDashboard)

        then: "the new quiz statistics is correctly persisted"
        quizStatsRepository.count() == 1L
        quizStats.getTeacherDashboard() == teacherDashboard
        def result = quizStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()

        and: "the teacher dashboard has a reference for the quiz statistics"
        teacherDashboard.getQuizStats().toString() == "[QuizStats{id=1, numAvailable=0, answeredQuestionUnique=0, averageQuestionsAnswered=0.0}]"
        teacherDashboard.getQuizStats().size() == 1
        teacherDashboard.getQuizStats().contains(result)
    }

    def "Add a quiz"() {
        when: "a quiz statistics is created and updated"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def quizStats = createQuizStatsAndPersist(teacherDashboard)
        quizStats.update()

        then: "the number of available quizzes is correct"
        quizStats.getNumQuizzes() == 1
    }

    def "Add another quiz"() {
        when: "a quiz statistics is created and updated"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def quizStats = createQuizStatsAndPersist(teacherDashboard)
        createQuiz(2)
        quizStats.update()
        

        then: "the number of available quizzes is correct"
        quizStats.getNumQuizzes() == 2
    }

    def "Make two updates"() {
        when: "a new quiz is created"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def quizStats = createQuizStatsAndPersist(teacherDashboard)
        createQuiz(2)
        quizStats.update()

        then: "the number of available quizzes is correct"
        quizStats.getNumQuizzes() == 2

        and: "the number of available quizzes is still correct"
        createQuiz(3)
        quizStats.update()
        quizStats.getNumQuizzes() == 3
    }

    def "one student solves 1 unique quiz"() {
        when: "a quiz statistics is created"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def quizStats = createQuizStatsAndPersist(teacherDashboard)
        def student = createStudentAndPersist(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL)
        def quiz = createQuizAndPersist(1)
        def quizQuestion = createQuizQuestionAndPersist(1, quiz, question1)

        and: "a quiz is answered by a student"
        def quizAnswer = createQuizAnswerAndPersist(student, quiz)
        def questionAnswer = createQuestionAnswerAndPersist(1, quizAnswer, quizQuestion)

        and: "updated"
        quizStats.update()

        then: "the total number of unique quizzes is correct"
        quizStats.getNumberOfSolvedQuizzes() == 1

    }

    def "one student solves 2 unique quizzes"() {
        when: "a quiz statistics is created"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def quizStats = createQuizStatsAndPersist(teacherDashboard)
        def student = createStudentAndPersist(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL)
        def quiz = createQuizAndPersist(1)
        def quizQuestion = createQuizQuestionAndPersist(1, quiz, question1)

        and: "a quiz is answered by a student"
        def quizAnswer = createQuizAnswerAndPersist(student, quiz)
        def questionAnswer = createQuestionAnswerAndPersist(1, quizAnswer, quizQuestion)

        and: "the same quiz statistics is created"
        def quiz2 = createQuizAndPersist(2)
        def quizQuestion2 = createQuizQuestionAndPersist(2, quiz2, question2)

        and: "a quiz is answered by a student"
        def quizAnswer2 = createQuizAnswerAndPersist(student, quiz2)
        def questionAnswer2 = createQuestionAnswerAndPersist(1, quizAnswer2, quizQuestion2)

        and: "updated"
        quizStats.update()

        then: "the total number of unique quizzes is correct"
        quizStats.getNumberOfSolvedQuizzes() == 2
    }

    def "one student solves 2 identical quizzes"() {
        when: "a quiz statistics is created"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def quizStats = createQuizStatsAndPersist(teacherDashboard)
        def student = createStudentAndPersist(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL)
        def quiz = createQuizAndPersist(1)
        def quizQuestion = createQuizQuestionAndPersist(1, quiz, question1)

        and: "a quiz is answered by a student"
        def quizAnswer = createQuizAnswerAndPersist(student, quiz)
        def questionAnswer = createQuestionAnswerAndPersist(1, quizAnswer, quizQuestion)

        and: "the same quiz statistics is created"
        def quiz2 = createQuizAndPersist(1)
        def quizQuestion2 = createQuizQuestionAndPersist(1, quiz2, question1)

        and: "a quiz is answered by a student"
        def quizAnswer2 = createQuizAnswerAndPersist(student, quiz2)
        def questionAnswer2 = createQuestionAnswerAndPersist(1, quizAnswer2, quizQuestion2)

        and: "updated"
        quizStats.update()

        then: "the total number of unique quizzes is correct"
        quizStats.getNumberOfSolvedQuizzes() == 1
    }

    def "two students solve 1 unique quiz each"() {
        when: "a quiz statistics is created"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def quizStats = createQuizStatsAndPersist(teacherDashboard)
        def student = createStudentAndPersist(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL)
        def quiz = createQuizAndPersist(1)
        def quizQuestion = createQuizQuestionAndPersist(1, quiz, question1)

        and: "a quiz is answered by a student"
        def quizAnswer = createQuizAnswerAndPersist(student, quiz)
        def questionAnswer = createQuestionAnswerAndPersist(1, quizAnswer, quizQuestion)

        and: "a different quiz is answered by another student"
        def student2 = createStudentAndPersist(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL)
        def quiz2 = createQuizAndPersist(2)
        def quizQuestion2 = createQuizQuestionAndPersist(2, quiz2, question2)
        def quizAnswer2 = createQuizAnswerAndPersist(student2, quiz2)
        def questionAnswer2 = createQuestionAnswerAndPersist(2, quizAnswer2, quizQuestion2)

        and: "updated"
        quizStats.update()

        then: "the total number of unique quizzes is correct"
        quizStats.getAverageQuizzesSolvedPerStudent() == 1

    }

    def "one student solves 4 unique quizzes and two students solve 1 unique quiz each"() {
        when: "a quiz statistics is created"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def quizStats = createQuizStatsAndPersist(teacherDashboard)
        def student = createStudentAndPersist(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL)
        def quiz = createQuizAndPersist(1)
        def quizQuestion = createQuizQuestionAndPersist(1, quiz, question1)
        def quiz2 = createQuizAndPersist(2)
        def quizQuestion2 = createQuizQuestionAndPersist(2, quiz2, question2)
        def quiz3 = createQuizAndPersist(3)
        def quizQuestion3 = createQuizQuestionAndPersist(3, quiz3, question3)
        def quiz4 = createQuizAndPersist(4)
        def quizQuestion4 = createQuizQuestionAndPersist(4, quiz4, question4)

        and: "a quiz is answered by a student"
        def quizAnswer = createQuizAnswerAndPersist(student, quiz)
        def questionAnswer = createQuestionAnswerAndPersist(1, quizAnswer, quizQuestion)
        def quizAnswer2 = createQuizAnswerAndPersist(student, quiz2)
        def questionAnswer2 = createQuestionAnswerAndPersist(2, quizAnswer2, quizQuestion2)
        def quizAnswer3 = createQuizAnswerAndPersist(student, quiz3)
        def questionAnswer3 = createQuestionAnswerAndPersist(3, quizAnswer3, quizQuestion3)
        def quizAnswer4 = createQuizAnswerAndPersist(student, quiz4)
        def questionAnswer4 = createQuestionAnswerAndPersist(4, quizAnswer4, quizQuestion4)

        and: "a different quiz is answered by another student"
        def student2 = createStudentAndPersist(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL)
        def quiz5 = createQuizAndPersist(5)
        def quizQuestion5 = createQuizQuestionAndPersist(5, quiz5, question2)
        def quizAnswer5 = createQuizAnswerAndPersist(student2, quiz5)
        def questionAnswer5 = createQuestionAnswerAndPersist(2, quizAnswer5, quizQuestion5)

        and: "a different quiz is answered by another student"
        def student3 = createStudentAndPersist(USER_3_NAME, USER_3_USERNAME, USER_3_EMAIL)
        def quizAnswer6 = createQuizAnswerAndPersist(student3, quiz5)
        def questionAnswer6 = createQuestionAnswerAndPersist(2, quizAnswer6, quizQuestion5)

        and: "updated"
        quizStats.update()

        then: "the total number of unique quizzes is correct"
        quizStats.getAverageQuizzesSolvedPerStudent() == 2
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}