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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpEntity.EMPTY;
import static org.springframework.http.HttpMethod.GET;
import static uk.nhs.hee.tis.revalidation.concerns.service.RevalidationService.DOCTORS_URL;

import com.github.javafaker.Faker;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsRequestDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.TraineeInfoDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.TraineeSummaryDto;
import uk.nhs.hee.tis.revalidation.concerns.exception.RevalidationException;

@ExtendWith(MockitoExtension.class)
class RevalidationServiceTest {

  private final Faker faker = new Faker();

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private ConcernsRequestDto concernsRequestDto;

  @InjectMocks
  private RevalidationService revalidationService;

  private String revalidationUrl;
  private String gmcNumber;
  private String sortColumn;
  private String sortOrder;
  private String searchQuery;
  private int pageNumber;


  @BeforeEach
  public void setup() {
    revalidationUrl = faker.internet().url();
    gmcNumber = faker.number().digits(8);
    sortColumn = faker.lorem().characters(6);
    sortOrder = faker.lorem().characters(3);
    searchQuery = faker.lorem().characters(8);
    pageNumber = faker.number().randomDigit();
    ReflectionTestUtils.setField(revalidationService, "revalidationUrl", revalidationUrl);
  }

  @Test
  public void shouldReturnTraineeInfo() {
    when(concernsRequestDto.getSortColumn()).thenReturn(sortColumn);
    when(concernsRequestDto.getSortOrder()).thenReturn(sortOrder);
    when(concernsRequestDto.getSearchQuery()).thenReturn(searchQuery);
    when(concernsRequestDto.getPageNumber()).thenReturn(pageNumber);
    final var url = String
        .format("%s/%s?sortColumn=%s&sortOrder=%s&searchQuery=%s&pageNumber=%d", revalidationUrl,
            DOCTORS_URL, sortColumn, sortOrder, searchQuery, pageNumber);
    final ResponseEntity<TraineeSummaryDto> response = ResponseEntity.ok(buildTraineeSummaryDto());
    when(restTemplate.exchange(url, GET, EMPTY, TraineeSummaryDto.class))
        .thenReturn(response);

    final var traineeInfo = revalidationService.getTraineeInfo(concernsRequestDto);
    assertThat(traineeInfo, notNullValue());
    assertThat(traineeInfo.getCountTotal(), is(1L));
    assertThat(traineeInfo.getTotalPages(), is(1L));
    assertThat(traineeInfo.getCountUnderNotice(), is(1L));
    assertThat(traineeInfo.getTotalResults(), is(1L));
    assertThat(traineeInfo.getTraineeInfo(), hasSize(1));
  }

  @Test
  public void shouldThrowExceptionWhenRevalidationIsDown() {
    when(concernsRequestDto.getSortColumn()).thenReturn(sortColumn);
    when(concernsRequestDto.getSortOrder()).thenReturn(sortOrder);
    when(concernsRequestDto.getSearchQuery()).thenReturn(searchQuery);
    when(concernsRequestDto.getPageNumber()).thenReturn(pageNumber);
    final var url = String
        .format("%s/%s?sortColumn=%s&sortOrder=%s&searchQuery=%s&pageNumber=%d", revalidationUrl,
            DOCTORS_URL, sortColumn, sortOrder, searchQuery, pageNumber);
    final ResponseEntity<TraineeSummaryDto> response = ResponseEntity.notFound().build();
    when(restTemplate.exchange(url, GET, EMPTY, TraineeSummaryDto.class))
        .thenReturn(response);

    Assertions.assertThrows(RevalidationException.class, () -> {
      revalidationService.getTraineeInfo(concernsRequestDto);
    });
  }

  private TraineeSummaryDto buildTraineeSummaryDto() {
    return TraineeSummaryDto.builder()
        .countTotal(1)
        .countUnderNotice(1)
        .totalPages(1)
        .totalResults(1)
        .traineeInfo(List.of(TraineeInfoDto.builder().build()))
        .build();
  }
}
