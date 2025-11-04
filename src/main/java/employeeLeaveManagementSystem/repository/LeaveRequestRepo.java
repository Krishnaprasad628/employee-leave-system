package employeeLeaveManagementSystem.repository;

import employeeLeaveManagementSystem.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepo extends JpaRepository<LeaveRequest, Long> {

    @Query("""
        SELECT lr FROM LeaveRequest lr
        WHERE lr.employee.id = :empId
          AND lr.status = 'APPROVED'
          AND NOT (lr.endDate < :start OR lr.startDate > :end)
    """)
    List<LeaveRequest> findOverlappingApproved(Long empId, LocalDate start, LocalDate end);
}
