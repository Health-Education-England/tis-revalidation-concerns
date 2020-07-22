package uk.nhs.hee.tis.revalidation.concerns.service;

import static java.time.LocalDate.now;
import static java.util.List.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsRequestDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.TraineeInfoDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.TraineeSummaryDto;
import uk.nhs.hee.tis.revalidation.concerns.entity.Concern;
import uk.nhs.hee.tis.revalidation.concerns.exception.RevalidationException;
import uk.nhs.hee.tis.revalidation.concerns.repository.ConcernsRepository;

@ExtendWith(MockitoExtension.class)
public class ConcernsServiceTest {

  private final Faker faker = new Faker();

  @InjectMocks
  private ConcernsService concernsService;

  @Mock
  private RevalidationService revalidationService;

  @Mock
  private ConcernsRepository concernsRepository;

  @Mock
  private TraineeSummaryDto traineeSummaryDto;

  private String gmcRef1, gmcRef2;
  private String concernId;
  private String firstName1, firstName2;
  private String lastName1, lastName2;
  private LocalDate submissionDate1, submissionDate2;
  private LocalDate dateAdded1, dateAdded2;

  private LocalDate dateOfIncident;
  private String concernType;
  private String source;
  private LocalDate dateReported;
  private String employer;
  private String site;
  private String grade;
  private String status;
  private String admin;
  private LocalDate followUpDate;
  private LocalDate lastUpdatedDate;
  private List<String> comments;
  private LocalDate closedDate;
  private LocalDate dateAdded;

  private String sanction1, sanction2;
  private TraineeSummaryDto gmcDoctorDTO;

  protected static final String DATE_RAISED = "dateRaised";
  protected static final String EMPTY_STRING = "";
  protected static final String ASC = "asc";

  @BeforeEach
  public void setup() {
    setupData();
    gmcDoctorDTO = prepareGmcDoctor();
  }

  @Test
  public void shouldReturnListOfAllDoctorsConcerns() {
    final var request = ConcernsRequestDto.builder().sortOrder(ASC).sortColumn(DATE_RAISED)
        .searchQuery(EMPTY_STRING).build();
    when(revalidationService.getTraineeInfo(request)).thenReturn(gmcDoctorDTO);

    final var allConcerns = concernsService.getConcernsSummary(request);
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

    final var request = ConcernsRequestDto.builder().sortOrder(ASC).sortColumn(DATE_RAISED)
        .searchQuery(EMPTY_STRING).build();
    when(revalidationService.getTraineeInfo(request)).thenReturn(traineeSummaryDto);
    when(traineeSummaryDto.getTraineeInfo()).thenReturn(List.of());

    final var allConcerns = concernsService.getConcernsSummary(request);
    assertThat(allConcerns.getCountTotal(), is(0L));
    assertThat(allConcerns.getTotalPages(), is(0L));
    assertThat(allConcerns.getTotalResults(), is(0L));
  }

  @Test
  public void shouldReturnExceptionWhenRevalidationIsDown() {
    final var request = ConcernsRequestDto.builder().build();
    when(revalidationService.getTraineeInfo(request)).thenThrow(new RevalidationException(""));
    Assertions.assertThrows(RevalidationException.class, () -> {
      concernsService.getConcernsSummary(request);
    });
  }

  @Test
  public void shouldReturnListOfAllDoctorsWithDefaultParameters() {
    final var request = ConcernsRequestDto.builder().build();
    when(revalidationService.getTraineeInfo(request)).thenReturn(gmcDoctorDTO);
    final var allConcerns = concernsService.getConcernsSummary(request);
    assertThat(allConcerns.getCountTotal(), is(2L));
  }

  @Test
  public void shouldReturnAllConcernsForADoctor() throws Exception {
    final var concern = prepareConcernRecord();
    when(concernsRepository.findAllByGmcNumber(gmcRef1)).thenReturn(List.of(concern));
    var detailedConcernDto = concernsService.getTraineeConcernsInfo(gmcRef1);
    assertThat(detailedConcernDto.getConcerns().size(), is(1));
    assertThat(detailedConcernDto.getConcerns().get(0).getConcernId(), is(concernId));
    assertThat(detailedConcernDto.getConcerns().get(0).getGmcNumber(), is(gmcRef1));
    assertThat(detailedConcernDto.getConcerns().get(0).getDateOfIncident(), is(dateOfIncident));
    assertThat(detailedConcernDto.getConcerns().get(0).getConcernType(), is(concernType));
    assertThat(detailedConcernDto.getConcerns().get(0).getSource(), is(source));
    assertThat(detailedConcernDto.getConcerns().get(0).getDateReported(), is(dateReported));
    assertThat(detailedConcernDto.getConcerns().get(0).getEmployer(), is(employer));
    assertThat(detailedConcernDto.getConcerns().get(0).getSite(), is(site));
    assertThat(detailedConcernDto.getConcerns().get(0).getGrade(), is(grade));
    assertThat(detailedConcernDto.getConcerns().get(0).getStatus(), is(status));
    assertThat(detailedConcernDto.getConcerns().get(0).getAdmin(), is(admin));
    assertThat(detailedConcernDto.getConcerns().get(0).getFollowUpDate(), is(followUpDate));
    assertThat(detailedConcernDto.getConcerns().get(0).getLastUpdatedDate(), is(lastUpdatedDate));
    assertThat(detailedConcernDto.getConcerns().get(0).getComments().get(0), is("Test Comment 1"));
    assertThat(detailedConcernDto.getConcerns().get(0).getComments().get(1), is("Test Comment 2"));
  }

  @Test
  public void shouldNotFailWhenThereIsNoConcernsForADoctorInTheService() throws Exception {
    when(concernsRepository.findAllByGmcNumber(gmcRef1)).thenReturn(List.of());
    var detailedConcernDto = concernsService.getTraineeConcernsInfo(gmcRef1);
    assertThat(detailedConcernDto.getConcerns().size(), is(0));
  }

  private Concern prepareConcernRecord() {
    return Concern.builder()
        .id(concernId)
        .gmcNumber(gmcRef1)
        .dateOfIncident(dateOfIncident)
        .concernType(concernType)
        .source(source)
        .dateReported(dateReported)
        .employer(employer)
        .site(site)
        .grade(grade)
        .status(status)
        .admin(admin)
        .followUpDate(followUpDate)
        .lastUpdatedDate(lastUpdatedDate)
        .comments(comments)
        .build();
  }

  private TraineeSummaryDto prepareGmcDoctor() {
    final var doctorsForDB = buildDoctorsForDbList();
    return TraineeSummaryDto.builder()
        .traineeInfo(doctorsForDB)
        .countTotal(doctorsForDB.size())
        .countUnderNotice(1L)
        .build();
  }

  private List<TraineeInfoDto> buildDoctorsForDbList() {
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
    concernId = faker.number().digits(6);
    dateOfIncident = now().minusDays(5);
    concernType = faker.lorem().characters(4);
    source = faker.lorem().characters(4);
    dateReported = now().minusDays(10);
    ;
    employer = "Mile End Hospital Trust";
    site = "Mile End Hospital Trust";
    grade = "Academic Clinical Lecturer";
    status = "Open";
    admin = "admin@admin.com";
    followUpDate = now().minusDays(7);
    lastUpdatedDate = now().minusDays(8);
    comments = of("Test Comment 1", "Test Comment 2");
    closedDate = now().minusDays(1);
    dateAdded = now().minusDays(2);

  }
}
