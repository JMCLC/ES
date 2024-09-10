package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.QuestionStatsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.QuizStatsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.StudentStatsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import spock.lang.Unroll

@DataJpaTest
class CreateTeacherDashboardTest extends SpockTest {
    def teacher
    def teacherDashboard
    def externalCourseExecution1
    def externalCourseExecution2
    def externalCourseExecution3

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)

    }
    
    def createExternalCourseExecutions() {
        def course = new Course(COURSE_1_NAME,Course.Type.TECNICO)
        courseRepository.save(course)
        externalCourseExecution1 = new CourseExecution(course, COURSE_1_ACRONYM,COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_BEFORE)
        courseExecutionRepository.save(externalCourseExecution1)

        externalCourseExecution2 = new CourseExecution(course, COURSE_2_ACRONYM,COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_YESTERDAY)
        courseExecutionRepository.save(externalCourseExecution2)
    }
        
    def createOneExtraCourseExecution() {
        def course = new Course(COURSE_1_NAME,Course.Type.TECNICO)
        courseRepository.save(course)
        externalCourseExecution1 = new CourseExecution(course, COURSE_1_ACRONYM,COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_BEFORE)
        courseExecutionRepository.save(externalCourseExecution1)

        externalCourseExecution2 = new CourseExecution(course, COURSE_2_ACRONYM,COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_YESTERDAY)
        courseExecutionRepository.save(externalCourseExecution2)

        externalCourseExecution3 = new CourseExecution(course, COURSE_3_ACRONYM,COURSE_3_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(externalCourseExecution3)
    }

    def "create an empty dashboard"() {
        given: "a teacher in a course execution"
        teacher.addCourse(externalCourseExecution)

        when: "a dashboard is created"
        teacherDashboardService.getTeacherDashboard(externalCourseExecution.getId(), teacher.getId())

        then: "an empty dashboard is created"
        teacherDashboardRepository.count() == 1L
        def result = teacherDashboardRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacher().getId() == teacher.getId()

        and: "the teacher has a reference for the dashboard"
        teacher.getDashboards().size() == 1
        teacher.getDashboards().contains(result)
    }

    def "cannot create multiple dashboards for a teacher on a course execution"() {
        given: "a teacher in a course execution"
        teacher.addCourse(externalCourseExecution)

        and: "an empty dashboard for the teacher"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())

        when: "a second dashboard is created"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())

        then: "there is only one dashboard"
        teacherDashboardRepository.count() == 1L

        and: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TEACHER_ALREADY_HAS_DASHBOARD
    }

    def "cannot create a dashboard for a user that does not belong to the course execution"() {
        when: "a dashboard is created"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())

        then: "exception is thrown"        
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TEACHER_NO_COURSE_EXECUTION
    }

    @Unroll
    def "cannot create a dashboard with courseExecutionId=#courseExecutionId"() {
        when: "a dashboard is created"
        teacherDashboardService.createTeacherDashboard(courseExecutionId, teacher.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.COURSE_EXECUTION_NOT_FOUND

        where:
        courseExecutionId << [0, 100]
    }

    @Unroll
    def "cannot create a dashboard with teacherId=#teacherId"() {
        when: "a dashboard is created"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacherId)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND

        where:
        teacherId << [0, 100]
    }

    def "create three course executions and create a new dashboard"() {
        def externalCourse = new Course(COURSE_2_NAME, Course.Type.TECNICO)
        courseRepository.save(externalCourse)

        def externalCourseExecutionOne = new CourseExecution(externalCourse, COURSE_2_ACRONYM, "2020/2021", Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(externalCourseExecutionOne)
        teacher.addCourse(externalCourseExecutionOne)

        def externalCourseExecutionTwo = new CourseExecution(externalCourse, COURSE_2_ACRONYM, "2021/2022", Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(externalCourseExecutionTwo)
        teacher.addCourse(externalCourseExecutionTwo)

        def externalCourseExecutionThree = new CourseExecution(externalCourse, COURSE_2_ACRONYM, "2022/2023", Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(externalCourseExecutionThree)
        teacher.addCourse(externalCourseExecutionThree)

        when: "a dashboard is created"
        def teacherDashboardDTO = teacherDashboardService.createTeacherDashboard(externalCourseExecutionThree.getId(), teacher.getId())

        then: "an empty dashboard is created"
        def questionStatsList = teacherDashboardDTO.getDashboardQuestionStats()
        def quizStatsList = teacherDashboardDTO.getDashboardQuizStats()
        def studentStatsList = teacherDashboardDTO.getDashboardStudentStats()

        QuestionStatsDto qs = questionStatsList.get(0)
        qs.getCourseExecutionYear() == "2020/2021"
        QuizStatsDto qzs = quizStatsList.get(1)
        qzs.getCourseExecutionYear() == "2021/2022"
        StudentStatsDto ss = studentStatsList.get(2)
        ss.getCourseExecutionYear() == "2022/2023"
    }

   def "create a teacher dashboard and verify a past course execution"() {
        def teacher1 = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher1)
        
        given: "a previous course execution"
        createExternalCourseExecutions()
        
        and: "a teacher in a course execution"
        teacher1.addCourse(externalCourseExecution2)

        when: "a teacher dashboard is created"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution2.getId(), teacher1.getId())

        then:"an empty dashboard is created"
        teacherDashboardRepository.count() == 1L
        def result = teacherDashboardRepository.findAll().get(0)

        and: "the teacher has a reference for the dashboard"
        teacher1.getDashboards().size() == 1
        teacher1.getDashboards().contains(result)

        and: "the teacher has the student statistics from the last two executions"
        result.getStudentStats().size() == 2
        result.getStudentStats()[1].getCourseExecution().getId() == externalCourseExecution2.getId()
        result.getStudentStats()[0].getCourseExecution().getId() == externalCourseExecution1.getId()
    } 
    
   def "create a teacher dashboard and verify two past course executions"() {
        def teacher1 = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher1)
        
        given: "a previous course execution"
        createOneExtraCourseExecution()
        
        and: "a teacher in a course execution"
        teacher1.addCourse(externalCourseExecution3)

        when: "a teacher dashboard is created"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution3.getId(), teacher1.getId())

        then:"an empty dashboard is created"
        teacherDashboardRepository.count() == 1L
        def result = teacherDashboardRepository.findAll().get(0)

        and: "the teacher has a reference for the dashboard"
        teacher1.getDashboards().size() == 1
        teacher1.getDashboards().contains(result)

        and: "the teacher has the student statistics from the last three executions"
        result.getStudentStats().size() == 3
        result.getStudentStats()[2].getCourseExecution().getId() == externalCourseExecution3.getId()
        result.getStudentStats()[1].getCourseExecution().getId() == externalCourseExecution2.getId()
        result.getStudentStats()[0].getCourseExecution().getId() == externalCourseExecution1.getId()
    } 
    
    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}




