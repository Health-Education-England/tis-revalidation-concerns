package uk.nhs.hee.tis.revalidation.concerns.service;

import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsRequestDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.TraineeInfoDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.TraineeSummaryDto;

import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;
import static java.util.List.of;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConcernsServiceTest {

  private final Faker faker = new Faker();

  @InjectMocks
  private ConcernsService concernsService;

  @Mock
  private RevalidationService revalidationService;

  @Mock
  private TraineeSummaryDto traineeSummaryDto;

  private String gmcRef1, gmcRef2;
  private String firstName1, firstName2;
  private String lastName1, lastName2;
  private LocalDate submissionDate1, submissionDate2;
  private LocalDate dateAdded1, dateAdded2;
  private String sanction1, sanction2;
  private TraineeSummaryDto gmcDoctorDTO;

  protected static final String DATE_RAISED = "dateRaised";
  protected static final String EMPTY_STRING = "";
  protected static final String ASC = "asc";

  @Before
  public void setup() {
    setupData();
    gmcDoctorDTO = prepareGmcDoctor();
  }

  @Test
  public void shouldReturnListOfAllDoctorsConcerns() {
    final var requestDTO = ConcernsRequestDto.builder().sortOrder(ASC).sortColumn(DATE_RAISED).searchQuery(EMPTY_STRING).build();
    when(revalidationService.getTraineeInfo(requestDTO)).thenReturn(gmcDoctorDTO);

    final var allConcerns = concernsService.getConcernsSummary(requestDTO);
    final var doctorsForDB = allConcerns.getConcernTrainees();
    assertThat(allConcerns.getCountTotal(), is(2L));
    assertThat(doctorsForDB.get(0).getGmcReferenceNumber(), is(gmcRef1));
    assertThat(doctorsForDB.get(0).getDoctorFirstName(), is(firstName1));
    assertThat(doctorsForDB.get(0).getDoctorLastName(), is(lastName1));
    assertThat(doctorsForDB.get(0).getDateAdded(), is(dateAdded1));

    assertThat(doctorsForDB.get(1).getGmcReferenceNumber(), is(gmcRef2));
    assertThat(doctorsForDB.get(1).getDoctorFirstName(), is(firstName2));
    assertThat(doctorsForDB.get(1).getDoctorLastName(), is(lastName2));
    assertThat(doctorsForDB.get(1).getDateAdded(), is(dateAdded2));

  }

  @Test
  public void shouldReturnEmptyListOfDoctorsConcernsWhenNoDataInRevalidationService() {

    final var requestDTO = ConcernsRequestDto.builder().sortOrder(ASC).sortColumn(DATE_RAISED).searchQuery(EMPTY_STRING).build();
    when(revalidationService.getTraineeInfo(requestDTO)).thenReturn(traineeSummaryDto);
    when(traineeSummaryDto.getTraineeInfo()).thenReturn(List.of());

    final var allConcerns = concernsService.getConcernsSummary(requestDTO);
    assertThat(allConcerns.getCountTotal(), is(0L));
    assertThat(allConcerns.getTotalPages(), is(0L));
    assertThat(allConcerns.getTotalResults(), is(0L));
  }

  @Test (expected = RuntimeException.class)
  public void shouldReturnExceptionWhenRevalidationIsDown() {
    final var requestDTO = ConcernsRequestDto.builder().build();
    when(revalidationService.getTraineeInfo(requestDTO)).thenThrow(new RuntimeException());
    concernsService.getConcernsSummary(requestDTO);

  }

  @Test
  public void shouldReturnListOfAllDoctorsWithDefaultParameters() {
    final var requestDTO = ConcernsRequestDto.builder().build();
    when(revalidationService.getTraineeInfo(requestDTO)).thenReturn(gmcDoctorDTO);
    final var allConcerns = concernsService.getConcernsSummary(requestDTO);
    assertThat(allConcerns.getCountTotal(), is(2L));
  }

  private TraineeSummaryDto prepareGmcDoctor() {
    final var doctorsForDB = buildDoctorsForDBList();
    return TraineeSummaryDto.builder()
        .traineeInfo(doctorsForDB)
        .countTotal(doctorsForDB.size())
        .countUnderNotice(1l)
        .build();
  }

  private List<TraineeInfoDto> buildDoctorsForDBList() {
    final var doctor1 = TraineeInfoDto.builder()
        .gmcReferenceNumber(gmcRef1)
        .doctorFirstName(firstName1)
        .doctorLastName(lastName1)
        .submissionDate(submissionDate1)
        .dateAdded(dateAdded1)
        .sanction(sanction1)
        .build();

    final var doctor2 = TraineeInfoDto.builder()
        .gmcReferenceNumber(gmcRef2)
        .doctorFirstName(firstName2)
        .doctorLastName(lastName2)
        .submissionDate(submissionDate2)
        .dateAdded(dateAdded2)
        .sanction(sanction2)
        .build();
    return of(doctor1, doctor2);
  }

  private void setupData() {
    gmcRef1 = faker.number().digits(8);
    gmcRef2 = faker.number().digits(8);
    firstName1 = faker.name().firstName();
    firstName2 = faker.name().firstName();
    lastName1 = faker.name().lastName();
    lastName2 = faker.name().lastName();
    submissionDate1 = now();
    submissionDate2 = now();
    dateAdded1 = now().minusDays(5);
    dateAdded2 = now().minusDays(5);
    sanction1 = faker.lorem().characters(2);
    sanction2 = faker.lorem().characters(2);
  }
}
