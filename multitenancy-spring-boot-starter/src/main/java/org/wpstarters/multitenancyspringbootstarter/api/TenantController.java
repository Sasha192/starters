package org.wpstarters.multitenancyspringbootstarter.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.wpstarters.multitenancyspringbootstarter.Tenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.domain.TenantManagementService;

import java.util.UUID;

@Controller
@RequestMapping("/tenants-controller/")
public class TenantController {

    @Autowired
    private TenantManagementService tenantManagementService;

    @PostMapping("/create")
    public ResponseEntity<? extends Tenant<UUID>> createTenant() {
        Tenant<UUID> tenant = this.tenantManagementService.createTenant();
        return new ResponseEntity<>(tenant, HttpStatus.OK);
    }
}
