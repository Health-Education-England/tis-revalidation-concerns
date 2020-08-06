package uk.nhs.hee.tis.revalidation.concerns.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.ListUsersInGroupResult;
import com.amazonaws.services.cognitoidp.model.UserType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.hee.tis.revalidation.concerns.dto.ConcernAdminDto;
import uk.nhs.hee.tis.revalidation.concerns.mappers.ConcernAdminMapper;

@ExtendWith(MockitoExtension.class)
class ConcernAdminServiceTest {

  private ConcernAdminService service;

  @Mock
  private AWSCognitoIdentityProvider identityProvider;

  @BeforeEach
  void setUp() {
    ConcernAdminMapper mapper = Mappers.getMapper(ConcernAdminMapper.class);
    service = new ConcernAdminService(identityProvider, mapper);
  }

  @Test
  void shouldReturnEmptyListWhenNoAssignableAdminsFound() {
    // Given.
    ListUsersInGroupResult listUsersInGroupResult = new ListUsersInGroupResult();
    listUsersInGroupResult.setUsers(Collections.emptyList());

    when(identityProvider.listUsersInGroup(any())).thenReturn(listUsersInGroupResult);

    // When.
    List<ConcernAdminDto> assignableAdmins = service.getAssignableAdmins();

    // Then.
    assertThat("Unexpected number of admins.", assignableAdmins.size(), is(0));
  }

  @Test
  void shouldReturnAdminsWhenAssignableAdminsFound() {
    // Given.
    UserType user1 = new UserType();
    user1.setUsername("user1");

    UserType user2 = new UserType();
    user2.setUsername("user2");

    ListUsersInGroupResult listUsersInGroupResult = new ListUsersInGroupResult();
    listUsersInGroupResult.setUsers(Arrays.asList(user1, user2));

    when(identityProvider.listUsersInGroup(any())).thenReturn(listUsersInGroupResult);

    // When.
    List<ConcernAdminDto> admins = service.getAssignableAdmins();

    // Then.
    assertThat("Unexpected number of admins.", admins.size(), is(2));

    ConcernAdminDto admin = admins.get(0);
    assertThat("Unexpected username.", admin.getUsername(), is("user1"));
    assertThat("Unexpected full name.", admin.getFullName(), is("user1"));

    admin = admins.get(1);
    assertThat("Unexpected username.", admin.getUsername(), is("user2"));
    assertThat("Unexpected full name.", admin.getFullName(), is("user2"));
  }
}
