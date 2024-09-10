describe('TeacherDashboard', () => {

    beforeEach( () => {

        cy.request('http://localhost:8080/auth/demo/teacher')
            .as('loginResponse')
            .then((response) => {
                Cypress.env('token', response.body.token);
                return response;
            });

        cy.updateDemoCourseAcademicTerm('2018/2019');

        cy.createCourseExecutionOnDemoCourse('2021/2022');

        cy.changeDemoTeacherCourseExecutionMatchingAcademicTerm('2021/2022');

        cy.request('http://localhost:8080/auth/demo/teacher')
            .as('loginResponse')
            .then((response) => {
                Cypress.env('token', response.body.token);
                return response;
            });

    });

    afterEach( () => {

        cy.deleteStats();
        cy.changeDemoTeacherCourseExecutionMatchingAcademicTerm('2018/2019');
        cy.deleteCourseExecutions('2021/2022')
        cy.updateDemoCourseAcademicTerm('2019/2020');
    });

    it('teacher access dashboard', () => {

        cy.intercept('GET', '**/teachers/dashboards/executions/*').as(
            'getDashboard'
        );

        cy.demoTeacherLogin();

        cy.get('[data-cy="dashboardMenuButton"]').click();
        cy.wait('@getDashboard');

        cy.contains('Logout').click();

        cy.addStudentStats(250, 0, 290, 1);

        cy.addStudentStats(315, 0, 320, 2);

        cy.demoTeacherLogin();

        cy.get('[data-cy="dashboardMenuButton"]').click();
        cy.wait(3000);

        cy.get('[data-cy=number-students]')
            .should('have.text', '320')
        //
        cy.get('[data-cy=students-solved-at-least-3-quizzes]')
            .should('have.text', '315')
        //

        // TAKE SCREENSHOT
        // cy.get('[data-cy=Bar-Chart]').first().scrollIntoView().wait(5000).screenshot('./base-screenshots/expected/questionBarChart')
        
        cy.get('[data-cy=Student-Chart]').first().scrollIntoView().wait(5000).screenshot('./base-screenshots/result/studentBarChart')
        

        // PNGJS lets me load the picture from disk
        const PNG = require('pngjs').PNG;
        // pixelmatch library will handle comparison
        const pixelmatch = require('pixelmatch');

        cy.readFile(
            './tests/e2e/specs/dashboard/base-screenshots/studentBarChart.png', 'base64'
        ).then(baseImage => {
            cy.readFile(
                './tests/e2e/screenshots/dashboard/checkTeacherDashboard.js/base-screenshots/result/studentBarChart.png', 'base64'
            ).then(chartImage => {
                // load both pictures
                const img1 = PNG.sync.read(Buffer.from(baseImage, 'base64'));
                const img2 = PNG.sync.read(Buffer.from(chartImage, 'base64'));

                const { width, height } = img1;
                const diff = new PNG({ width, height });

                // calling pixelmatch return how many pixels are different
                const numDiffPixels = pixelmatch(img1.data, img2.data, diff.data, width, height, { threshold: 0.1 });

                // calculating a percent diff
                const diffPercent = (numDiffPixels / (width * height) * 100);

                cy.task('log', `Found a ${diffPercent.toFixed(2)}% pixel difference`);
                cy.log(`Found a ${diffPercent.toFixed(2)}% pixel difference`);
                //cy.writeFile('diff.png', PNG.sync.write(diff));

                expect(diffPercent).to.be.below(40);
            });
        });

        cy.contains('Logout').click();
    });

});
