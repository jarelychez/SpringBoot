package com.webage.dao;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.webage.domain.Customer;

//  PART 4 (Optional):
//  We are using @TestPropertySource to override the value in application.yml.
//  This is done to reduce the possibility of a port conflict.
//  When ready, remove the @Disabled annotation and run the test.  It should pass.


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "api.server.port=27184")   // Psuedo-random.
//@TestPropertySource(properties = "api.server.port=#{ T(java.util.concurrent.ThreadLocalRandom).current().nextInt(1024, 65536) }")
public class ApiClientCustomersDaoTests {
    
    //  The Object we are using for our test entry point.  
    //  Remember, this object contains our RestClient:
    @Autowired
    ApiClientCustomersDao dao;

    //  Get the port to use for our WireMock server:   
    @Value("${api.server.port}")
    private int wiremockPort;

    //  Wiremock is used to intercept HTTP calls and provice mock responses:
    private WireMockServer wireMockServer;


    @BeforeEach
    public void setup() {
        // Start the WireMock server
        System.out.println("Wiremock Server will run on port: " + wiremockPort);
        wireMockServer = new WireMockServer(wireMockConfig().port(wiremockPort));
        wireMockServer.start();

        // Configure WireMock to intercept calls to http://localhost:<port>/customers.
        // The response will be a JSON string produced in another method:
        WireMock.configureFor("localhost", wireMockServer.port());
        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/customers"))
                .willReturn(
                    WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(makeTestData())
                ));
    }


    @Test
    @Disabled
    public void getAllCustomers() {
        //  Call our DAO.  Remember, this object contains our RestClient 
        //  which we use to call the API server.  But the RestClient is 
        //  using a URL based on the port defined above, so it reaches 
        //  the WireMock server instead.
        List<Customer> customers = dao.getAllCustomers();

        //  Our test data was only two customers:
        assert customers.size() == 2;
        assert customers.get(0).getName().equals("John Doe");
    }

    @AfterEach
    public void teardown() {
        // Stop the WireMock server after the test
        wireMockServer.stop();
    }

    
    //  Creating some test data using a List of Customers.
    //  Jackson object mapper turns it into JSON, then into a String:
    private static String makeTestData() {
        Customer c1 = new Customer();
        c1.setId(1);
        c1.setName("John Doe");
        c1.setEmail("john@example.com");

        Customer c2 = new Customer();
        c2.setId(2);
        c2.setName("Jane Doe");
        c2.setEmail("jane@example.com");

        ObjectMapper mapper = new ObjectMapper();
        try{
            return mapper.writeValueAsString(Arrays.asList(c1, c2));
        }
        catch(Exception e){
            throw new RuntimeException(e);
        } 
    }

}
