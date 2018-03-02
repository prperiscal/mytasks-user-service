package com.mytasks.user.service;

import com.mytasks.user.ServiceApplication;
import com.mytasks.user.model.User;
import com.mytasks.user.model.UserGroup;
import com.mytasks.user.rest.input.IncludeUsers;
import com.mytasks.user.rest.input.RemoveUsers;
import com.mytasks.user.rest.input.UserGroupInsert;
import com.prperiscal.spring.data.compose.DataComposeResource;
import com.prperiscal.spring.data.compose.SpringDataCompose;
import lombok.Setter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static com.google.common.collect.Sets.newHashSet;
import static com.mytasks.user.CommonTestVars.PROFILE;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(value = PROFILE)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceApplication.class)
@SpringDataCompose
public class UserGroupServiceTest {

    @Setter(onMethod = @__({ @Autowired}))
    private UserGroupService userGroupService;


    @Setter(onMethod = @__({ @Autowired}))
    private UserService userService;


    @Test
    @DataComposeResource("UsersWithGroups.json")
    public void findOne() {
        UUID tenantOne = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID userGroupOne = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        UUID pablo = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID juan = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        UUID pedro = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

        UserGroup userGroup = userGroupService.findOne(tenantOne,userGroupOne);

        assertThat(userGroup).isNotNull();
        assertThat(userGroup.getName()).isEqualTo("group1");
        assertThat(userGroup.getUsers().stream().map(User::getId)).containsExactlyInAnyOrder(pablo, juan, pedro);
    }

    @Test
    @DataComposeResource("UsersWithGroups.json")
    public void delete() {
        UUID tenantOne = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID userGroupOne = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        UUID pablo = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

        Boolean deleted = userGroupService.delete(tenantOne,userGroupOne);
        assertThat(deleted).isTrue();

        UserGroup userGroup = userGroupService.findOne(tenantOne, userGroupOne);
        assertThat(userGroup).isNull();

        User user = userService.findOne(tenantOne, pablo);
        assertThat(user.getUserGroups().stream().map(UserGroup::getId)).doesNotContain(userGroupOne);
    }

    @Test
    public void insert() {
        UUID tenant = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        String userGroupName = "insertedUserGroup";

        UserGroupInsert userGroupInsert = new UserGroupInsert();
        userGroupInsert.setName(userGroupName);
        userGroupInsert.setTenantId(tenant);

        UserGroup userGroupInserted = userGroupService.insert(userGroupInsert);
        assertThat(userGroupInserted).isNotNull();
        assertThat(userGroupInserted.getName()).isEqualTo(userGroupName);
    }

    @Test
    @DataComposeResource("UsersWithGroups.json")
    public void addUsers() {
        UUID tenantOne = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID userGroupTwo = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
        UUID raquel = UUID.fromString("550e8400-e29b-41d4-a716-446655440004");

        IncludeUsers includeUsers = new IncludeUsers();
        includeUsers.setUserIds(newHashSet(raquel));

        userGroupService.addUsers(tenantOne, userGroupTwo, includeUsers);

        UserGroup userGroup = userGroupService.findOne(tenantOne, userGroupTwo);
        assertThat(userGroup.getUsers().stream().map(User::getId)).contains(raquel);
    }

    @Test
    @DataComposeResource("UsersWithGroups.json")
    public void removeUsers() {
        UUID tenantOne = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID userGroupTwo = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
        UUID juan = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");

        RemoveUsers removeUsers = new RemoveUsers();
        removeUsers.setUserIds(newHashSet(juan));

        userGroupService.removeUsers(tenantOne, userGroupTwo, removeUsers);

        UserGroup userGroup = userGroupService.findOne(tenantOne, userGroupTwo);
        assertThat(userGroup.getUsers().stream().map(User::getId)).doesNotContain(juan);
    }
}
