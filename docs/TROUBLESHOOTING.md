# Troubleshooting

Please consult this collection of solutions to common problems if you have any issues before issuing a support request or asking developers for help. Also note that this resource has only been added recently and will be extended in the future. If you have encountered (and fixed) any issue that you think would be worth adding to this list, please don't hesitate to let us know!

## IDE Troubleshooting: eclipse

### Deployment Problems

Unfortunately, when using eclipse together with the Payara Tools, there are a number of deployment problems that you might run into. Examples of these include:

* ClassDefNotFoundExceptions after deploying the artifacts and logging in to the web app
* Error messages in eclipse telling you that the deployment failed

There are a couple of things you can do to fix these problems:

* Do a Maven update for all projects
* Stop and restart the server
* Re-deploy the server artifacts

If the problem occurred right after you've pulled new code from GitHub, your safest bet is probably to start with the Maven update. For most other problems, a simple re-deployment or, if necessary, server restart should suffice.

### News Feeds Polling

When running eclipse with JDK 11, you might encounter the following error message: `An internal error occurred during: "Polling news feeds".  javax/xml/bind/JAXBContext`. To fix it, disable `Window --> Preferences --> General --> News --> "Enable automatic news polling"`.
