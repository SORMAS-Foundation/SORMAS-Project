# SORMAS Web App

The current version of the SORMAS Frontend is a web application that uses the **Vaadin** web framework in version 8.
Vaadin consists of a server-side framework and a client-side engine.
The engine runs in the browser as JavaScript code, renders the user interface and passes user interaction to the server.
The UI logic of an application runs as a Java servlet in a Java application server.

The data exchange between client and server takes place Vaadin-internally in json format and is limited to transmitting only the actual changes.
The data transfer takes place via an encrypted HTTPS connection.

On the client-side additional JavaScript libraries are used to display components like maps and charts.

The user interface is divided according to the main modules of SORMAS:
Cases, Aggregate, Contacts, Events, Samples, Messages, Statistics, Settings and User Management.
In addition, there is a dashboard that gives an overview of the data.


There is also an [angular-based prototype](https://github.com/sormas-foundation/SORMAS-Angular) of an alternative web application.

