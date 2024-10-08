package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.TeacherDashboardDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.services.TeacherDashboardService;

import java.security.Principal;

@RestController
public class TeacherDashboardController {
    @Autowired
    private TeacherDashboardService teacherDashboardService;

    TeacherDashboardController(TeacherDashboardService teacherDashboardService) {
        this.teacherDashboardService = teacherDashboardService;
    }

    @GetMapping("/teachers/dashboards/executions/{courseExecutionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS')")
    public TeacherDashboardDto getTeacherDashboard(Principal principal, @PathVariable int courseExecutionId) {
        int teacherId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();

        return teacherDashboardService.getTeacherDashboard(courseExecutionId, teacherId);
    }

    @GetMapping("/teachers/dashboards/{dashboardId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#dashboardId, 'TEACHER_DASHBOARD.ACCESS')")
    public void updateTeacherDashboard(@PathVariable Integer dashboardId) {
        teacherDashboardService.updateTeacherDashboard(dashboardId);
    }

    @DeleteMapping("/teachers/dashboards/{dashboardId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#dashboardId, 'TEACHER_DASHBOARD.ACCESS')")
    public void removeTeacherDashboard(@PathVariable Integer dashboardId) {
        teacherDashboardService.removeTeacherDashboard(dashboardId);
    }
    
    @GetMapping("/admin/dashboards")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateAllTeacherDashboards() {
        teacherDashboardService.updateAllTeacherDashboards();
    }
}
