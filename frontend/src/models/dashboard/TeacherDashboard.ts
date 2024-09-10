import QuestionStats from '@/models/dashboard/QuestionStats';
import StudentStatistics from '@/models/dashboard/StudentStatistics';

export default class TeacherDashboard {
  id!: number;
  numberOfStudents!: number;

  dashboardQuestionStats: QuestionStats[] = [];
  dashboardStudentStats: StudentStatistics[] = [];

  constructor(jsonObj?: TeacherDashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.numberOfStudents = jsonObj.numberOfStudents;
      if (jsonObj.dashboardQuestionStats) {
        this.dashboardQuestionStats = jsonObj.dashboardQuestionStats.map(
            (qs: QuestionStats) => new QuestionStats(qs)
        );
      }
      if (jsonObj.dashboardStudentStats) {
        this.dashboardStudentStats = jsonObj.dashboardStudentStats.map(
            (ss: StudentStatistics) => new StudentStatistics(ss)
        );
      }
    }
  }
}
