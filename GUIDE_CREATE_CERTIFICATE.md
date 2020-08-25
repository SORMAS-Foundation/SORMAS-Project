# How to create a new certificate?

This guide explains how to create a new self-signed certificate, used for SORMAS to SORMAS communication.
   
### Using the certificate generation script

1. Run ``bash ./generate-cert.sh``
2. For the generation of the certificate, the following data is needed: a password, a *Common Name* (CN) 
    and an *Organization* (O). These may be set in environment variables (recommended), or provided 
    manually as the script executes.
    * The password environment variable should be named ``SORMAS_S2S_CERT_PASS``.
    * the *Common Name* environment variable should be named ``SORMAS_S2S_CERT_CN``.<br/>
    **Important**: for Germany, this value should be the SurvNet Code Site. <br/>
    E.g. *2.03.1.01.*
    * the *Organization* (O) environment variable should be named ``SORMAS_S2S_CERT_ORG``.<br/>
    **Important**: for Germany, this value should be the name of the Health Department (Gesundheitsamt) 
    to which the SORMAS instance will be assigned. <br/>
    E.g. *GA Braunschweig*
3. After providing the requested data, the certificate files will be generated. <br/>
   The generated certificate has a validity of 3 years. 
   The certificate files will be available in the root SORMAS directory, in the folder ``/sormas2sormas``.
4. The generated ``.p12`` file should not be shared with third parties. <br/>
   The generated ``.crt`` file will be verified and shared with other SORMAS instances, from which this instance
   will be able to request data. Conversely, in order to enable other SORMAS instances to request data from this 
   instance, their certificate files should be obtained and added to the local truststore. More details can be found
   in the next section.
5. If the ``SORMAS_PROPERTIES`` environment variable is available, the relevant properties will be 
    automatically set by the script.
    * Else, the properties which need to be added will be displayed in the console after the script finishes executing.
    * Please note these properties and add them to the ``sormas.properties`` file. This should be located in the 
    ``/domains/sormas`` folder.
    * Example output:
    ```
    sormas.properties file was not found. 
    Please add the following properties to the sormas.properties file:
    sormas2sormas.keyAlias=mycertificate
    sormas2sormas.keyPassword=changeit
    ```

### Adding a new certificate to the Truststore

