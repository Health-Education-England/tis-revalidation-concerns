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

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.ListUsersInGroupRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersInGroupResult;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernAdminDto;
import uk.nhs.hee.tis.revalidation.concerns.mappers.ConcernAdminMapper;

@Service
public class ConcernAdminService {

  @Value("${app.cognito.admin-group}")
  private String adminGroup;

  @Value("${app.cognito.admin-user-pool}")
  private String adminUserPool;

  private AWSCognitoIdentityProvider identityProvider;
  private ConcernAdminMapper mapper;

  ConcernAdminService(AWSCognitoIdentityProvider identityProvider, ConcernAdminMapper mapper) {
    this.identityProvider = identityProvider;
    this.mapper = mapper;
  }

  public List<ConcernAdminDto> getAssignableAdmins() {
    ListUsersInGroupRequest request = new ListUsersInGroupRequest();
    request.setGroupName(adminGroup);
    request.setUserPoolId(adminUserPool);

    // TODO: A limited number of users can be returned before pagination occurs, handle pagination.
    ListUsersInGroupResult listUsersResult = identityProvider.listUsersInGroup(request);

    return listUsersResult.getUsers().stream()
        .map(mapper::toDto)
        .collect(Collectors.toList());
  }
}
