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
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsRequestDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ReferenceDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.TraineeInfoDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.TraineeSummaryDto;
import uk.nhs.hee.tis.revalidation.concerns.entity.Concern;
import uk.nhs.hee.tis.revalidation.concerns.entity.Reference;
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
  private long concernTypeId;
  private String source;
  private long sourceId;
  private LocalDate dateReported;
  private String employer;
  private String site;
  private long siteId;
  private String grade;
  private long gradeId;
  private String status;
  private long statusId;
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
    final var concern = prepareConcern();
    when(concernsRepository.findAllByGmcNumber(gmcRef1)).thenReturn(List.of(concern));
    var detailedConcernDto = concernsService.getTraineeConcernsInfo(gmcRef1);
    assertThat(detailedConcernDto.getConcerns().size(), is(1));
    final var concernsDto = detailedConcernDto.getConcerns().get(0);
    assertThat(concernsDto.getConcernId(), is(concernId));
    assertThat(concernsDto.getGmcNumber(), is(gmcRef1));
    assertThat(concernsDto.getDateOfIncident(), is(dateOfIncident));
    assertThat(concernsDto.getConcernType().getLabel(), is(concernType));
    assertThat(concernsDto.getConcernType().getId(), is(concernTypeId));
    assertThat(concernsDto.getSource().getLabel(), is(source));
    assertThat(concernsDto.getSource().getId(), is(sourceId));
    assertThat(concernsDto.getDateReported(), is(dateReported));
    assertThat(concernsDto.getEmployer(), is(employer));
    assertThat(concernsDto.getSite().getLabel(), is(site));
    assertThat(concernsDto.getSite().getId(), is(siteId));
    assertThat(concernsDto.getGrade().getLabel(), is(grade));
    assertThat(concernsDto.getGrade().getId(), is(gradeId));
    assertThat(concernsDto.getStatus().getLabel(), is(status));
    assertThat(concernsDto.getStatus().getId(), is(statusId));
    assertThat(concernsDto.getAdmin(), is(admin));
    assertThat(concernsDto.getFollowUpDate(), is(followUpDate));
    assertThat(concernsDto.getLastUpdatedDate(), is(lastUpdatedDate));
    assertThat(concernsDto.getComments().get(0), is("Test Comment 1"));
    assertThat(concernsDto.getComments().get(1), is("Test Comment 2"));
  }

  @Test
  public void shouldNotFailWhenThereIsNoConcernsForADoctorInTheService() throws Exception {
    when(concernsRepository.findAllByGmcNumber(gmcRef1)).thenReturn(List.of());
    var detailedConcernDto = concernsService.getTraineeConcernsInfo(gmcRef1);
    assertThat(detailedConcernDto.getConcerns().size(), is(0));
  }

  @Test
  public void shouldSaveConcern() {
    final var concernRecord = prepareConcernRecordDto();
    var concern = prepareConcern();
    when(concernsService.saveConcern(concernRecord)).thenReturn(concern);
    concern = concernsService.saveConcern(concernRecord);
    assertThat(concern.getDateOfIncident(), is(dateOfIncident));
    assertThat(concern.getConcernType().getLabel(), is(concernType));
    assertThat(concern.getConcernType().getId(), is(concernTypeId));
    assertThat(concern.getSource().getLabel(), is(source));
    assertThat(concern.getSource().getId(), is(sourceId));
    assertThat(concern.getDateReported(), is(dateReported));
    assertThat(concern.getEmployer(), is(employer));
    assertThat(concern.getSite().getLabel(), is(site));
    assertThat(concern.getSite().getId(), is(siteId));
    assertThat(concern.getGrade().getLabel(), is(grade));
    assertThat(concern.getGrade().getId(), is(gradeId));
    assertThat(concern.getStatus().getLabel(), is(status));
    assertThat(concern.getStatus().getId(), is(statusId));
    assertThat(concern.getAdmin(), is(admin));
    assertThat(concern.getFollowUpDate(), is(followUpDate));
    assertThat(concern.getLastUpdatedDate(), is(lastUpdatedDate));
  }

  private Concern prepareConcern() {
    return Concern.builder()
        .id(concernId)
        .gmcNumber(gmcRef1)
        .dateOfIncident(dateOfIncident)
        .concernType(prepareReferenceEntity(concernTypeId, concernType))
        .source(prepareReferenceEntity(sourceId, source))
        .dateReported(dateReported)
        .employer(employer)
        .site(prepareReferenceEntity(siteId, site))
        .grade(prepareReferenceEntity(gradeId, grade))
        .status(prepareReferenceEntity(statusId, status))
        .admin(admin)
        .followUpDate(followUpDate)
        .lastUpdatedDate(lastUpdatedDate)
        .comments(comments)
        .build();
  }

  private ConcernsDto prepareConcernRecordDto() {
    return ConcernsDto.builder()
        .gmcNumber(gmcRef1)
        .dateOfIncident(dateOfIncident)
        .concernType(prepareReferenceDto(concernTypeId, concernType))
        .source(prepareReferenceDto(sourceId,source))
        .dateReported(dateReported)
        .employer(employer)
        .site(prepareReferenceDto(siteId, site))
        .grade(prepareReferenceDto(gradeId, grade))
        .status(prepareReferenceDto(statusId, status))
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

  private Reference prepareReferenceEntity(final long id, final String label) {
    return Reference.builder().id(id).label(label).build();
  }

  private ReferenceDto prepareReferenceDto(final long id, final String label) {
    return ReferenceDto.builder().id(id).label(label).build();
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
    concernTypeId = faker.number().randomNumber();
    source = faker.lorem().characters(4);
    sourceId = faker.number().randomNumber();
    dateReported = now().minusDays(10);
    employer = "Mile End Hospital Trust";
    site = "Mile End Hospital Trust";
    siteId = faker.number().randomNumber();
    grade = "Academic Clinical Lecturer";
    gradeId = faker.number().randomNumber();
    status = "Open";
    statusId = faker.number().randomNumber();
    admin = "admin@admin.com";
    followUpDate = now().minusDays(7);
    lastUpdatedDate = now().minusDays(8);
    comments = of("Test Comment 1", "Test Comment 2");
    closedDate = now().minusDays(1);
    dateAdded = now().minusDays(2);
  }
}
