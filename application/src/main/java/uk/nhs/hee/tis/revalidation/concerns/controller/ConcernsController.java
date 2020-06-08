package uk.nhs.hee.tis.revalidation.concerns.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsRequestDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsSummaryDto;
import uk.nhs.hee.tis.revalidation.concerns.service.ConcernsService;

@Slf4j
@RestController
@RequestMapping("/api/concerns")
public class ConcernsController {

  protected static final String SORT_COLUMN = "sortColumn";
  protected static final String SORT_ORDER = "sortOrder";
  protected static final String DATE_RAISED = "dateRaised";
  protected static final String DESC = "desc";
  protected static final String ASC = "asc";
  protected static final String CONCERNS_STATUS_CLOSED = "closed";
  protected static final String CONCERNS_STATUS_CLOSED_VALUE = "false";
  protected static final String PAGE_NUMBER = "pageNumber";
  protected static final String PAGE_NUMBER_VALUE = "0";

  @Autowired
  private ConcernsService concernsService;

  @GetMapping("/concernsinfo")
  public ResponseEntity<ConcernsSummaryDto> getConcernsInfo(@RequestParam(name = SORT_COLUMN, defaultValue = DATE_RAISED, required = false) final String sortColumn,
      @RequestParam(name = SORT_ORDER, defaultValue = DESC, required = false) final String sortOrder,
      @RequestParam(name = CONCERNS_STATUS_CLOSED, defaultValue = CONCERNS_STATUS_CLOSED_VALUE, required = false) final boolean concernsStatusClosed,
      @RequestParam(name = PAGE_NUMBER, defaultValue = PAGE_NUMBER_VALUE, required = false) final int pageNumber) {

    final var concernsRequestDto = ConcernsRequestDto.builder()
        .sortColumn(sortColumn)
        .sortOrder(sortOrder)
        .concernsStatusClosed(concernsStatusClosed)
        .pageNumber(pageNumber)
        .build();
    //TODO: validation
    final var concernSummary = concernsService.getConcernsSummary(concernsRequestDto);
    /*final var concernSummary = new ConcernsSummaryDto().builder()
        .countTotal(1234).build();*/
    return ResponseEntity.ok().body(concernSummary);
  }


}
