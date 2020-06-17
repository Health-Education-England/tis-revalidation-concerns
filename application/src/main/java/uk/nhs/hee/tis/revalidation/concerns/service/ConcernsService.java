package uk.nhs.hee.tis.revalidation.concerns.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernTraineeDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsRequestDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsSummaryDto;
import uk.nhs.hee.tis.revalidation.concerns.repository.ConcernsForDBRepository;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.*;

@Transactional
@Service
public class ConcernsService {

  @Autowired
  private ConcernsForDBRepository concernsRepository;

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
}
