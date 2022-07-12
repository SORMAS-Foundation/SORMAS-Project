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

package org.sormas.e2etests.helpers.api.demis.okhttpclient;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import javax.net.ssl.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SormasOkHttpClient {

  private static final String CERTIFICATE_TYPE = "PKCS12";
  private static final String SSL = "SSL";

  private static okhttp3.OkHttpClient.Builder newBuilder;

  /** Returns new default client */
  public static okhttp3.OkHttpClient getClient() {
    newBuilder = new okhttp3.OkHttpClient.Builder();
    log.info("Returning new OkHttpClient");
    return newBuilder.build();
  }

  /** Returns new configured client */
  public static okhttp3.OkHttpClient getClient(String pathToP12Certificate, String certPassphrase) {
    newBuilder =
        configureToIgnoreCertificate(
            new okhttp3.OkHttpClient.Builder(), pathToP12Certificate, certPassphrase);
    log.info("Returning new OkHttpClient configured with certificate");
    return newBuilder.build();
  }

  @SneakyThrows
  private static okhttp3.OkHttpClient.Builder configureToIgnoreCertificate(
      okhttp3.OkHttpClient.Builder builder, String path, String password) {
    if (password.isEmpty() || path.isEmpty()) {
      throw new Exception("None of provided certificate path or password should be empty!");
    }
    try {

      KeyStore keyStore = KeyStore.getInstance(CERTIFICATE_TYPE);

      FileInputStream clientCertificateContent = null;

      try {
        clientCertificateContent = new FileInputStream(path);
      } catch (IOException ioException) {
        throw new IOException(
            String.format("Unable to find certificate under path -> [ %s ]", path));
      }

      keyStore.load(clientCertificateContent, password.toCharArray());

      builder.sslSocketFactory(
          getSSLSocket(keyStore, password), (X509TrustManager) getTrustableManager()[0]);
      builder.hostnameVerifier(
          new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
              return true;
            }
          });
    } catch (Exception any) {
      throw new Exception(
          String.format(
              "Failed to configure certificate over OkHttpClient due to ->[ %s ]",
              any.getMessage()));
    }
    return builder;
  }

  /** TrustManager that does not validate certificate chains */
  private static TrustManager[] getTrustableManager() {

    return new TrustManager[] {
      new X509TrustManager() {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
            throws CertificateException {}

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
            throws CertificateException {}

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
          return new java.security.cert.X509Certificate[] {};
        }
      }
    };
  }

  @SneakyThrows
  private static SSLSocketFactory getSSLSocket(KeyStore keyStore, String certPassphrase) {
    KeyManagerFactory keyManagerFactory =
        KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    keyManagerFactory.init(keyStore, certPassphrase.toCharArray());

    SSLContext sslContext = SSLContext.getInstance(SSL);
    sslContext.init(
        keyManagerFactory.getKeyManagers(),
        getTrustableManager(),
        new java.security.SecureRandom());

    return sslContext.getSocketFactory();
  }
}
