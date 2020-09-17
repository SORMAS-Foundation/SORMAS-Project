# How to create and add certificates?

This guide explains how to:
 * create a new self-signed certificate, used for SORMAS to SORMAS communication
 * set up the server address list file
 * add certificates of other SORMAS instances to the local truststore
 * add other servers to the local server list
   
### Prerequisites

Java is needed, because the keytool is used for certificate import. <br/>
See [Installing Java](SERVER_SETUP.md#java-11)

### Using the certificate generation script

1. Run ``bash ./generate-cert.sh``
2. If the ``sormas2sormas`` directory is not found, you will be prompted to provide its path.
3. If the ``SORMAS_PROPERTIES`` environment variable is not available, the script will search for the ``sormas.properties`` 
file in ``/opt/domains/sormas/sormas.properties`` by default. If it is not found there, you will be prompted to provide 
the path to the ``sormas.properties`` file.
4. For the generation of the certificate, the following data is needed: a password for the certificate key store, a *Common Name* (CN), 
    an *Organization* (O) and a password for the REST user to be used when sharing data through the REST api. These may be set in environment variables (recommended), or provided 
    manually as the script executes.
    * The password environment variable should be named ``SORMAS_S2S_CERT_PASS``. Please note that the password has to be 
    at least 6 characters, or you will be prompted for a new one.
    * the *Common Name* environment variable should be named ``SORMAS_S2S_CERT_CN``.<br/>
    **Important**: for Germany, this value should be the SurvNet Code Site. <br/>
    E.g. *2.03.1.01.*
    * the *Organization* (O) environment variable should be named ``SORMAS_S2S_CERT_ORG``.<br/>
    **Important**: for Germany, this value should be the name of the Health Department (Gesundheitsamt) 
    to which the SORMAS instance will be assigned. <br/>
    E.g. *GA Braunschweig*
    * the REST user password environment variable should be named ``SORMAS_S2S_REST_PASSWORD``.
    Please note that the password has to be at least 12 characters, or you will be prompted for a new one.
    
5. After providing the requested data, the certificate files will be generated. <br/>
   The generated certificate has a validity of 3 years. 
   The certificate files will be available in the root SORMAS directory, in the folder ``/sormas2sormas``.
6. A CSV file containing the access data for this instance will also be generated in the folder ``/sormas2sormas``.
   It will be named ``server-access-data.csv``.
   The file will contain on the first two columns of the first row the Common Name and the Organization, as provided
   when creating the certificate and the REST user password as the third column.<br/>
7. The generated ``.p12`` file should not be shared with third parties. <br/>
   The generated ``.crt`` file will be verified and shared with other SORMAS instances, from which this instance
   will be able to request data. Conversely, in order to enable other SORMAS instances to request data from this 
   instance, their certificate files should be obtained and added to the local truststore. The ``server-access-data.csv``
   file will also have to be shared so that the access data of this instance is known to other instances. 
   More details can be found in the next section.
8. The relevant properties will be automatically set by the script in the ``sormas.properties`` file.

### Adding a new certificate to the Truststore

To enable other SORMAS instances to send and receive data from this instance, their certificate must be added to the 
truststore of this instance. Furthermore, the access data of other instances must be added to the local server
list. To complete this setup, please follow the next steps:
1. Run ``bash ./import-to-truststore.sh``
2. If the ``sormas2sormas`` directory is not found, you will be prompted to provide its path.
3. If the ``SORMAS_PROPERTIES`` environment variable is not available, the script will search for the ``sormas.properties`` 
   file in ``/opt/domains/sormas/sormas.properties`` by default. If it is not found there, you will be prompted to provide 
   the path to the ``sormas.properties`` file.
4. If ``sormas2sormas.truststore.p12`` is not found in the folder ``/sormas2sormas``, it will be created. 
    The truststore password may be provided in an environment variable ``SORMAS_S2S_TRUSTSTORE_PASS``.
    * If the aforementioned environment variable is not available, the truststore password will be searched in the 
    ``sormas.properties`` file.
    * If it is not found there, you will be prompted to provide the truststore password.
    * The relevant properties will be automatically set by the script in the ``sormas.properties`` file.
5. If the server address list file ``server-list.csv`` is not found in the folder ``/sormas2sormas``, it will also be created.
6. You will be prompted to provide the file name of the certificate to be imported. This certificate should be located
in the ``/sormas2sormas`` folder. Please provide the name including the extension. E.g ``mycert.crt``
7. After providing the requested data, the certificate will be imported to the truststore.
8. Next you will be prompted to provide the `URL` and the `user password` of the **REST** api the certificate belongs to.
    > The `user password` should be provided together with the certificate. 
    It should be the same password that is requested while generating the certificate. 
9. The new server information will be added to the ``server-list.csv`` file, then you will be able to select the new server in the application to share data with it.
10. You may now delete the ``.crt`` file.

### SORMAS to SORMAS Feature
In the application the sharing feature will get enabled after the certificate is generated and at least one other certificate is imported.  