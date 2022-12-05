package com.github.courtandrey.simpledatascraperbot.test.servicetest;

import com.github.courtandrey.simpledatascraperbot.entity.request.HHVacancyRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.HabrCareerVacancyRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.exception.UnknownRequestException;
import com.github.courtandrey.simpledatascraperbot.service.RequestService;
import com.github.courtandrey.simpledatascraperbot.service.UserService;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class RequestServiceTest {
    @Autowired
    private RequestService requestService;
    @Autowired
    private UserService userService;

    private User generatedUser;

    @BeforeAll
    void generateUser() {
        long id = 0;
        while (userService.getUserById(id).isPresent()) {
            id = (long) (Math.random() * Long.MAX_VALUE);
        }

        User user = new User();
        user.setUserId(id);
        Set<Request> requests = new HashSet<>();
        for (int i=0; i<5; i++) {
            requests.add(generateRequest(HHVacancyRequest.class, user));
            requests.add(generateRequest(HabrCareerVacancyRequest.class, user));
        }
        user.setRequests(requests);
        generatedUser = user;
        userService.save(user);
    }

    @Test
    public void testCountRequestByUserId() {
        int count = requestService.countByUserId(generatedUser.getUserId());
        Assertions.assertEquals(10, count);
    }

    @Test
    @Order(Integer.MAX_VALUE)
    public void testAddRequestToUser() {
        HHVacancyRequest wrongRequest = new HHVacancyRequest();
        HabrCareerVacancyRequest wrongHabrRequest = new HabrCareerVacancyRequest();

        Assertions.assertThrows(Exception.class, () -> requestService.addRequestToUser(
                generatedUser.getUserId(), wrongRequest
        ));

        Assertions.assertThrows(Exception.class, () -> requestService.addRequestToUser(
                generatedUser.getUserId(), wrongHabrRequest
        ));

        HHVacancyRequest hhVacancyRequest = (HHVacancyRequest) generateRequest(HHVacancyRequest.class, generatedUser);

        requestService.addRequestToUser(generatedUser.getUserId(), hhVacancyRequest);

        Assertions.assertEquals(11, requestService.countByUserId(generatedUser.getUserId()));

        HabrCareerVacancyRequest habrCareerVacancyRequest = (HabrCareerVacancyRequest) generateRequest(HabrCareerVacancyRequest.class,
                generatedUser);

        requestService.addRequestToUser(generatedUser.getUserId(), habrCareerVacancyRequest);

        Assertions.assertEquals(12, requestService.countByUserId(generatedUser.getUserId()));
    }

    @Test
    public void testFindRequestByUserId() {
        Collection<Request> requests = requestService.findRequestsByUserId(generatedUser.getUserId());
        Assertions.assertEquals(10, requests.size());
        for (Request r:requests) {
            if (r instanceof HHVacancyRequest hhVacancyRequest) {
                Assertions.assertTrue(Hibernate.isInitialized(hhVacancyRequest.getRegions()));
            }
        }
    }
    @AfterAll
    public void clean() {
        userService.deleteUserById(generatedUser.getUserId());
    }


    private Request generateRequest(Class<? extends Request> requestClass, User user) {
        if (requestClass == HHVacancyRequest.class) {
            HHVacancyRequest hhVacancyRequest = new HHVacancyRequest();
            hhVacancyRequest.setUser(user);
            hhVacancyRequest.setExperience(HHVacancyRequest.Experience.BETWEEN_1_AND_3);
            hhVacancyRequest.setSearchText(UUID.randomUUID().toString());
            return hhVacancyRequest;
        } else if (requestClass == HabrCareerVacancyRequest.class) {
            HabrCareerVacancyRequest habrCareerVacancyRequest = new HabrCareerVacancyRequest();
            habrCareerVacancyRequest.setUser(user);
            habrCareerVacancyRequest.setLevel(HabrCareerVacancyRequest.Level.INTERN);
            habrCareerVacancyRequest.setSkill((int) (Math.random() * Integer.MAX_VALUE));
            return habrCareerVacancyRequest;
        }

        throw new UnknownRequestException("Unknown type of request");
    }
}
