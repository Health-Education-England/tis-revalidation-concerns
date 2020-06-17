package uk.nhs.hee.tis.revalidation.concerns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernTraineeDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsRequestDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsSummaryDto;
import uk.nhs.hee.tis.revalidation.concerns.service.ConcernsService;

import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;
import static java.util.List.of;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.nhs.hee.tis.revalidation.concerns.controller.ConcernsController.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ConcernsController.class)
public class ConcernsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ConcernsService concernsService;

  @Autowired
  private ObjectMapper mapper;

  private final Faker faker = new Faker();
  private String gmcRef1, gmcRef2;
  private String firstName1, firstName2;
  private String lastName1, lastName2;
  private String programme1, programme2;
  private String concernsStatus1, concernsStatus2;
  private LocalDate dateRaised1, dateRaised2;
  private String type1, type2;
  private String site1, site2;
  private String source1, source2;
  private String status1, status2;
  private String admin1, admin2;
  private LocalDate followUpDate1, followUpDate2;
  private LocalDate closedDate1, closedDate2;
  private LocalDate dateAdded1, dateAdded2;

  @Before
  public void setup() {
    gmcRef1 = faker.number().digits(8);
    gmcRef2 = faker.number().digits(8);
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
    type1 = "AAAA";
    type2 = "AAAA";
    site1 = "AAAAA";
    site2 = "AAAAA";
    source1 = "BBBBB";
    source2 = "BBBBB";
    status1 = "AAAAA";
    status2 = "AAAAA";
    admin1 = "AAAAA";
    admin2 = "BBBBB";
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
    this.mockMvc.perform(get("/api/doctors")
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
    this.mockMvc.perform(get("/api/doctors"))
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
    this.mockMvc.perform(get("/api/doctors"))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().json(mapper.writeValueAsString(doctorConcernsSummaryDto)));
  }

  private ConcernsSummaryDto prepareDoctorConcernsSummary() {
    final var doctorsConcernsForDB = buildDoctorsConcernsForDBList();
    return ConcernsSummaryDto.builder()
        .concernTrainees(doctorsConcernsForDB)
        .countTotal(doctorsConcernsForDB.size())
        .build();
  }

  private List<ConcernTraineeDto> buildDoctorsConcernsForDBList() {
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
        .status(status1)
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
        .status(status2)
        .admin(admin2)
        .followUpDate(followUpDate2)
        .closedDate(closedDate2)
        .dateAdded(dateAdded1)
        .build();
    return of(concern1, concern2);
  }
}
