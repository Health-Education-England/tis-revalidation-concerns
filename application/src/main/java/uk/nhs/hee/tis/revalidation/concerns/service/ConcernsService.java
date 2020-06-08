package uk.nhs.hee.tis.revalidation.concerns.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernTraineeDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsRequestDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsSummaryDto;

import java.util.stream.Collectors;

import static java.time.LocalDate.now;

@Transactional
@Service
public class ConcernsService {

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
          .dateAdded(now())
          .build();
    }).collect(Collectors.toList());

    return ConcernsSummaryDto.builder()
        .countTotal(traineeInfo.getCountTotal())
        .concernTrainees(concernTrainees)
        .build();
  }

//  private Page<ConcernsForDB> getSortedConcernsByPageNumber(final ConcernsRequestDto requestDto) {
//    final var direction = "asc".equalsIgnoreCase(requestDto.getSortOrder()) ? ASC : DESC;
//    final var pageableAndSortable = of(requestDto.getPageNumber(), pageSize, by(direction, requestDto.getSortColumn()));
//    if (requestDto.isConcernsStatusClosed()) {
//      return concernsRepository.findAllByConcernsStatusClosedIn(pageableAndSortable, requestDto.getSearchQuery(), "Closed");
//    }
//
//    return null;
//    //return doctorsRepository.findAll(pageableAndSortable, requestDTO.getSearchQuery());
//  }

}
