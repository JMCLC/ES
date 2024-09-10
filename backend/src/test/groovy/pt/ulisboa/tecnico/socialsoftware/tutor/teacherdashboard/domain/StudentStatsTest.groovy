package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.CourseExecutionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import spock.lang.Unroll
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats

@DataJpaTest
class StudentStatsTest extends SpockTest {
    def student1
    def student2
    def teacher
    def question1
    def question2
    def question3
    def courseDto
    def option
    def other_option
    def quiz1
    def quiz2
    def quiz3
    def quiz1Question
    def quiz2Question
    def quiz3Question

    def setup() {
        createExternalCourseAndExecution()

        courseDto = new CourseExecutionDto(externalCourseExecution)

        student1 = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student1.addCourse(externalCourseExecution)
        userRepository.save(student1)
        
        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)

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

        other_option = new Option()
        other_option.setContent("Option Content")
        other_option.setCorrect(false)
        other_option.setSequence(0)
        other_option.setQuestionDetails(questionDetails)
        optionRepository.save(other_option)
    }

    def createTeacherDashboardAndPersist() {
        def teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
        return teacherDashboard
    }

    def createStudentStatsAndPersist(TeacherDashboard teacherDashboard) {
        def studentStats = new StudentStats(teacherDashboard)
        StudentStatsRepository.save(studentStats)
        return studentStats
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
	
    def createAndAnswerQuizTwo(Student student, Option option) {
        quiz2 = createQuiz(2)
        quiz2Question = createQuizQuestion(quiz2)
        def quiz2Answer = createQuizAnswer(student, quiz2)
        def question2Answer = createQuestionAnswer(quiz2Answer, quiz2Question)
        def answer2Details = createAnswerDetails(question2Answer, option)
        saveQuiz(quiz2, quiz2Answer, question2Answer, answer2Details)
    }

	def createAndAnswerQuizThree(Student student, Option option){
        quiz3 = createQuiz(3)
        quiz3Question = createQuizQuestion(quiz3)
        def quiz3Answer = createQuizAnswer(student, quiz3)
        def question3Answer = createQuestionAnswer(quiz3Answer, quiz3Question)
        def answer3Details = createAnswerDetails(question3Answer, option)
        saveQuiz(quiz3, quiz3Answer, question3Answer, answer3Details)
    }


    def "create empty student statistics"() {

        when: "a student statistics is created"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def studentStats = createStudentStatsAndPersist(teacherDashboard)

        then: "the new student statistics is correctly persisted"
        studentStatsRepository.count() == 1L
        studentStats.getTeacherDashboard() == teacherDashboard
        def result = studentStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()

        and: "the teacher dashboard has a reference for the student statistics"
        teacherDashboard.getStudentStats().toString() == "[StudentStats{id=1, numStudents=0, numMore75CorrectQuestions=0, numAtLeast3Quizzes=0}]"
        teacherDashboard.getStudentStats().size() == 1
        teacherDashboard.getStudentStats().contains(result)
    }

    def "add a student and get the number of students"(){
        
        when:"a student statistics is created"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def studentStats = createStudentStatsAndPersist(teacherDashboard)
        
        and: "a student is added to student statistics"
        studentStatsRepository.save(student1)

        and: "updated"
        teacherDashboard.update()
        
        then: "the number of students is correct"
        studentStats.getNumStudents() == 1

    }

    def "add another student and get the number of students"(){
        
        when:"a student statistics is created"
        student2 = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
        student2.addCourse(externalCourseExecution)
        userRepository.save(student2)
        def teacherDashboard = createTeacherDashboardAndPersist()
        def studentStats = createStudentStatsAndPersist(teacherDashboard)
            
        and: "a student is added to student statistics"
        studentStatsRepository.save(student2)

        and: "updated"
        teacherDashboard.update()
            
        then: "the number of students is correct"
        studentStats.getNumStudents() == 2
    }

    def "add a student and get the number of students with at least 75% correct answers"(){

        when:"a student statistics is created"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def studentStats = createStudentStatsAndPersist(teacherDashboard)
        
        and: "a student is added to student statistics"
        studentStatsRepository.save(student1)

        and: "three quizzes are  answered by the student"
        createAndAnswerQuizOne(student1, option)
        createAndAnswerQuizTwo(student1, option)
        createAndAnswerQuizThree(student1, option)

        and: "and the teacher dashboard is updated"
        teacherDashboard.update()

        then: "the number of students with at least 75% correct answers"
        studentStats.getNumStudents() == 1
        studentStats.getNumMore75CorrectQuestions() == 1
    }
    
    def "add a student with 75% or more correct answers and a student with less than 75% correct answers and get the number of students with at least 75% correct answers"(){

        when:"a student statistics is created"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def studentStats = createStudentStatsAndPersist(teacherDashboard)
        student2 = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
        student2.addCourse(externalCourseExecution)
        userRepository.save(student2)
        
        and: "a student is added to student statistics"
        studentStatsRepository.save(student1)
        studentStatsRepository.save(student2)

        and: "three quizzes are  answered by the student"
        createAndAnswerQuizOne(student1, option)
        createAndAnswerQuizTwo(student1, option)
        createAndAnswerQuizThree(student1, option)

        and: "three quizzes are  answered by the student"
        createAndAnswerQuizOne(student2,other_option)
        createAndAnswerQuizTwo(student2,other_option)
        createAndAnswerQuizThree(student2, option)

        and: "and the teacher dashboard is updated"
        teacherDashboard.update()

        then: "the number of students with at least 75% correct answers"
        studentStats.getNumStudents() == 2
        studentStats.getNumMore75CorrectQuestions() == 1
    }

    @Unroll
    def "add a student get the number of students with at least 3 quizzes"(){

        when:"a student statistics is created"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def studentStats = createStudentStatsAndPersist(teacherDashboard)
        
        and: "a student is added to student statistics"
        studentStatsRepository.save(student1)


        and: "three quizzes are  answered by the student"
        createAndAnswerQuizOne(student1, option)
        createAndAnswerQuizTwo(student1, option)
        createAndAnswerQuizThree(student1, option)


        and: "and the teacher dashboard is updated"
        teacherDashboard.update()

        then: "the number of students who did at least 3 quizzes is correct"
        studentStats.getNumStudents() == 1
        studentStats.getNumAtLeast3Quizzes() == 1
    }

    def "add two students and get the number of students with at least 3 quizzes"(){

        when:"a student statistics is created"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def studentStats = createStudentStatsAndPersist(teacherDashboard)
        student2 = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
        student2.addCourse(externalCourseExecution)
        userRepository.save(student2)
        
        and: "two student are added to student statistics"
        studentStatsRepository.save(student1)
        studentStatsRepository.save(student2)


        and: "three quizzes are  answered by a student"
        createAndAnswerQuizOne(student1, option)
        createAndAnswerQuizTwo(student1, option)
        createAndAnswerQuizThree(student1, option)

        and: "a quiz is answered by one student"
        createAndAnswerQuizOne(student2, option)

        and: "and the teacher dashboard is updated"
        teacherDashboard.update()

        then: "the number of students who did at least 3 quizzes is correct"
        studentStats.getNumStudents() == 2
        studentStats.getNumAtLeast3Quizzes() == 1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
 