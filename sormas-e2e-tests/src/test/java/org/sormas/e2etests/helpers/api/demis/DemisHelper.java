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
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */
package org.sormas.e2etests.helpers.api.demis;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.specification.RequestSpecification;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.net.ssl.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.RestAssuredClient;

@Slf4j
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
  public void okHttp() {

    OkHttpClient.Builder newBuilder = new OkHttpClient.Builder();
    newBuilder = configureToIgnoreCertificate(newBuilder);

    OkHttpClient client = newBuilder.build();

    Request request =
        new Request.Builder()
            .url("https://10.210.11.214:443/auth/realms/LAB/protocol/openid-connect/token")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .post(
                RequestBody.create(
                    MediaType.parse("application/x-www-form-urlencoded"),
                    "client_id=demis-adapter&client_secret=secret_client_secret&username=test-lab999&grant_type=password"))
            .build();
    Response response = client.newCall(request).execute();

    System.out.println("Response body: " + response.body().string());
  }

  private static OkHttpClient.Builder configureToIgnoreCertificate(OkHttpClient.Builder builder) {
    try {

      KeyStore keyStore = KeyStore.getInstance("PKCS12");
      FileInputStream clientCertificateContent =
          new FileInputStream(
              "C:\\Users\\Razvan\\Downloads\\demis\\DEMIS-Adapter-2.0.1\\config\\DEMIS-test-lab999_CSM026304641.p12");
      keyStore.load(clientCertificateContent, "W7JDGJOVJ7".toCharArray());

      KeyManagerFactory keyManagerFactory =
          KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      keyManagerFactory.init(keyStore, "W7JDGJOVJ7".toCharArray());

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
      sslContext.init(
          keyManagerFactory.getKeyManagers(), trustAllCerts, new java.security.SecureRandom());
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
      log.error("Certificate problem: " + e);
    }
    return builder;
  }

  @SneakyThrows
  public void loginRequest() {
    okHttp();
  }
}
