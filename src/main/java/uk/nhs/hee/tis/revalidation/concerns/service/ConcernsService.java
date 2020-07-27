package uk.nhs.hee.tis.revalidation.concerns.service;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernTraineeDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsRequestDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsSummaryDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.DetailedConcernDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ReferenceDto;
import uk.nhs.hee.tis.revalidation.concerns.entity.Concern;
import uk.nhs.hee.tis.revalidation.concerns.entity.Reference;
import uk.nhs.hee.tis.revalidation.concerns.repository.ConcernsRepository;

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
      return ConcernsDto.builder()
          .gmcNumber(gmcId)
          .concernId(concern.getId())
          .dateOfIncident(concern.getDateOfIncident())
          .concernType(createReferenceDto(concern.getConcernType()))
          .source(createReferenceDto(concern.getSource()))
          .dateReported(concern.getDateReported())
          .employer(concern.getEmployer())
          .site(createReferenceDto(concern.getSite()))
          .grade(createReferenceDto(concern.getGrade()))
          .status(createReferenceDto(concern.getStatus()))
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

  public Concern saveConcern(final ConcernsDto concern) {

    final var traineeConcern = Concern.builder()
        .gmcNumber(concern.getGmcNumber())
        .dateOfIncident(concern.getDateOfIncident())
        .concernType(createReferenceEntity(concern.getConcernType()))
        .source(createReferenceEntity(concern.getSource()))
        .dateReported(concern.getDateReported())
        .employer(concern.getEmployer())
        .site(createReferenceEntity(concern.getSite()))
        .grade(createReferenceEntity(concern.getGrade()))
        .status(createReferenceEntity(concern.getStatus()))
        .admin(concern.getAdmin())
        .followUpDate(concern.getFollowUpDate())
        .lastUpdatedDate(concern.getLastUpdatedDate())
        .build();

    return concernsRepository.save(traineeConcern);

  }

  private Reference createReferenceEntity(final ReferenceDto referenceDto) {
    return Reference.builder().id(referenceDto.getId()).label(referenceDto.getLabel()).build();
  }

  private ReferenceDto createReferenceDto(final Reference reference) {
    return ReferenceDto.builder().id(reference.getId()).label(reference.getLabel()).build();
  }
}
