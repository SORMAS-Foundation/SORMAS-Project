# How to create and add certificates?

This guide explains how to:

* create a new self-signed certificate, used for SORMAS to SORMAS communication
* set up the server address list file
* add certificates of other SORMAS instances to the local truststore
* add other servers to the local server list
* handling self-signed ssl certificates on test systems

## Prerequisites

Java is needed, because the keytool is used for certificate import. <br/>
See [Installing Java](SERVER_SETUP.md#java-11)

## Using the certificate generation script

1. Run ``bash ./s2s-generate-cert.sh``
2. If the ``SORMAS2SORMAS_DIR`` environment variable is not available, the script will search for ``/opt/sormas2sormas`` by default.
If it is not found there, you will be prompted to provide the pat to the *sormas2sormas* directory.
3. If the ``SORMAS_DOMAIN_DIR`` environment variable is not available, the script will search for ``/opt/domains/sormas`` by default.<br>
   If it is not found there, you will be prompted to provide the path to the *sormas domain directory*.
   >If you don't have a local sormas installation, for example you are using the docker environment,
   >you can specify any existing directory and after the script finishes you will find a ``sormas.properties`` file there
   >that contains the necessary configuration that must be added to the ``sormas.properties`` file of your installation
4. For the generation of the certificate, the following data is needed:
   an identifier of the *Organization*, the name of the *Organization*, the host name of the SORMAS server, the **https** port of the server,
   a password for the certificate keystore and a password for the REST user to be used when sharing data through the REST api.
   These may be set in environment variables (recommended), or provided manually as the script executes.

    * the identifier of the *Organization* environment variable should be named ``SORMAS_ORG_ID``.
    This variable is also used as *Common Name* (CN) of the certificate<br/>
    **Important**: for Germany, this value should be the SORMAS SurvNet Code Site (e.g. 2.99.1.01. if the regular Code Site was 1.99.1.01.). <br/>
    * the name of the organization *Organization* (O) environment variable should be named ``SORMAS_ORG_NAME``.<br/>
    **Important**: for Germany, this value should be the name of the Health Department (Gesundheitsamt)
    to which the SORMAS instance will be assigned. <br/>
    E.g. *GA Musterhausen*
    * the host name variable should be named ``SORMAS_HOST_NAME``. <br/>
    E.g. *sormas.gesundheitsamt-musterhausen.de*
    * the https port environment variable should be named ``SORMAS_HTTPS_PORT``. If it is not found, you will be prompted to provide it.
    If you press enter without typing a port number the default 443 will be used.
    * The password environment variable should be named ``SORMAS_S2S_CERT_PASS``. Please note that the password has to be
    at least 6 characters, or you will be prompted for a new one.
    * the REST user password environment variable should be named ``SORMAS_S2S_REST_PASSWORD``.
    Please note that the password has to be at least 12 characters, or you will be prompted for a new one.

5. After providing the requested data, the certificate files will be generated. <br/>
   The generated certificate has a validity of 3 years.
   The certificate files will be available in the root SORMAS directory, in the folder ``/sormas2sormas``.
6. A CSV file containing the access data for this instance will also be generated in the folder ``/sormas2sormas``.
   It will be named ``{host name}-server-access-data.csv``.
   The file will contain the organization identifier, organization name, host name and the REST user password.<br/>
7. The generated ``.p12`` file should not be shared with third parties. <br/>
   The generated ``.crt`` file will be verified and shared with other SORMAS instances, from which this instance
   will be able to request data. Conversely, in order to enable other SORMAS instances to request data from this
   instance, their certificate files should be obtained and added to the local truststore. The ``server-access-data.csv``
   file will also have to be shared so that the access data of this instance is known to other instances.
   More details can be found in the next section.
8. The relevant properties will be automatically set by the script in the ``sormas.properties`` file.

## Adding a new certificate to the Truststore

To enable other SORMAS instances to send and receive data from this instance, their certificate must be added to the
truststore of this instance. Furthermore, the access data of other instances must be added to the local server
list. To complete this setup, please follow the next steps:
1. Run ``bash ./s2s-import-to-truststore.sh``
2. If the ``SORMAS2SORMAS_DIR`` environment variable is not available, the script will search for ``/opt/sormas2sormas`` by default.
If it is not found there, you will be prompted to provide the path to the *sormas2sormas* directory.
3. If the ``SORMAS_DOMAIN_DIR`` environment variable is not available, the script will search for ``/opt/domains/sormas`` by default.
   If it is not found there, you will be prompted to provide the path to the *sormas domain directory*.
   >If you don't have a local sormas installation, for example you are using the docker environment,
   >you can specify any existing directory and after the script finishes you will find a ``sormas.properties`` file there
   >that contains the necessary configuration that must be added to the ``sormas.properties`` file of your installation

4. If ``sormas2sormas.truststore.p12`` is not found in the folder ``/sormas2sormas``, it will be created.
    The truststore password may be provided in an environment variable ``SORMAS_S2S_TRUSTSTORE_PASS``.
    * If the aforementioned environment variable is not available, the truststore password will be searched in the
    ``sormas.properties`` file.
    * If it is not found there, you will be prompted to provide the truststore password.
    * The relevant properties will be automatically set by the script in the ``sormas.properties`` file.
5. If the server address list file ``server-list.csv`` is not found in the folder ``/sormas2sormas``, it will also be created.
6. You will be prompted to provide the *host name* of the organization that's certificate is being imported.
   If the certificate was generated with the `s2s-generate-cert.sh` script, the identifier can be found at the beginning of the file.
   This certificate should be located in the ``/sormas2sormas`` folder.
7. After providing the requested data, the certificate will be imported to the truststore.
8. The content of the ``server-access-data.csv`` provided together with the certificate will be copied to the ``server-list.csv`` file.
9. You may now delete the ``.crt`` and ``server-access-data.csv`` files.

10. *Optional for test systems and other systems with self-signed ssl certificates* <br>
    You must import the SSL certificate of the other server into the ``cacerts.jks`` of your sormas domain.
    * For getting the SSL certificate you can use ``openssl`` <br>
        e.g.
        ```shell script
        openssl s_client -showcerts -servername sormas.gesundheitsamt-musterhausen.de -connect sormas.gesundheitsamt-musterhausen.de:443 </dev/null | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > certificate.cer
        ```
    * To import the SSL certificate you can use ``keytool`` <br>
        e.g.
        ```shell script
        keytool -importcert -trustcacerts -noprompt -keystore /opt/domains/sormas/config/cacerts.jks -alias sormas_dev -storepass changeit -file certificate.cer
        ```
        Note that the alias can be used only once.

After the certificate is generated and at least one other certificate is imported,
on some pages of the application you will see a new box with a *Share* button and information about sharing.
