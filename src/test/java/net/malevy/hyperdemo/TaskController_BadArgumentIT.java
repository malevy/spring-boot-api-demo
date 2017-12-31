package net.malevy.hyperdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.malevy.hyperdemo.models.viewmodels.TaskInputVM;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HypermediaDemoApplication.class})
@WebAppConfiguration
public class TaskController_BadArgumentIT {

    @Autowired
    private WebApplicationContext context;
    MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void whenTheRequestBodyIsBad_ReturnBadRequest() throws Exception {

        TaskInputVM taskInput = new TaskInputVM();
        ObjectMapper mapper = new ObjectMapper();
        String requestContent = mapper.writeValueAsString(taskInput);

        RequestBuilder rb = put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent)
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(rb)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("each task must have a title"));

    }

}
