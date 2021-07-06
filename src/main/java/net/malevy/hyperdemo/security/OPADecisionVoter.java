package net.malevy.hyperdemo.security;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static net.malevy.hyperdemo.security.OPAInputFactory.buildHttpInput;
import static net.malevy.hyperdemo.security.OPAInputFactory.buildUserInput;


@Slf4j
public class OPADecisionVoter implements AccessDecisionVoter<Object> {

    private final OkHttpClient httpClient;
    private final String opaApiDecisionUrl;

    public OPADecisionVoter(OkHttpClient httpClient, String opaApiDecisionUrl) {

        // since we're tunneling a GET via a POST, it is
        // safe to retry.
        this.httpClient = httpClient.newBuilder()
                .retryOnConnectionFailure(true)
                .build();
        this.opaApiDecisionUrl = opaApiDecisionUrl;
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object o, Collection<ConfigAttribute> collection) {

        if (!(o instanceof FilterInvocation)) return ACCESS_ABSTAIN;

        FilterInvocation fi = (FilterInvocation) o;
        Map<String, Object> http = buildHttpInput(fi.getRequest());
        Map<String, Object> user = buildUserInput(authentication);

        Map<String, Object> input = new HashMap<>();
        input.put("user", user);
        input.put("http", http);

        JSONObject payload = new JSONObject();
        payload.put("input", input);

        RequestBody body = RequestBody.create(payload.toString(), MediaType.parse("application/json"));
        Request decisionRequest = new Request.Builder()
                .url(opaApiDecisionUrl)
                .post(body)
                .build();

        Response response;
        try {
            response = httpClient.newCall(decisionRequest).execute();

            String responseAsString = response.body().string();
            if (! response.isSuccessful()) {
                log.error("failed authorization check: {} ", responseAsString);
                throw new SecurityException("failed authorization check");
            }

            JSONObject doc = new JSONObject(responseAsString);
            if(! doc.has("result")) {
                log.error("unrecognized response format from PDP: missing 'result' key");
                return ACCESS_DENIED;
            }

            return doc.getBoolean("result")
                    ? ACCESS_GRANTED
                    : ACCESS_DENIED;

        } catch (IOException e) {
            log.error("failed authorization check", e);
            throw new SecurityException("failed authorization check");
        }

    }




}
