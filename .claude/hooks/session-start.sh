#!/bin/bash
#
# SessionStart hook for Claude Code on the web.
#
# Configures Maven so that `mvn clean install` resolves all dependencies through
# the managed egress proxy of the remote environment. Two issues are handled:
#
#   1. The maven-resolver HTTP transport (Apache HttpClient) does NOT read the
#      JVM proxy system properties (https.proxyHost/Port from JAVA_TOOL_OPTIONS)
#      by default, so it bypasses the egress proxy and gets HTTP 403. We enable
#      `aether.connector.http.useSystemProperties` via MAVEN_OPTS so it honors
#      the proxy that is already configured for this session.
#
#   2. The egress policy allowlists repo1.maven.org but NOT the canonical Central
#      host repo.maven.apache.org (used by Maven's built-in super-POM), which is
#      blocked with 403. Both serve identical Maven Central content, so we mirror
#      every "central" request to repo1.maven.org via ~/.m2/settings.xml.
#
# The hook is idempotent and only does anything in the remote (web) environment;
# on a local machine with direct internet access it exits immediately.

set -euo pipefail

# Only relevant for Claude Code on the web (managed egress proxy). Skip locally.
if [ "${CLAUDE_CODE_REMOTE:-}" != "true" ]; then
  exit 0
fi

# 1) Mirror Maven Central (repo.maven.apache.org) to the allowlisted repo1 host.
mkdir -p "$HOME/.m2"
cat > "$HOME/.m2/settings.xml" << 'SETTINGS'
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              https://maven.apache.org/xsd/settings-1.0.0.xsd">
  <!--
    Egress environment: only repo1.maven.org is allowlisted, the canonical
    Central host repo.maven.apache.org is policy-blocked (403). Both are
    identical Maven Central, so route all "central" requests (repository AND
    plugin repository from the super-POM) to repo1.maven.org.
  -->
  <mirrors>
    <mirror>
      <id>central-repo1</id>
      <name>Maven Central via repo1 (allowlisted host)</name>
      <mirrorOf>central</mirrorOf>
      <url>https://repo1.maven.org/maven2</url>
    </mirror>
  </mirrors>
</settings>
SETTINGS

# 2) Make the maven-resolver HTTP transport use the JVM proxy system properties
#    so it routes through the egress proxy instead of connecting directly.
if [ -n "${CLAUDE_ENV_FILE:-}" ]; then
  echo 'export MAVEN_OPTS="${MAVEN_OPTS:-} -Daether.connector.http.useSystemProperties=true"' >> "$CLAUDE_ENV_FILE"
fi

echo "SORMAS Maven proxy/mirror configured for the web environment (~/.m2/settings.xml)."
