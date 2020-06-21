FROM postgres:10-alpine

RUN apk update --no-cache && \
    apk upgrade --no-cache && \
    apk add --no-cache openssl curl tzdata py-pip postgresql-dev postgresql-contrib make gcc musl-dev && \
    pip install pgxnclient && \
    pgxnclient install temporal_tables
