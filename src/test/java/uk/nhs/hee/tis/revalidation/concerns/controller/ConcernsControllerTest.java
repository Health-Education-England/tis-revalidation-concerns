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

import static java.time.LocalDate.now;
import static java.util.List.of;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.nhs.hee.tis.revalidation.concerns.controller.ConcernsController.ASC;
import static uk.nhs.hee.tis.revalidation.concerns.controller.ConcernsController.DATE_RAISED;
import static uk.nhs.hee.tis.revalidation.concerns.controller.ConcernsController.DESC;
import static uk.nhs.hee.tis.revalidation.concerns.controller.ConcernsController.EMPTY_STRING;
import static uk.nhs.hee.tis.revalidation.concerns.controller.ConcernsController.PAGE_NUMBER;
import static uk.nhs.hee.tis.revalidation.concerns.controller.ConcernsController.PAGE_NUMBER_VALUE;
import static uk.nhs.hee.tis.revalidation.concerns.controller.ConcernsController.SEARCH_QUERY;
import static uk.nhs.hee.tis.revalidation.concerns.controller.ConcernsController.SORT_COLUMN;
import static uk.nhs.hee.tis.revalidation.concerns.controller.ConcernsController.SORT_ORDER;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernTraineeDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsRequestDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsSummaryDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ReferenceDto;
import uk.nhs.hee.tis.revalidation.concerns.entity.Concern;
import uk.nhs.hee.tis.revalidation.concerns.entity.ConcernStatus;
import uk.nhs.hee.tis.revalidation.concerns.service.ConcernsService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ConcernsController.class)
public class ConcernsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private ConcernsService concernsService;

  @Mock
  private Concern concern;

  private final Faker faker = new Faker();
  private String gmcRef1, gmcRef2;
  private String concernId;
  private String firstName1, firstName2;
  private String lastName1, lastName2;
  private String programme1, programme2;
  private String concernsStatus1, concernsStatus2;
  private LocalDate dateRaised1, dateRaised2;
  private String type1, type2;
  private long typeId1, typeId2;
  private String site1, site2;
  private long siteId1, siteId2;
  private String source1, source2;
  private long sourceId1, sourceId2;
  private ConcernStatus status1, status2;
  private String admin1, admin2;
  private String comment;
  private long employerId;
  private String employer;
  private String grade;
  private long gradeId;
  private LocalDate dateReported;
  private LocalDate lastUpdatedDate;
  private LocalDate followUpDate1, followUpDate2;
  private LocalDate closedDate1, closedDate2;
  private LocalDate dateAdded1, dateAdded2;

  @BeforeEach
  public void setup() {
    gmcRef1 = faker.number().digits(8);
    gmcRef2 = faker.number().digits(8);
    concernId = faker.number().digits(5);
    firstName1 = faker.name().firstName();
    firstName2 = faker.name().firstName();
    lastName1 = faker.name().lastName();
    lastName2 = faker.name().lastName();
    programme1 = "Alternative Medicine";
    programme2 = "Accident and Emergency";
    concernsStatus1 = "Open";
    concernsStatus2 = "Closed";
    dateRaised1 = now().minusDays(1);
    dateRaised2 = now();
    dateAdded1 = now().minusDays(5);
    dateAdded2 = now().minusDays(5);
    dateReported = now();
    type1 = faker.lorem().characters(4);
    type2 = faker.lorem().characters(4);
    typeId1 = faker.number().randomNumber();
    typeId2 = faker.number().randomNumber();
    site1 = faker.lorem().characters(5);
    site2 = faker.lorem().characters(5);
    siteId1 = faker.number().randomNumber();
    siteId2 = faker.number().randomNumber();
    source1 = faker.lorem().characters(5);
    source2 = faker.lorem().characters(5);
    sourceId1 = faker.number().randomNumber();
    sourceId2 = faker.number().randomNumber();
    status1 = faker.options().option(ConcernStatus.class);
    status2 = faker.options().option(ConcernStatus.class);
    admin1 = faker.lorem().characters(5);
    admin2 = faker.lorem().characters(5);
    employerId = faker.number().randomNumber();
    employer = "Royal London Hospital Trust";
    comment = "This is a test comment";
    grade = "Academic Clinical Fellow";
    gradeId = faker.number().randomNumber();
    lastUpdatedDate = now().minusDays(5);
    followUpDate1 = now().plusDays(6);
    followUpDate2 = now().plusDays(6);
    closedDate1 = now().plusMonths(7);
    closedDate2 = now().plusMonths(7);
    dateAdded1 = now().minusDays(10);
    dateAdded2 = now().minusDays(10);
  }

  @Test
  public void shouldReturnTraineeConcernsInformation() throws Exception {
    final var doctorConcernsSummaryDto = prepareDoctorConcernsSummary();
    final var requestDto = ConcernsRequestDto.builder().sortOrder(ASC).sortColumn(DATE_RAISED)
        .searchQuery(EMPTY_STRING).build();
    when(concernsService.getConcernsSummary(requestDto)).thenReturn(doctorConcernsSummaryDto);
    this.mockMvc.perform(get("/api/concerns")
        .param(SORT_ORDER, ASC)
        .param(SORT_COLUMN, DATE_RAISED)
        .param(PAGE_NUMBER, PAGE_NUMBER_VALUE)
        .param(SEARCH_QUERY, EMPTY_STRING))
        .andExpect(status().isOk())
        .andExpect(content().json(mapper.writeValueAsString(doctorConcernsSummaryDto)));

  }

  @Test
  public void shouldReturnDataWhenEndpointIsCalledWithoutParameters() throws Exception {
    final var doctorConcernsSummaryDto = prepareDoctorConcernsSummary();
    final var requestDTO = ConcernsRequestDto.builder().sortOrder(DESC).sortColumn(DATE_RAISED)
        .searchQuery(EMPTY_STRING).build();
    when(concernsService.getConcernsSummary(requestDTO)).thenReturn(doctorConcernsSummaryDto);
    this.mockMvc.perform(get("/api/concerns"))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().json(mapper.writeValueAsString(doctorConcernsSummaryDto)));
  }

  @Test
  public void shouldNotFailAppWhenResponseIsEmpty() throws Exception {
    final ConcernsSummaryDto doctorConcernsSummaryDto = new ConcernsSummaryDto();
    final var requestDTO = ConcernsRequestDto.builder().sortOrder(DESC).sortColumn(DATE_RAISED)
        .searchQuery(EMPTY_STRING).build();
    when(concernsService.getConcernsSummary(requestDTO)).thenReturn(doctorConcernsSummaryDto);
    this.mockMvc.perform(get("/api/concerns"))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().json(mapper.writeValueAsString(doctorConcernsSummaryDto)));
  }

  @Test
  public void shouldReturnAllConcernsForADoctor() throws Exception {
    final var detailedConcernDto = List.of(prepareConcernsRecordDto());
    when(concernsService.getTraineeConcernsInfo(gmcRef1)).thenReturn(detailedConcernDto);
    this.mockMvc.perform(get("/api/concerns/{gmcId}", gmcRef1))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().json(mapper.writeValueAsString(detailedConcernDto)));
  }

  @Test
  public void shouldNotFailWhenThereIsNoConcernsForADoctor() throws Exception {
    final var detailedConcernDto = List.of(prepareConcernsRecordDto());
    when(concernsService.getTraineeConcernsInfo(gmcRef1)).thenReturn(detailedConcernDto);
    this.mockMvc.perform(get("/api/concerns/{gmcId}", gmcRef1))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().json(mapper.writeValueAsString(detailedConcernDto)));
  }

  @Test
  public void shouldSaveConcern() throws Exception {

    final var concernsDto = ConcernsDto.builder()
        .gmcNumber("110000")
        .dateOfIncident(now())
        .concernType(ReferenceDto.builder().id(1).label("concernType").build())
        .source(ReferenceDto.builder().id(1).label("source").build())
        .employer(ReferenceDto.builder().id(1).label("employer").build())
        .site(ReferenceDto.builder().id(1).label("site").build())
        .grade(ReferenceDto.builder().id(1).label("grade").build())
        .dateReported(now())
        .status(ConcernStatus.OPEN.name())
        .admin("adfad")
        .followUpDate(now().plusDays(10))
        .lastUpdatedDate(now())
        .comments(List.of())
        .build();
    when(concernsService.saveConcern(concernsDto)).thenReturn(concern);
    when(concern.getId()).thenReturn(concernId);
    this.mockMvc.perform(post("/api/concerns")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsBytes(concernsDto)))
        .andExpect(status().isOk());
  }

  private ConcernsSummaryDto prepareDoctorConcernsSummary() {
    final var doctorsConcernsForDB = buildTraineeConcernList();
    return ConcernsSummaryDto.builder()
        .concernTrainees(doctorsConcernsForDB)
        .countTotal(doctorsConcernsForDB.size())
        .build();
  }

  private List<ConcernTraineeDto> buildTraineeConcernList() {
    final var concern1 = ConcernTraineeDto.builder()
        .gmcReferenceNumber(gmcRef1)
        .doctorFirstName(firstName1)
        .doctorLastName(lastName1)
        .programme(programme1)
        .concernsStatus(concernsStatus1)
        .dateRaised(dateRaised1)
        .type(type1)
        .site(site1)
        .source(source1)
        .status(status1.name())
        .admin(admin1)
        .followUpDate(followUpDate1)
        .closedDate(closedDate1)
        .dateAdded(dateAdded1)
        .build();

    final var concern2 = ConcernTraineeDto.builder()
        .gmcReferenceNumber(gmcRef2)
        .doctorFirstName(firstName2)
        .doctorLastName(lastName2)
        .programme(programme2)
        .concernsStatus(concernsStatus2)
        .dateRaised(dateRaised2)
        .type(type2)
        .site(site2)
        .source(source2)
        .status(status2.name())
        .admin(admin2)
        .followUpDate(followUpDate2)
        .closedDate(closedDate2)
        .dateAdded(dateAdded1)
        .build();
    return of(concern1, concern2);
  }

  private ConcernsDto prepareConcernsRecordDto() {
    return ConcernsDto.builder()
        .concernId(concernId)
        .gmcNumber(gmcRef1)
        .dateOfIncident(dateRaised1)
        .concernType(prepareReferenceDto(typeId1, type1))
        .source(prepareReferenceDto(sourceId1, source1))
        .dateReported(dateReported)
        .employer(prepareReferenceDto(employerId, employer))
        .site(prepareReferenceDto(siteId1, site1))
        .grade(prepareReferenceDto(gradeId, grade))
        .status(status1.name())
        .admin(admin1)
        .followUpDate(followUpDate1)
        .lastUpdatedDate(lastUpdatedDate)
        .comments(List.of(comment))
        .build();
  }

  private ReferenceDto prepareReferenceDto(final long id, final String label) {
    return ReferenceDto.builder().id(id).label(label).build();
  }
}
