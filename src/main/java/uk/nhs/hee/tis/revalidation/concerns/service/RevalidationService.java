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

import static java.lang.String.format;
import static org.springframework.http.HttpEntity.EMPTY;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernsRequestDto;
import uk.nhs.hee.tis.revalidation.concerns.dto.TraineeSummaryDto;
import uk.nhs.hee.tis.revalidation.concerns.exception.RevalidationException;

@Slf4j
@Service
public class RevalidationService {

  protected static final String DOCTORS_URL = "v1/doctors";

  @Value("${app.concern.revalidation.url}")
  private String revalidationUrl;

  @Autowired
  private RestTemplate restTemplate;

  public TraineeSummaryDto getTraineeInfo(final ConcernsRequestDto requestDto) {
    final var uriComponentsBuilder = buildTraineeRequestUrl(requestDto);

    final var responseEntity = restTemplate.exchange(uriComponentsBuilder.toUriString(),
        GET, EMPTY, TraineeSummaryDto.class);

    if (OK == responseEntity.getStatusCode()) {
      return responseEntity.getBody();
    }
    throw new RevalidationException(
        "Failed to fetched trainee information: " + responseEntity.getStatusCode());
  }

  private UriComponentsBuilder buildTraineeRequestUrl(final ConcernsRequestDto requestDto) {
    return UriComponentsBuilder.fromUriString(format("%s/%s", revalidationUrl, DOCTORS_URL))
        .queryParam("sortColumn", requestDto.getSortColumn())
        .queryParam("sortOrder", requestDto.getSortOrder())
        .queryParam("searchQuery", requestDto.getSearchQuery())
        .queryParam("pageNumber", requestDto.getPageNumber())
        .queryParam("dbcs", getDbcs(requestDto.getDbcs()));
  }

  private String getDbcs(final List<String> dbcs) {
    return dbcs.stream().collect(Collectors.joining(","));
  }
}
