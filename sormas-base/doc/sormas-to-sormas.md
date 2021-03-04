# SORMAS to SORMAS Interface

SORMAS to SORMAS is an interface for securely sharing data between different SORMAS instances.

## Prerequisites
* Instances should have the same version
* Instances should be behind an https server
* At least two instances with SORMAS to SORMAS set up, and both of them configured to know about each other

## Security
* Forced `https` connection: sending the data form one instance to another happens using the REST api dedicated for SORMAS to SORMAS communication using https request
* Encrypted data: the data sent is encrypted using a self signed certificate generated during feature setup
* Only users with district level role are allowed to share data between sormas instances, by default

**NOTE** The feature on the UI of an instance will be available only after the certificate has been set up and at least one certificate of another instance has been imported

## Features

After the setup, on *case* and *contact* pages will appear UI parts that allows sharing with other SORMAS instances.

On the detail page of a shared item there is an information box that displays the name of the target or source, the name of the user that shared the item, and the date when it has been shared.

### Share a single item
Sharing a single item can be done on the details page of a *case* or *contact*.

First the target instance should be selected form a list of available instances imported during setup.
There are also several options to chose from:
* Share associated contacts (only for cases): option for sharing the contacts of cases
* Share samples: option for sharing *samples*; will share also the samples of the associated contacts when sharing with associated contacts
* Hand over the ownership: by default shared data is readonly on the receiving instance, switching this option on will allow the editing in the receiving instance, but not on the sending one
* Pseudonymize personal data: clears all personal data like person name, birth day and address fields and geo coordinates
* Pseudonymize sensitive data: clears all personal and free text fields
* Comment: free text

### Share multiple items
Sharing multiple items is possible on the directory pages using the *Bulk edit mode* that allows the selection of multiple rows and clicking the *Share* action from *Bulk actions*.

## Technical details

Feature setup can be done with the provided scripts:
* `s2s-generate-cert.sh` is for creating a self signed certificate that will be used for encrypting data before sending to the other instance, and a csv file which contains the necessary data for recognizing and communicating to this server;
this file together with the certificate should be shared with others to set up their system to allow accepting share requests from this server.
* `s2s-import-to-truststore.sh` is for storing server information and certificate of other instances. This certificate is used when decrypting the date coming from other instances. The certificate is added into a trust store, and the server information is appended to the list of the known servers stored in a csv file.

The certificates are of type X.509 and generated using openssl.

The communication is going through the REST api using a dedicated user with `SORMAS_TO_SORMAS_CLIENT` role. This user is automatically created during system startup.
The encrypted data is transferred using POST requests and only the requests from known servers are accepted by the endpoints.

The data is encrypted/decrypted using the *Bouncy Castle Crypto APIs* java library.

## Setup
To set up the instances see [SORMAS to SORMAS certificate guide](../../GUIDE_SORMAS2SORMAS_CERTIFICATE.md)
