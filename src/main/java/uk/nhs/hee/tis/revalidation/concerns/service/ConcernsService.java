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
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernTraineeDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsRecordDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsRequestDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsSummaryDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.ReferenceDto;
import uk.nhs.hee.tis.revalidation.concerns.entity.Concern;
import uk.nhs.hee.tis.revalidation.concerns.entity.ConcernStatus;
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

  public Map<String, ConcernsRecordDto> getTraineesLatestConcernsInfo(final List<String> gmcIds) {
    return gmcIds.stream().collect(toMap(identity(), this::prepareConcernData));
  }

  public List<ConcernsDto> getTraineeConcernsInfo(final String gmcId) {
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
          .employer(createReferenceDto(concern.getEmployer()))
          .site(createReferenceDto(concern.getSite()))
          .grade(createReferenceDto(concern.getGrade()))
          .status(concern.getStatus().name())
          .admin(concern.getAdmin())
          .followUpDate(concern.getFollowUpDate())
          .lastUpdatedDate(concern.getLastUpdatedDate())
          .comments(concern.getComments())
          .build();
    }).collect(toList());

    return allConcernsForTrainee;
  }

  public Concern saveConcern(final ConcernsDto concern) {
    log.info("Receive request to save concerns: {}", concern);
    final var traineeConcern = Concern.builder()
        .gmcNumber(concern.getGmcNumber())
        .dateOfIncident(concern.getDateOfIncident())
        .concernType(createReferenceEntity(concern.getConcernType()))
        .source(createReferenceEntity(concern.getSource()))
        .dateReported(concern.getDateReported())
        .employer(createReferenceEntity(concern.getEmployer()))
        .site(createReferenceEntity(concern.getSite()))
        .grade(createReferenceEntity(concern.getGrade()))
        .status(ConcernStatus.valueOf(concern.getStatus()))
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

  private ConcernsRecordDto prepareConcernData(final String gmcId) {
    final var concernList = concernsRepository.findAllByGmcNumberOrderByDateReportedDesc(gmcId);
    if (!concernList.isEmpty()) {
      final var concern = concernList.get(0);
      return ConcernsRecordDto.builder()
          .followUpDate(concern.getFollowUpDate())
          .site(concern.getSite().getLabel())
          .closedDate(concern.getClosedDate())
          .dateRaised(concern.getDateReported())
          .type(concern.getConcernType().getLabel())
          .concernsStatus(concern.getStatus().name())
          .source(concern.getSource().getLabel())
          .build();
    }
    return new ConcernsRecordDto();
  }
}
