# Publishing sormas-api

The **Publish sormas-api to GitHub Packages** workflow runs automatically on every
push to `development`, regardless of which files changed. You can also run it
manually from GitHub Actions, selecting the branch or tag to publish. It runs the API tests and
publishes `de.symeda.sormas:sormas-api` using the version inherited from
`sormas-base/pom.xml`. Both release and `-SNAPSHOT` versions are supported.
Use a new version for each release; an existing release cannot be overwritten.
Keep the version on `development` as a `-SNAPSHOT` so repeated pushes can publish
new snapshot builds.
The workflow uses its repository's `GITHUB_TOKEN` with `packages: write` and
publishes to that repository's package registry, including when run in a fork.

Only `sormas-api` is built and deployed. The `github-packages` Maven profile
generates a flattened POM with resolved dependency versions and no parent
reference, so `sormas-base` does not need to be published. Normal builds do not
activate this profile.

Maven's `deploy` phase uploads the JAR, POM, and Maven metadata to GitHub Packages
after building and testing. It does not deploy the SORMAS application to a server.

## Publishing locally

Use JDK 17 and Maven 3.6.3 or later. Add this server entry to your Maven
`~/.m2/settings.xml` (merge it into any existing `servers` element):

```xml

<settings>
    <servers>
        <server>
            <id>github</id>
            <username>${env.GITHUB_ACTOR}</username>
            <password>${env.GITHUB_TOKEN}</password>
        </server>
    </servers>
</settings>
```

Set `GITHUB_ACTOR` to your GitHub username and `GITHUB_TOKEN` to a personal access
token (classic) with `write:packages` and access to the target repository. Keep
credentials outside the repository. From the repository root, run:

```sh
mvn -B -ntp -f sormas-api/pom.xml -Pgithub-packages clean deploy
```

The default destination is `SORMAS-Foundation/SORMAS-Project`. To publish to
another repository, add
`-Dgithub.packages.repository=https://maven.pkg.github.com/OWNER/REPOSITORY`,
using a lowercase owner. Keep `-f sormas-api/pom.xml` to select only this module.

To build and test the publishing configuration without uploading anything:

```sh
mvn -B -ntp -f sormas-api/pom.xml -Pgithub-packages clean verify
```

## Using the package

Configure a Maven server with ID `github` as above, using a token (classic) with
`read:packages` and repository access. Add the repository and dependency to the
consumer's POM, replacing `PUBLISHED_VERSION` with the published version:

```xml

<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/sormas-foundation/SORMAS-Project</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>

<dependencies>
<dependency>
    <groupId>de.symeda.sormas</groupId>
    <artifactId>sormas-api</artifactId>
    <version>PUBLISHED_VERSION</version>
</dependency>
</dependencies>
```

See [GitHub's Maven registry documentation](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)
for authentication details.
