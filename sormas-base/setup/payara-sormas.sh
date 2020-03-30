#!/bin/sh
### BEGIN INIT INFO
# Provides:          payara-sormas
# Required-Start:    postgresql
# Required-Stop:     postgresql
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Payara-Server SORMAS
### END INIT INFO


ASADMIN="/opt/payara5/bin/asadmin"

case "$1" in
start)
    su --login payara --command "$ASADMIN start-domain --domaindir /opt/domains sormas"
    ;;
stop)
    su --login payara --command "$ASADMIN stop-domain --domaindir /opt/domains sormas"
    ;;
restart)
    su --login payara --command "$ASADMIN restart-domain --domaindir /opt/domains sormas"
    ;;
*)
    echo "usage: $0 (start|stop|restart|help)"
esac
