package org.ikasan.rest.dashboard;

import org.ikasan.rest.dashboard.util.TestBatchInsert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.Resource;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SystemEventController.class)
@WebAppConfiguration
@EnableWebMvc
@ContextConfiguration(
    {
        "/substitute-components.xml"
    }
)
public class SystemControllerTest extends AbstractRestMvcTest
{
    public static final String SYSTEMEVENTS_JSON = "/data/system-events.json";


    protected MockMvc mvc;
    @Autowired
    WebApplicationContext webApplicationContext;

    @Resource
    TestBatchInsert batchInsert;

    @Before
    public void setUp()
    {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    public void harvest_system_event_success() throws Exception
    {
        String uri = "/rest/harvest/systemevents";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri)
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(super.loadDataFile(SYSTEMEVENTS_JSON))).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), status);
        String content = mvcResult.getResponse().getContentAsString();

        Assert.assertEquals("Batch insert size == 2", 2, batchInsert.getSize());
    }

    @Test
    public void test_exception_bad_post_json() throws Exception
    {
        String uri = "/rest/harvest/systemevents";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content("bad json")).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.BAD_REQUEST.value(), status);
        String content = mvcResult.getResponse().getContentAsString();
        assertThat(content,containsString( "An error has occurred attempting to perform a batch insert of SystemEvents!"));

    }
}
