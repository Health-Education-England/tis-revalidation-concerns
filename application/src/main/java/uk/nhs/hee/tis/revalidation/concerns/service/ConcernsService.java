package uk.nhs.hee.tis.revalidation.concerns.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.nhs.hee.tis.revalidation.concerns.dto.*;
import uk.nhs.hee.tis.revalidation.concerns.repository.ConcernsRepository;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;

@Slf4j
@Transactional
@Service
public class ConcernsService {

  @Autowired
  private ConcernsRepository concernsRepository;

  @Autowired
  private RevalidationService revalidationService;

  public ConcernsSummaryDto getConcernsSummary(final ConcernsRequestDto requestDto) {
    final var traineeInfo = revalidationService.getTraineeInfo(requestDto);

    final var concernTrainees = traineeInfo.getTraineeInfo().stream().map(trainee -> {
      return ConcernTraineeDto.builder()
          .gmcReferenceNumber(trainee.getGmcReferenceNumber())
          .doctorFirstName(trainee.getDoctorFirstName())
          .doctorLastName(trainee.getDoctorLastName())
          .followUpDate(now())
          .programme(trainee.getProgrammeName())
          .site("site")
          .status(trainee.getDoctorStatus())
          .admin(trainee.getAdmin())
          .closedDate(now())
          .dateRaised(now())
          .type("concern")
          .concernsStatus("Open")
          .source("source")
          .dateAdded(trainee.getDateAdded())
          .build();
    }).collect(toList());

    return ConcernsSummaryDto.builder()
        .countTotal(traineeInfo.getCountTotal())
        .totalPages(traineeInfo.getTotalPages())
        .totalResults(traineeInfo.getTotalResults())
        .concernTrainees(concernTrainees)
        .build();
  }

  public DetailedConcernDto getTraineeConcernsInfo(final String gmcId) {
    log.info("Fetching concerns info for GmcId: {}", gmcId);
    final var concerns = concernsRepository.findAllByGmcNumber(gmcId);
    final var allConcernsForTrainee = concerns.stream().map(concern -> {
          return ConcernsRecordDto.builder()
          .gmcNumber(gmcId)
          .concernId(concern.getId())
          .dateOfIncident(concern.getDateOfIncident())
          .concernType(concern.getConcernType())
          .source(concern.getSource())
          .dateReported(concern.getDateReported())
          .employer(concern.getEmployer())
          .site(concern.getSite())
          .grade(concern.getGrade())
          .status(concern.getStatus())
          .admin(concern.getAdmin())
          .followUpDate(concern.getFollowUpDate())
          .lastUpdatedDate(concern.getLastUpdatedDate())
          .comments(concern.getComments())
          .build();
    }).collect(toList());

    final var detailedConcernDtoBuilder = DetailedConcernDto.builder()
        .concerns(allConcernsForTrainee);
    return detailedConcernDtoBuilder.build();

  }
}
