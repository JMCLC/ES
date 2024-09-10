package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats
import spock.lang.Unroll

@DataJpaTest
class RemoveTeacherDashboardTest extends SpockTest {
    def teacher
    def externalCourseExecution1
    def externalCourseExecution2

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)
    }

    def createTeacherDashboard() {
        def dashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(dashboard)
        return dashboard
    }

    def createDashboard() {
        def dashboard = new TeacherDashboard(externalCourseExecution, teacher)
        def questionStats = new QuestionStats(dashboard, externalCourseExecution)
        questionStatsRepository.save(questionStats)
        def quizStats = new QuizStats(dashboard, externalCourseExecution)
        quizStatsRepository.save(quizStats)
        def studentStats = new StudentStats(dashboard, externalCourseExecution)
        studentStatsRepository.save(studentStats)

        teacherDashboardRepository.save(dashboard)
        dashboard.addQuestionStats(questionStats)
        dashboard.addQuizStats(quizStats)
        dashboard.addStudentStats(studentStats)
        return dashboard
    }

    def createExternalCourseExecutions() {
        def course = new Course(COURSE_1_NAME,Course.Type.TECNICO)
        courseRepository.save(course)
        externalCourseExecution1 = new CourseExecution(course, COURSE_1_ACRONYM,COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_BEFORE)
        courseExecutionRepository.save(externalCourseExecution1)

        externalCourseExecution2 = new CourseExecution(course, COURSE_2_ACRONYM,COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_YESTERDAY)
        courseExecutionRepository.save(externalCourseExecution2)
    }

    def "remove a dashboard"() {
        given: "a dashboard"
        def dashboard = createTeacherDashboard()

        when: "the user removes the dashboard"
        teacherDashboardService.removeTeacherDashboard(dashboard.getId())

        then: "the dashboard is removed"
        teacherDashboardRepository.findAll().size() == 0L
        teacher.getDashboards().size() == 0
    }

    def "cannot remove a dashboard twice"() {
        given: "a removed dashboard"
        def dashboard = createTeacherDashboard()
        teacherDashboardService.removeTeacherDashboard(dashboard.getId())

        when: "the dashboard is removed for the second time"
        teacherDashboardService.removeTeacherDashboard(dashboard.getId())

        then: "an exception is thrown"        
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DASHBOARD_NOT_FOUND
    }

    @Unroll
    def "cannot remove a dashboard that doesn't exist with the dashboardId=#dashboardId"() {
        when: "an incorrect dashboard id is removed"
        teacherDashboardService.removeTeacherDashboard(dashboardId)

        then: "an exception is thrown"        
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DASHBOARD_NOT_FOUND

        where:
        dashboardId << [null, 10, -1]
    }

    def "create a dashboard with statistics and remove it"() {
        given: "A dashboard"
        def dashboard = createDashboard()

        when: "the user removes the dashboard"
        teacherDashboardService.removeTeacherDashboard(dashboard.getId())

        then: "the statistics are removed"
        teacherDashboardRepository.count() == 0L
        questionStatsRepository.count() == 0L
        quizStatsRepository.count() == 0L
        studentStatsRepository.count() == 0L
    }

   def "create two teachers dashboards and remove one of them and guarantee that student statistics are removed"() {
        def teacher1 = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher1)
        
        given: "a previous course execution"
        createExternalCourseExecutions()
        
        and: "a teacher in a course execution"
        teacher1.addCourse(externalCourseExecution2)

        and: "a teacher dashboard is created"
        def dashboard = createTeacherDashboard()
        def other_dashboard = createTeacherDashboard()

        when: "a teacher dashboard is removed"
        teacherDashboardService.removeTeacherDashboard(other_dashboard.getId())

        then: "the dashboard is removed"
        teacherDashboardRepository.findAll().size() == 1L
        teacher.getDashboards().size() == 1

        and: "the  student statistics are removed"
        dashboard.getStudentStats() != null
        other_dashboard.getStudentStats() == null
    }

   def "create two teachers dashboards and remove one of them and guarantee that quiz statistics are removed"() {
        def teacher1 = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher1)
        
        given: "a previous course execution"
        createExternalCourseExecutions()
        
        and: "a teacher in a course execution"
        teacher1.addCourse(externalCourseExecution2)

        and: "a teacher dashboard is created"
        def dashboard = createTeacherDashboard()
        def other_dashboard = createTeacherDashboard()

        when: "a teacher dashboard is removed"
        teacherDashboardService.removeTeacherDashboard(other_dashboard.getId())

        then: "the dashboard is removed"
        teacherDashboardRepository.findAll().size() == 1L
        teacher.getDashboards().size() == 1

        and: "the quiz statistics are removed"
        dashboard.getQuizStats() != null
        other_dashboard.getQuizStats() == null
    }

   def "create two teachers dashboards and remove one of them and guarantee that question statistics are removed"() {
        def teacher1 = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher1)
        
        given: "a previous course execution"
        createExternalCourseExecutions()
        
        and: "a teacher in a course execution"
        teacher1.addCourse(externalCourseExecution2)

        and: "a teacher dashboard is created"
        def dashboard = createTeacherDashboard()
        def other_dashboard = createTeacherDashboard()

        when: "a teacher dashboard is removed"
        teacherDashboardService.removeTeacherDashboard(other_dashboard.getId())

        then: "the dashboard is removed"
        teacherDashboardRepository.findAll().size() == 1L
        teacher.getDashboards().size() == 1

        and: "the question statistics are removed"
        dashboard.getQuestion() != null
        other_dashboard.getQuestion() == null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
