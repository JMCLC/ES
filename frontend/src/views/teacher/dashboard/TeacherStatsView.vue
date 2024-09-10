<template>
  <div class="container">
    <div class="update-button">
      <button @click="updateDashboard" style="background-color: #297dfc; color: white; border: none; margin: 20px; font-size: 1.2rem; padding: 10px 20px; border-radius: 20px; padding: 10px 20px;">Update Dashboard</button>
    </div>
    <h2>Statistics for this course execution</h2>
    <div v-if="teacherDashboard != null" class="stats-container">
      <div class="items">
        <div data-cy="number-students" ref="totalStudents" class="icon-wrapper">
          <animated-number :number="teacherDashboard.dashboardStudentStats.slice(-1)[0].numStudents" />
        </div>
        <div class="project-name">
          <p>Number of Students</p>
        </div>
      </div>
      <div class="items">
        <div data-cy="students-solved-75%-questions" ref="numMore75CorrectQuestions" class="icon-wrapper">
          <animated-number :number="teacherDashboard.dashboardStudentStats.slice(-1)[0].numMore75CorrectQuestions" />
        </div>
        <div class="project-name">
          <p>Number of Students who Solved >= 75% Questions</p>
        </div>
      </div>
      <div class="items">
        <div data-cy="students-solved-at-least-3-quizzes" ref="numAtLeast3Quizzes" class="icon-wrapper">
          <animated-number :number="teacherDashboard.dashboardStudentStats.slice(-1)[0].numAtLeast3Quizzes" />
        </div>
        <div class="project-name">
          <p>Number of Students who Solved >= 3 Quizzes</p>
        </div>
      </div>
      <div class="items">
          <div data-cy="num-questions-available" ref="numQuestionsAvailable" class="icon-wrapper">
          <animated-number :number="teacherDashboard.dashboardQuestionStats.slice(-1)[0].numAvailable" />
        </div>
        <div class="project-name">
          <p>Number of Questions</p>
        </div>
      </div>
      <div class="items">
        <div data-cy="answered-question-unique" ref="answeredQuestionUnique" class="icon-wrapper">
          <animated-number :number="teacherDashboard.dashboardQuestionStats.slice(-1)[0].answeredQuestionUnique" />
        </div>
        <div class="project-name">
          <p>Number of Questions Solved (Unique)</p>
        </div>
      </div>
      <div class="items">
        <div data-cy="average-question-unique" ref="averageQuestionUnique" class="icon-wrapper">
          <animated-number :number="teacherDashboard.dashboardQuestionStats.slice(-1)[0].averageQuestionsAnswered" />
        </div>
        <div class="project-name">
          <p>Number of Questions Correctly Solved (Unique, Average Per Student)</p>
        </div>
      </div>
    </div>
    <h2 v-if="teacherDashboard != null && (teacherDashboard.dashboardQuestionStats.length > 1 || teacherDashboard.dashboardStudentStats.length > 1)">Comparison with previous course executions</h2>
    <div v-if="teacherDashboard != null" class="stats-container">
      <div class="items">
          <BarChart data-cy="Bar-Chart" v-if="teacherDashboard != null && teacherDashboard.dashboardQuestionStats.length > 1" :stat="'questions'" :teacherDashboard="teacherDashboard" style="background-color: white"/>
      </div>
      <div class="items">
        <BarChart data-cy="Student-Chart" v-if="teacherDashboard != null && teacherDashboard.dashboardStudentStats.length > 1" :stat="'students'" :teacherDashboard="teacherDashboard" style="background-color: white"/>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import TeacherDashboard from '@/models/dashboard/TeacherDashboard';
import BarChart from './Bar.vue';

@Component({
  components: { AnimatedNumber, BarChart },
})

export default class TeacherStatsView extends Vue {
  @Prop() readonly dashboardId!: number;
  teacherDashboard: TeacherDashboard | null = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.teacherDashboard = await RemoteServices.getTeacherDashboard();
      this.teacherDashboard.dashboardQuestionStats.sort((a, b) => a.courseExecutionYear.localeCompare(b.courseExecutionYear));
      this.teacherDashboard.dashboardStudentStats.sort((a, b) => a.courseExecutionYear.localeCompare(b.courseExecutionYear));
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async updateDashboard() {
    try {
      await RemoteServices.updateTeacherDashboard(this.dashboardId);
      this.teacherDashboard = await RemoteServices.getTeacherDashboard();
      this.teacherDashboard.dashboardQuestionStats.sort((a, b) => a.courseExecutionYear.localeCompare(b.courseExecutionYear));
      this.teacherDashboard.dashboardStudentStats.sort((a, b) => a.courseExecutionYear.localeCompare(b.courseExecutionYear));
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}


</script>

<style lang="scss" scoped>
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #1976d2;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }

  .bar-chart {
    background-color: rgba(255, 255, 255, 0.90);
    height: 400px;
  }
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}

.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }

  & .icon-wrapper i {
    transform: translateY(5px);
  }
}
</style>