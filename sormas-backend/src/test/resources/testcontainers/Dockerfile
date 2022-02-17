FROM postgres:10.18-alpine

RUN apk add --no-cache --repository  http://dl-cdn.alpinelinux.org/alpine/v3.13/main/ 'musl<1.2.2-r3' 'musl-dev<1.2.2-r3' && \
    apk add --no-cache openssl curl tzdata py-pip python3-dev postgresql-dev postgresql-contrib make gcc py3-psutil  && \
    pip install --no-cache-dir pgxnclient && \
    pgxnclient install temporal_tables
    
COPY setup_sormas.sh /docker-entrypoint-initdb.d/
COPY sormas_schema.sql /tmp/sormas_schema.sql
