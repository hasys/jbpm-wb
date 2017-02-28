/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workbench.wi.backend.server.cases.service;

import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementProvisioningServiceTest {

    @Mock
    private CaseManagementProvisioningExecutor executor;

    @Mock
    private CaseManagementProvisioningSettings settings;

    @InjectMocks
    private CaseManagementProvisioningService service;

    @Test
    public void testProvisioningDisabled() {
        when(settings.isProvisioningEnabled()).thenReturn(false);

        service.init();

        verify(executor, never()).execute(any(PipelineExecutor.class), any(Pipeline.class), any(Input.class));
    }

    @Test
    public void testProvisioningUsingWarPath() {
        final String path = "path/to/file.war";
        when(settings.isProvisioningEnabled()).thenReturn(true);
        when(settings.isDeployFromLocalPath()).thenReturn(true);
        when(settings.getPath()).thenReturn(path);

        service.init();

        ArgumentCaptor<Input> captor = ArgumentCaptor.forClass(Input.class);
        verify(executor).execute(any(PipelineExecutor.class), any(Pipeline.class), captor.capture());
        assertEquals(path, captor.getValue().get("war-path"));
    }

    @Test
    public void testProvisioningUsingMaven() {
        final String gav = "org.jbpm:jbpm-wb-case-mgmt-showcase:war:1.0.0";
        when(settings.isProvisioningEnabled()).thenReturn(true);
        when(settings.isDeployFromLocalPath()).thenReturn(false);
        when(settings.getGAV()).thenReturn(gav);

        service.init();

        ArgumentCaptor<Input> captor = ArgumentCaptor.forClass(Input.class);
        verify(executor).execute(any(PipelineExecutor.class), any(Pipeline.class), captor.capture());
        assertEquals(gav, captor.getValue().get("artifact"));
    }

}
