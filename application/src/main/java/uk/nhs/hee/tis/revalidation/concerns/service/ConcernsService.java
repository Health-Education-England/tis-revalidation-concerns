package uk.nhs.hee.tis.revalidation.concerns.service;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import static org.springframework.data.domain.PageRequest.of;
import static org.springframework.data.domain.Sort.by;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsRequestDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsSummaryDto;
import uk.nhs.hee.tis.revalidation.concerns.entity.ConcernsForDB;
import uk.nhs.hee.tis.revalidation.concerns.repository.ConcernsForDBRepository;

@Transactional
@Service
public class ConcernsService {

  @Value("${app.reval.pagination.pageSize}")
  private int pageSize;

  @Autowired
  private ConcernsForDBRepository concernsRepository;

  public ConcernsSummaryDto getConcernsSummary(final ConcernsRequestDto requestDto) {
    final var paginatedConcerns = getSortedConcernsByPageNumber(requestDto);
    final var doctorsList = paginatedConcerns.get().collect(toList());
    final var gmcIds = doctorsList.stream().map(doc -> doc.getGmcReferenceNumber()).collect(toList());
   /* final var traineeCoreInfo = traineeCoreService.getTraineeInformationFromCore(gmcIds);
    final var traineeDoctors = doctorsList.stream().map(d ->
        convert(d, traineeCoreInfo.get(d.getGmcReferenceNumber()))).collect(toList());
*/
    /*return TraineeSummaryDto.builder()
        .traineeInfo(traineeDoctors)
        .countTotal(getCountAll())
        .countUnderNotice(getCountUnderNotice())
        .totalPages(paginatedConcerns.getTotalPages())
        .totalResults(paginatedConcerns.getTotalElements())
        .build();*/
    return null;
  }

  private Page<ConcernsForDB> getSortedConcernsByPageNumber(final ConcernsRequestDto requestDto) {
    final var direction = "asc".equalsIgnoreCase(requestDto.getSortOrder()) ? ASC : DESC;
    final var pageableAndSortable = of(requestDto.getPageNumber(), pageSize, by(direction, requestDto.getSortColumn()));
    if (requestDto.isConcernsStatusClosed()) {
      return concernsRepository.findAllByConcernsStatusClosedIn(pageableAndSortable, requestDto.getSearchQuery(), "Closed");
    }

    return null;
    //return doctorsRepository.findAll(pageableAndSortable, requestDTO.getSearchQuery());
  }

}
