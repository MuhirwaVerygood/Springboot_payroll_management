package com.gov.rw.payroll.audits;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audits")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping("/get-all")
    public List<AuditLog> getAllLogs() {
        return auditLogService.getAllLogs();
    }

}
