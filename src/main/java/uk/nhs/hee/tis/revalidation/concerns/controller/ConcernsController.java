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

package uk.nhs.hee.tis.revalidation.concerns.controller;

import static java.util.List.of;
import static org.springframework.http.HttpStatus.OK;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsRecordDto;
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
  protected static final String DESIGNATED_BODY_CODES = "dbcs";

  @Autowired
  private ConcernsService concernsService;

  @ApiOperation(value = "Get detailed concerns of a trainee", notes =
      "It will return trainee's concern details", response = List.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Trainee concern details", response = List.class)})
  @GetMapping("/{gmcId}")
  public ResponseEntity<List<ConcernsDto>> getDetailedConcerns(
      @PathVariable("gmcId") final String gmcId) {
    log.info("Received request to fetch concerns for GmcId: {}", gmcId);
    if (Objects.nonNull(gmcId)) {
      final var concerns = concernsService.getTraineeConcernsInfo(gmcId);
      return ResponseEntity.ok().body(concerns);
    }
    return ResponseEntity.ok().body(of());
  }

  @ApiOperation(value = "Get latest concerns info of a list of trainee", notes =
      "It will return trainees' latest concern info", response = Map.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Trainees' latest concern info", response = Map.class)})
  @GetMapping("/summary/{gmcIds}")
  public ResponseEntity<Map<String, ConcernsRecordDto>> getConcernsSummary(
      @PathVariable("gmcIds") final List<String> gmcIds) {
    log.info("Received request to fetch concerns for GmcIds: {}", gmcIds);
    final var concerns = concernsService.getTraineesLatestConcernsInfo(gmcIds);
    return new ResponseEntity<Map<String, ConcernsRecordDto>>(concerns, OK);
  }

  @ApiOperation(value = "Save new concern for a trainee", notes =
      "It will save trainee concern record", response = String.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Trainee's new concern Id", response = String.class)})
  @PostMapping
  public ResponseEntity saveConcern(@RequestBody final ConcernsDto concerns) {
    log.info("Received request to save concerns: {}", concerns);
    if (Objects.nonNull(concerns)) {
      final var concern = concernsService.saveConcern(concerns);
      return ResponseEntity.ok(concern.getId());
    }
    return ResponseEntity.ok().build();
  }
}
