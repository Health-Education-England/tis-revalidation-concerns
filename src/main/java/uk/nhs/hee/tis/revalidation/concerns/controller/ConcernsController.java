package uk.nhs.hee.tis.revalidation.concerns.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsRequestDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsSummaryDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.DetailedConcernDto;
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
  protected static final String SEARCH_QUERY = "searchQuery";
  protected static final String EMPTY_STRING = "";

  @Autowired
  private ConcernsService concernsService;

  @ApiOperation(value = "All trainee doctors information", notes =
      "It will return all the information about trainee doctors", response = ConcernsSummaryDto.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Trainee gmc all doctors data", response = ConcernsSummaryDto.class)})
  @GetMapping
  public ResponseEntity<ConcernsSummaryDto> getConcernsInfo(
      @RequestParam(name = SORT_COLUMN, defaultValue = DATE_RAISED, required = false) final String sortColumn,
      @RequestParam(name = SORT_ORDER, defaultValue = DESC, required = false) final String sortOrder,
      @RequestParam(name = CONCERNS_STATUS_CLOSED, defaultValue = CONCERNS_STATUS_CLOSED_VALUE, required = false) final boolean concernsStatusClosed,
      @RequestParam(name = PAGE_NUMBER, defaultValue = PAGE_NUMBER_VALUE, required = false) final int pageNumber,
      @RequestParam(name = SEARCH_QUERY, defaultValue = EMPTY_STRING, required = false) final String searchQuery) {

    final var concernsRequestDto = ConcernsRequestDto.builder()
        .sortColumn(sortColumn)
        .sortOrder(sortOrder)
        .pageNumber(pageNumber)
        .searchQuery(searchQuery)
        .build();
    final var concernSummary = concernsService.getConcernsSummary(concernsRequestDto);
    return ResponseEntity.ok().body(concernSummary);
  }

  @ApiOperation(value = "Get detailed concerns of a trainee", notes =
      "It will return trainee's concern details", response = DetailedConcernDto.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Trainee concern details", response = DetailedConcernDto.class)})
  @GetMapping("/{gmcId}")
  public ResponseEntity<DetailedConcernDto> getDetailedConcerns(
      @PathVariable("gmcId") final String gmcId) {
    log.info("Received request to fetch concerns for GmcId: {}", gmcId);
    if (Objects.nonNull(gmcId)) {
      final var detailedConcernDto = concernsService.getTraineeConcernsInfo(gmcId);
      return ResponseEntity.ok().body(detailedConcernDto);
    }
    return new ResponseEntity<>(DetailedConcernDto.builder().build(), HttpStatus.OK);
  }

  @ApiOperation(value = "Save new concern for a trainee", notes =
      "It will save trainee concern record", response = String.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Trainee's new concern Id", response = String.class)})
  @PostMapping
  public ResponseEntity saveConcern(final ConcernsDto concerns) {
    log.info("Received request to save concerns: {}", concerns);
    if (Objects.nonNull(concerns)) {
      final var concern = concernsService.saveConcern(concerns);
      return ResponseEntity.ok(concern.getId());
    }
    return ResponseEntity.ok().build();
  }
}