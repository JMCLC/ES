<template>
  <Bar
      :chart-options="chartOptions"
      :chart-data="chartData"
      :chart-id="chartId"
      :dataset-id-key="datasetIdKey"
      :plugins="plugins"
      :css-classes="cssClasses"
      :styles="styles"
      :width="width"
      :height="height"
  />
</template>

<script>
import { Bar } from 'vue-chartjs/legacy'

import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale
} from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

export default {
  name: 'BarChart',
  components: {
    Bar
  },
  props: {
    stat: {
      type: String
    },
    teacherDashboard: {
      type: Object
    },
    chartId: {
      type: String,
      default: 'bar-chart'
    },
    datasetIdKey: {
      type: String,
      default: 'label'
    },
    width: {
      type: Number,
      default: 400
    },
    height: {
      type: Number,
      default: 400
    },
    cssClasses: {
      default: '',
      type: String
    },
    styles: {
      type: Object,
      default: () => {}
    },
    plugins: {
      type: Array,
      default: () => []
    }
  },
  data() {

    let labels = [];
    let dataSets = [];
    if(this.stat === 'questions'){ 
      if(this.teacherDashboard.dashboardQuestionStats.length == 3) {
        labels = [
            this.teacherDashboard.dashboardQuestionStats[0].courseExecutionYear,
            this.teacherDashboard.dashboardQuestionStats[1].courseExecutionYear,
            this.teacherDashboard.dashboardQuestionStats[2].courseExecutionYear
        ];

      dataSets = [
          {
            label: 'Questions: Total Available',
            backgroundColor: '#297dfc',
            data: [
              this.teacherDashboard.dashboardQuestionStats[0].numAvailable,
              this.teacherDashboard.dashboardQuestionStats[1].numAvailable,
              this.teacherDashboard.dashboardQuestionStats[2].numAvailable
            ]
          },
          {
            label: 'Questions: Total Solved (Unique)',
            backgroundColor: '#ffc107',
            data: [
              this.teacherDashboard.dashboardQuestionStats[0].answeredQuestionUnique,
              this.teacherDashboard.dashboardQuestionStats[1].answeredQuestionUnique,
              this.teacherDashboard.dashboardQuestionStats[2].answeredQuestionUnique
            ]
          },
          {
            label: 'Questions Correctly Solved (Unique, Average per Student)',
            backgroundColor: '#28a745',
              data: [
                this.teacherDashboard.dashboardQuestionStats[0].averageQuestionsAnswered,
                this.teacherDashboard.dashboardQuestionStats[1].averageQuestionsAnswered,
                this.teacherDashboard.dashboardQuestionStats[2].averageQuestionsAnswered
              ]
          }
        ];
      } else if(this.teacherDashboard.dashboardQuestionStats.length == 2) {
        labels = [
          this.teacherDashboard.dashboardQuestionStats[0].courseExecutionYear,
          this.teacherDashboard.dashboardQuestionStats[1].courseExecutionYear,
        ];

        dataSets = [
          {
            label: 'Questions: Total Available',
            backgroundColor: '#297dfc',
            data: [
              this.teacherDashboard.dashboardQuestionStats[0].numAvailable,
              this.teacherDashboard.dashboardQuestionStats[1].numAvailable,
            ]
          },
          {
            label: 'Questions: Total Solved (Unique)',
            backgroundColor: '#ffc107',
            data: [
              this.teacherDashboard.dashboardQuestionStats[0].answeredQuestionUnique,
              this.teacherDashboard.dashboardQuestionStats[1].answeredQuestionUnique,
            ]
          },
          {
            label: 'Questions Correctly Solved (Unique, Average per Student)',
            backgroundColor: '#28a745',
            data: [
              this.teacherDashboard.dashboardQuestionStats[0].averageQuestionsAnswered,
              this.teacherDashboard.dashboardQuestionStats[1].averageQuestionsAnswered,
            ]
          }
        ];
      } else if(this.teacherDashboard.dashboardQuestionStats.length == 1) {
        // Nothing
      }
    } else if(this.stat === 'students'){
      if(this.teacherDashboard.dashboardStudentStats.length == 3) {
        labels = [
            this.teacherDashboard.dashboardStudentStats[0].courseExecutionYear,
            this.teacherDashboard.dashboardStudentStats[1].courseExecutionYear,
            this.teacherDashboard.dashboardStudentStats[2].courseExecutionYear,
        ];

        dataSets = [
          {
            label: 'Total Number of Students',
            backgroundColor: '#297dfc',
            data: [
              this.teacherDashboard.dashboardStudentStats[0].numStudents,
              this.teacherDashboard.dashboardStudentStats[1].numStudents,
              this.teacherDashboard.dashboardStudentStats[2].numStudents,
            ]
          },
          {
            label: 'Number of Students who Solved >= 75% Questions',
            backgroundColor: '#ffc107',
            data: [
              this.teacherDashboard.dashboardStudentStats[0].numMore75CorrectQuestions,
              this.teacherDashboard.dashboardStudentStats[1].numMore75CorrectQuestions,
              this.teacherDashboard.dashboardStudentStats[2].numMore75CorrectQuestions,
            ]
          },
          {
            label: 'Number of Students who Solved >= 3 Quizzes',
            backgroundColor: '#28a745',
            data: [
              this.teacherDashboard.dashboardStudentStats[0].numAtLeast3Quizzes,
              this.teacherDashboard.dashboardStudentStats[1].numAtLeast3Quizzes,
              this.teacherDashboard.dashboardStudentStats[2].numAtLeast3Quizzes,
            ]
          }
        ];
      } else if(this.teacherDashboard.dashboardStudentStats.length == 2) {
        labels = [
          this.teacherDashboard.dashboardStudentStats[0].courseExecutionYear,
          this.teacherDashboard.dashboardStudentStats[1].courseExecutionYear,
        ];

        dataSets = [
          {
            label: 'Total Number of Students',
            backgroundColor: '#297dfc',
            data: [
              this.teacherDashboard.dashboardStudentStats[0].numStudents,
              this.teacherDashboard.dashboardStudentStats[1].numStudents,
            ]
          },
          {
            label: 'Number of Students who Solved >= 75% Questions',
            backgroundColor: '#ffc107',
            data: [
              this.teacherDashboard.dashboardStudentStats[0].numMore75CorrectQuestions,
              this.teacherDashboard.dashboardStudentStats[1].numMore75CorrectQuestions,
            ]
          },
          {
            label: 'Number of Students who Solved >= 3 Quizzes',
            backgroundColor: '#28a745',
            data: [
              this.teacherDashboard.dashboardStudentStats[0].numAtLeast3Quizzes,
              this.teacherDashboard.dashboardStudentStats[1].numAtLeast3Quizzes,
            ]
          }
        ];
    } else if(this.teacherDashboard.dashboardQuestionStats.length == 1) {
      // Nothing
    }
    }
    return {
      chartData: {
        labels: labels,
        datasets: dataSets
      },
      chartOptions: {
        responsive: true,
        maintainAspectRatio: false
      }
    };
  }
}
</script>
