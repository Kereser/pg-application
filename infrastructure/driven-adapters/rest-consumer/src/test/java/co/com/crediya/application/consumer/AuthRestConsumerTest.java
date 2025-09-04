package co.com.crediya.application.consumer;

import java.io.IOException;

import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import co.com.crediya.application.consumer.helper.AuthRestMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@Disabled
class AuthRestConsumerTest {

  private static AuthRestConsumer authRestConsumer;
  private static AuthRestMapper authRestMapper;

  private static MockWebServer mockBackEnd;

  @BeforeAll
  static void setUp() throws IOException {
    mockBackEnd = new MockWebServer();
    mockBackEnd.start();
    var webClient = WebClient.builder().baseUrl(mockBackEnd.url("/").toString()).build();
    authRestConsumer = new AuthRestConsumer(webClient, authRestMapper);
  }

  @AfterAll
  static void tearDown() throws IOException {

    mockBackEnd.shutdown();
  }

  @Test
  @DisplayName("Validate the function testGet.")
  void validateTestGet() {

    mockBackEnd.enqueue(
        new MockResponse()
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setResponseCode(HttpStatus.OK.value())
            .setBody("{\"state\" : \"ok\"}"));
    //    var response = authRestConsumer.testGet();

    //    StepVerifier.create(response)
    //        .expectNextMatches(objectResponse -> objectResponse.getState().equals("ok"))
    //        .verifyComplete();
  }

  @Test
  @DisplayName("Validate the function testPost.")
  void validateTestPost() {

    mockBackEnd.enqueue(
        new MockResponse()
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setResponseCode(HttpStatus.OK.value())
            .setBody("{\"state\" : \"ok\"}"));
    //    var response = authRestConsumer.testPost();
    //
    //    StepVerifier.create(response)
    //        .expectNextMatches(objectResponse -> objectResponse.getState().equals("ok"))
    //        .verifyComplete();
  }
}
