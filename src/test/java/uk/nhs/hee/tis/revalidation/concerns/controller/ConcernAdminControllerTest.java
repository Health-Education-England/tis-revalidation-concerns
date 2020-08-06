/*
 * The MIT License (MIT)
 *
 * Copyright 2020 Crown Copyright (Health Education England)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.nhs.hee.tis.revalidation.concerns.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernAdminDto;
import uk.nhs.hee.tis.revalidation.concerns.service.ConcernAdminService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ConcernAdminController.class)
class ConcernAdminControllerTest {

  private final MockMvc mockMvc;

  @MockBean
  private ConcernAdminService service;

  @Autowired
  ConcernAdminControllerTest(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  @Test
  void shouldReturnEmptyListWhenNoAssignableAdminsFound() throws Exception {
    when(service.getAssignableAdmins()).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/api/concerns/admins"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));
  }

  @Test
  void shouldReturnAdminsWhenAssignableAdminsFound() throws Exception {
    ConcernAdminDto admin1 = new ConcernAdminDto();
    admin1.setUsername("admin1");
    admin1.setFullName("Admin One");

    ConcernAdminDto admin2 = new ConcernAdminDto();
    admin2.setUsername("admin2");
    admin2.setFullName("Admin Two");

    when(service.getAssignableAdmins()).thenReturn(Arrays.asList(admin1, admin2));

    mockMvc.perform(get("/api/concerns/admins"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").value("admin1"))
        .andExpect(jsonPath("$[0].fullName").value("Admin One"))
        .andExpect(jsonPath("$[1].username").value("admin2"))
        .andExpect(jsonPath("$[1].fullName").value("Admin Two"));
  }
}
