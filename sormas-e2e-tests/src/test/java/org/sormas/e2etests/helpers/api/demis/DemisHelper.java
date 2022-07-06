/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.sormas.e2etests.helpers.api.demis;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import javax.net.ssl.*;
import lombok.SneakyThrows;
import okhttp3.*;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.RestAssuredClient;

public class DemisHelper {

  private final RestAssuredClient restAssuredClient;
  private final ObjectMapper objectMapper;
  private final RunningConfiguration runningConfiguration;
  private RequestSpecification requestSpecification;
  private final boolean logRestAssuredInfo;

  @Inject
  public DemisHelper(
      RunningConfiguration runningConfiguration,
      RestAssuredClient restAssuredClient,
      ObjectMapper objectMapper,
      @Named("LOG_RESTASSURED") boolean logRestAssuredInfo) {
    this.restAssuredClient = restAssuredClient;
    this.objectMapper = objectMapper;
    this.runningConfiguration = runningConfiguration;
    this.logRestAssuredInfo = logRestAssuredInfo;
  }

  @SneakyThrows
  private RequestSpecification request() {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    String baseDemis = "https://10.210.11.214:443";
    final String restEndpoint = "/auth/realms/LAB/protocol/openid-connect/token";
    // RestAssured.baseURI = runningConfiguration.getEnvironmentUrlForMarket(locale) + restEndpoint;
    RestAssured.baseURI = baseDemis + restEndpoint;
    Filter filters[];
    if (logRestAssuredInfo) {
      filters =
          new Filter[] {
            new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssured()
          };
    } else {
      filters = new Filter[] {new AllureRestAssured()};
    }

    KeyStore keyStore = KeyStore.getInstance("PKCS12");
    keyStore.load(
        new FileInputStream(
            "C:\\Users\\Razvan\\Downloads\\demis\\DEMIS-Adapter-2.0.1\\config\\DEMIS-test-lab999_CSM026304641.p12"),
        "W7JDGJOVJ7".toCharArray());

    org.apache.http.conn.ssl.SSLSocketFactory clientAuthFactory =
        new org.apache.http.conn.ssl.SSLSocketFactory(keyStore, "W7JDGJOVJ7");

    // set the config in rest assured
    SSLConfig config =
        new SSLConfig().with().sslSocketFactory(clientAuthFactory).and().allowAllHostnames();

    requestSpecification =
        RestAssured.given()
            .config(RestAssured.config().sslConfig(config))
            .relaxedHTTPSValidation()
            //            .config(
            //                RestAssured.config()
            //                    .sslConfig(
            //                        new SSLConfig()
            //                            .trustStore(
            //                                new File(
            //
            // "C:\\Users\\Razvan\\Downloads\\demis\\DEMIS-Adapter-2.0.1\\config\\DEMIS-test-lab999_CSM026304641.p12"),
            //                                "W7JDGJOVJ7")
            //                            .allowAllHostnames()))
            //            .relaxedHTTPSValidation()

            //            .trustStore(
            //
            // "C:\\Users\\Razvan\\Downloads\\demis\\DEMIS-Adapter-2.0.1\\config\\DEMIS-test-lab999_CSM026304641.p12",
            //                "W7JDGJOVJ7")
            .contentType("application/x-www-form-urlencoded; charset=utf-8")
            .formParam("client_secret", "secret_client_secret")
            .formParam("username", "test-lab999")
            .formParam("grant_type", "password");

    return requestSpecification
        //        .config(
        //            RestAssured.config()
        //                .encoderConfig(
        //                    EncoderConfig.encoderConfig()
        //                        .appendDefaultContentCharsetToContentTypeIfUndefined(false)))
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .filters(Arrays.asList(filters).get(0));
  }

  private List<X509Certificate> getCertificatesFromTrustStore() throws Exception {
    KeyStore truststore = KeyStore.getInstance("JKS");
    truststore.load(new FileInputStream("d:\certs.jsk"), "mypassword".toCharArray());

    PKIXParameters params = new PKIXParameters(truststore);
    Set<TrustAnchor> trustAnchors = params.getTrustAnchors();


    List<X509Certificate> certificates = trustAnchors.stream()
            .map(TrustAnchor::getTrustedCert)
            .collect(Collectors.toList());
    return certificates;
  }

  @SneakyThrows
  public void okHttp() {
    List<X509Certificate> certificates = getCertificatesFromTrustStore();

    Builder certificateBuilder =  new HandshakeCertificates.Builder();
    for (X509Certificate x509Certificate : certificates) {
      certificateBuilder.addTrustedCertificate(x509Certificate);
    }
    HandshakeCertificates handshakeCertificates =  certificateBuilder.build();



    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder = configureToIgnoreCertificate(builder);

    OkHttpClient client = builder.build();

    // OkHttpClient client = new OkHttpClient().newBuilder().build();

    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
    RequestBody body =
        RequestBody.create(
            mediaType,
            "client_id=demis-adapter&client_secret=secret_client_secret&username=test-lab999&grant_type=password");
    Request request =
        new Request.Builder()
            .url("https://10.210.11.214:443/auth/realms/LAB/protocol/openid-connect/token")
            .method("POST", body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build();
    Response response = client.newCall(request).execute();
  }

  @SneakyThrows
  public void loginRequest() {
    okHttp();
    // request().post().then().extract().response().getBody().asString();

    // restAssuredClient.sendRequest();
    //    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    //    List<Task> listOfContacts = List.of(task);
    //    objectMapper.writeValue(out, listOfContacts);
    //    restAssuredClient.sendRequest(
    //        Request.builder()
    //            .method(Method.POST)
    //            .path(TASKS_PATH + "push")
    //            .body(out.toString())
    //            .build());
  }

  private static OkHttpClient.Builder configureToIgnoreCertificate(OkHttpClient.Builder builder) {
    try {
      // Create a trust manager that does not validate certificate chains
      final TrustManager[] trustAllCerts =
          new TrustManager[] {
            new X509TrustManager() {
              @Override
              public void checkClientTrusted(
                  java.security.cert.X509Certificate[] chain, String authType)
                  throws CertificateException {}

              @Override
              public void checkServerTrusted(
                  java.security.cert.X509Certificate[] chain, String authType)
                  throws CertificateException {}

              @Override
              public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
              }
            }
          };

      // Install the all-trusting trust manager
      final SSLContext sslContext = SSLContext.getInstance("SSL");
      sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
      // Create an ssl socket factory with our all-trusting manager
      final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

      builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
      builder.hostnameVerifier(
          new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
              return true;
            }
          });
    } catch (Exception e) {
    }
    return builder;
  }
}
