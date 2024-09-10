export default class QuestionStats {
    id!: number;
    numAvailable!: number;
    answeredQuestionUnique!: number;
    averageQuestionsAnswered!: number;
    courseExecutionYear!: string;

    constructor(jsonObj?: QuestionStats) {
        if(jsonObj) {
            this.id = jsonObj.id;
            this.numAvailable = jsonObj.numAvailable;
            this.answeredQuestionUnique = jsonObj.answeredQuestionUnique;
            this.averageQuestionsAnswered = jsonObj.averageQuestionsAnswered;
            this.courseExecutionYear = jsonObj.courseExecutionYear;
        }
    }
}