package net.malevy.hyperdemo.security;

import com.github.tomakehurst.wiremock.WireMockServer;
import net.malevy.hyperdemo.AuthMother;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static org.springframework.security.access.AccessDecisionVoter.ACCESS_DENIED;
import static org.springframework.security.access.AccessDecisionVoter.ACCESS_GRANTED;

public class OPADecisionVoterTests {

    final OkHttpClient client = new OkHttpClient();
    final OPADecisionVoter voter = new OPADecisionVoter(client);
    final WireMockServer server = new WireMockServer(8181);

    final String denyResponse = "{'result':false}";
    final String approveResponse = "{'result':true}";

    @BeforeEach
    public void preTest() {
        server.start();
    }

    @AfterEach
    public void postTest() {
        server.stop();
    }

    @Test
    public void denyAuthorization() {

        server.givenThat(post("/v1/data/api/allow")
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
                                .withBody(denyResponse)));

        HttpServletRequest request = new MockHttpServletRequest("GET", "https://example.com/api/tasks");
        FilterInvocation fi = new FilterInvocation(request, new MockHttpServletResponse(), new MockFilterChain());
        Authentication nonAuthenticated = AuthMother.authentication();

        Assertions.assertEquals(ACCESS_DENIED, voter.vote(nonAuthenticated, fi, new ArrayList<>()));
    }

    @Test
    public void approveAuthorization() {

        server.givenThat(post("/v1/data/api/allow")
                .willReturn(
                        aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
                                .withBody(approveResponse)));

        HttpServletRequest request = new MockHttpServletRequest("GET", "https://example.com/api/tasks");
        FilterInvocation fi = new FilterInvocation(request, new MockHttpServletResponse(), new MockFilterChain());
        Authentication nonAuthenticated = AuthMother.authentication(AuthMother.adminUser());

        Assertions.assertEquals(ACCESS_GRANTED, voter.vote(nonAuthenticated, fi, new ArrayList<>()));
    }

}
