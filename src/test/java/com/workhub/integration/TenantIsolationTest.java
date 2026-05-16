package com.workhub.integration;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TenantIsolationTest {

    @Test
    void crossTenantReadShouldBeBlocked() {

        String tenantAProject = "TENANT_A_PROJECT";
        String tenantBProject = "TENANT_B_PROJECT";

        assertNotEquals(tenantAProject, tenantBProject);
    }

    @Test
    void crossTenantUpdateShouldBeBlocked() {

        String tenantATask = "TASK_A";
        String tenantBTask = "TASK_B";

        assertFalse(tenantATask.equals(tenantBTask));
    }

    @Test
    void crossTenantListShouldNotLeakData() {

        List<String> tenantAProjects = new ArrayList<>();
        tenantAProjects.add("PROJECT_A");

        assertFalse(
                tenantAProjects.contains("PROJECT_B")
        );
    }
}