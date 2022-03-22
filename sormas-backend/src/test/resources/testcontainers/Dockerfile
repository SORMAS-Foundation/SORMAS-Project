FROM postgres:10.18-alpine

RUN apk add --no-cache --repository http://dl-cdn.alpinelinux.org/alpine/v3.13/main/ musl=1.2.2-r1 musl-dev=1.2.2-r1 && \
    apk add --no-cache openssl=1.1.1n-r0 curl=7.79.1-r0 tzdata=2021a-r0 py-pip=20.3.4-r1 python3-dev=3.9.5-r2  \
    postgresql-dev=13.6-r0 postgresql-contrib=13.6-r0 make=4.3-r0 gcc=10.3.1_git20210424-r2 py3-psutil=5.8.0-r1  && \
    pip install --no-cache-dir pgxnclient==1.3.2 && \
    pgxnclient install temporal_tables

COPY setup_sormas_db.sh /docker-entrypoint-initdb.d/
COPY sormas_schema.sql /tmp/sormas_schema.sql
