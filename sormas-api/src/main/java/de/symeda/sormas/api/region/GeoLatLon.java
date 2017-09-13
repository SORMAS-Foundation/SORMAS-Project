package de.symeda.sormas.api.region;

import java.io.Serializable;

public class GeoLatLon implements Serializable, Cloneable {

	private static final long serialVersionUID = 6016397482506424761L;

	private double lat;
    private double lon;

    public GeoLatLon() {
    }

    public GeoLatLon(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lon);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GeoLatLon other = (GeoLatLon) obj;
        if (Double.doubleToLongBits(lat) != Double
            .doubleToLongBits(other.lat)) {
            return false;
        }
        if (Double.doubleToLongBits(lon) != Double
            .doubleToLongBits(other.lon)) {
            return false;
        }
        return true;
    }

}
