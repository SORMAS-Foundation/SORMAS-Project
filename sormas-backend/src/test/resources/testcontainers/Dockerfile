FROM postgres:10.18-alpine

RUN apk update --no-cache && \
    apk upgrade --no-cache && \
# this line below keeping fixed musl lib version is unnecessary under docker 20.10.3+ but musl-dev has to be normally included in apk add, then
    apk add --no-cache --repository  http://dl-cdn.alpinelinux.org/alpine/v3.13/main/ 'musl<1.2.2-r3' 'musl-dev<1.2.2-r3' && \
    apk add --no-cache openssl curl tzdata py-pip python3-dev postgresql-dev postgresql-contrib make gcc py3-psutil  && \
    pip install pgxnclient && \
    pgxnclient install temporal_tables
    
COPY setup_sormas.sh /docker-entrypoint-initdb.d/
COPY sormas_schema.sql /tmp/sormas_schema.sql
