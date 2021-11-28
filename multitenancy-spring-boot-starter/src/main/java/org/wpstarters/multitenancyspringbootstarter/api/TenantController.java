package org.wpstarters.multitenancyspringbootstarter.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.wpstarters.commonwebstarter.Tenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.ITenantManagementService;

@Controller
@RequestMapping("/tenants-controller/")
public class TenantController {

    @Autowired
    private ITenantManagementService<?> schemaTenantManagementService;

    @PostMapping("/create")
    public ResponseEntity<? extends Tenant<?>> createTenant() {
        Tenant<?> tenant = this.schemaTenantManagementService.createTenant();
        return new ResponseEntity<>(tenant, HttpStatus.OK);
    }
}
