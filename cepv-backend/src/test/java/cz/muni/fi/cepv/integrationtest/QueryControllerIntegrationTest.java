package cz.muni.fi.cepv.integrationtest;

import cz.muni.fi.cepv.web.LinkUtil;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author xgarcar
 */
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class QueryControllerIntegrationTest extends BaseIntegrationTest {


    @Test
    public void test01_findQueryResources() throws Exception {
        mockMvc.perform(get(LinkUtil.QUERIES)
                .contentType(MediaType.APPLICATION_JSON)
                .with(httpBasic(environment.getProperty("spring.security.user"),
                        environment.getProperty("spring.security.password"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size", is(20)))
                .andExpect(jsonPath("$.page.totalElements", is(12)))
                .andExpect(jsonPath("$.page.totalPages", is(1)))
                .andExpect(jsonPath("$.page.number", is(0)));
    }

    @Test
    public void test02_findQueryResourcesByExperiment() throws Exception {
        mockMvc.perform(get(LinkUtil.EXPERIMENT_QUERIES, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .with(httpBasic(environment.getProperty("spring.security.user"),
                        environment.getProperty("spring.security.password"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size", is(20)))
                .andExpect(jsonPath("$.page.totalElements", is(12)))
                .andExpect(jsonPath("$.page.totalPages", is(1)))
                .andExpect(jsonPath("$.page.number", is(0)));
    }

    @Test
    public void test03_findQueryResourcesByNode() throws Exception {
        mockMvc.perform(get(LinkUtil.NODE_QUERIES, "PC002")
                .contentType(MediaType.APPLICATION_JSON)
                .with(httpBasic(environment.getProperty("spring.security.user"),
                        environment.getProperty("spring.security.password"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size", is(20)))
                .andExpect(jsonPath("$.page.totalElements", is(4)))
                .andExpect(jsonPath("$.page.totalPages", is(1)))
                .andExpect(jsonPath("$.page.number", is(0)));
    }

    @Test
    public void test04_findOneQueryResource() throws Exception {
        mockMvc.perform(get(LinkUtil.QUERY, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .with(httpBasic(environment.getProperty("spring.security.user"),
                        environment.getProperty("spring.security.password"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deploymentTime", is("2014-10-10T08:00:10.070")));
    }

    @Test
    public void test05_findQueryResourcesByExperimentAndNode() throws Exception {
        mockMvc.perform(get(LinkUtil.EXPERIMENT_NODE_QUERIES, 1L, "PC001")
                .contentType(MediaType.APPLICATION_JSON)
                .with(httpBasic(environment.getProperty("spring.security.user"),
                        environment.getProperty("spring.security.password"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size", is(20)))
                .andExpect(jsonPath("$.page.totalElements", is(8)))
                .andExpect(jsonPath("$.page.totalPages", is(1)))
                .andExpect(jsonPath("$.page.number", is(0)));
    }

    @Test
    public void test06_findFilteredQueryResourcesByExperimentAndNode() throws Exception {
        mockMvc.perform(get(LinkUtil.EXPERIMENT_NODE_QUERIES_FILTER, 1L, "PC001")
                .contentType(MediaType.APPLICATION_JSON)
                .with(httpBasic(environment.getProperty("spring.security.user"),
                        environment.getProperty("spring.security.password")))
                .param("gtDeploymentTime", "20141010080010069")
                .param("ltDeploymentTime", "20141010080010071"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size", is(20)))
                .andExpect(jsonPath("$.page.totalElements", is(1)))
                .andExpect(jsonPath("$.page.totalPages", is(1)))
                .andExpect(jsonPath("$.page.number", is(0)));
    }

    @Test
    public void test07_createQuery() throws Exception {
        mockMvc.perform(post(LinkUtil.EXPERIMENT_NODE_QUERIES, 1L, "PC001")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"deploymentTime\": \"2014-10-10T08:04:38.178\", \"content\": \"Content from test\"}")
                .with(httpBasic(environment.getProperty("spring.security.user"),
                        environment.getProperty("spring.security.password"))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", is(LinkUtil.getQueryResourceLink(13L))));
    }

    @Test
    public void test08_createQueryWithNullRequiredFields() throws Exception {
        mockMvc.perform(post(LinkUtil.EXPERIMENT_NODE_QUERIES, 1L, "PC001")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\": \"Content from test\"}")
                .with(httpBasic(environment.getProperty("spring.security.user"),
                        environment.getProperty("spring.security.password"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test09_findNonExistingQueryResource() throws Exception {
        mockMvc.perform(get(LinkUtil.QUERY, 1000L)
                .contentType(MediaType.APPLICATION_JSON)
                .with(httpBasic(environment.getProperty("spring.security.user"),
                        environment.getProperty("spring.security.password"))))
                .andExpect(status().isNotFound());
    }

    @Test
    public void test10_updateQueryWithPatch() throws Exception {
        mockMvc.perform(patch(LinkUtil.QUERY, 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\": \"Modified content from test\"}")
                .with(httpBasic(environment.getProperty("spring.security.user"),
                        environment.getProperty("spring.security.password"))))
                .andExpect(status().isNoContent());
    }

    @Test
    public void test11_findOneQueryAttributeResource() throws Exception {
        mockMvc.perform(get(LinkUtil.QUERY_ATTRIBUTE, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .with(httpBasic(environment.getProperty("spring.security.user"),
                        environment.getProperty("spring.security.password"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key", is("Test attribute key 1")))
                .andExpect(jsonPath("$.value", is("Test attribute value 1")));
    }

    @Test
    public void test12_createQueryAttribute() throws Exception {
        mockMvc.perform(post(LinkUtil.QUERY_QUERY_ATTRIBUTES, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"key\": \"Query attribute key from test\", \"value\": \"Value from test\"}")
                .with(httpBasic(environment.getProperty("spring.security.user"),
                        environment.getProperty("spring.security.password"))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", is(LinkUtil.getQueryAttributeResourceLink(12L))));
    }

    @Test
    public void test13_createQueryAttributeWithNullRequiredFields() throws Exception {
        mockMvc.perform(post(LinkUtil.QUERY_QUERY_ATTRIBUTES, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"value\": \"Value from test\"}")
                .with(httpBasic(environment.getProperty("spring.security.user"),
                        environment.getProperty("spring.security.password"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test14_updateQueryAttributeWithPut() throws Exception {
        mockMvc.perform(put(LinkUtil.QUERY_ATTRIBUTE, 1L, 12L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"key\": \"Modified query attribute key from test\", \"value\": \"Value from test\"}")
                .with(httpBasic(environment.getProperty("spring.security.user"),
                        environment.getProperty("spring.security.password"))))
                .andExpect(status().isNoContent());
    }

    @Test
    public void test15_updateQueryAttributeWithPatch() throws Exception {
        mockMvc.perform(patch(LinkUtil.QUERY_ATTRIBUTE, 1L, 12L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"value\": \"Modified value from test\"}")
                .with(httpBasic(environment.getProperty("spring.security.user"),
                        environment.getProperty("spring.security.password"))))
                .andExpect(status().isNoContent());
    }

    @Test
    public void test16_findQueryExecutionResourcesByQuery() throws Exception {
        mockMvc.perform(get(LinkUtil.QUERY_QUERY_EXECUTIONS, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .with(httpBasic(environment.getProperty("spring.security.user"),
                        environment.getProperty("spring.security.password"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size", is(20)))
                .andExpect(jsonPath("$.page.totalElements", is(35)))
                .andExpect(jsonPath("$.page.totalPages", is(2)))
                .andExpect(jsonPath("$.page.number", is(0)));
    }

    @Test
    public void test17_createQueryExecution() throws Exception {
        mockMvc.perform(post(LinkUtil.QUERY_QUERY_EXECUTIONS, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"executionTime\": \"2014-10-10T08:04:38.178\"}")
                .with(httpBasic(environment.getProperty("spring.security.user"),
                        environment.getProperty("spring.security.password"))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", is(LinkUtil.getQueryExecutionResourceLink(51L))));
    }

    @Test
    public void test18_createQueryExecutionWithInvalidCredentials() throws Exception {
        mockMvc.perform(post(LinkUtil.QUERY_QUERY_EXECUTIONS, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"executionTime\": \"2014-10-10T08:04:38.178\"}")
                .with(httpBasic(environment.getProperty("spring.security.user"),
                        environment.getProperty("spring.security.password") + "1")))
                .andExpect(status().isUnauthorized());
    }
}
