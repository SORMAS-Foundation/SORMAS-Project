package de.symeda.sormas.api.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date> {
 
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
 
    @Override
    public String marshal(Date date) throws Exception {
        return dateFormat.format(date);
    }
 
    @Override
    public Date unmarshal(String string) throws Exception {
        return dateFormat.parse(string);
    }
 
}