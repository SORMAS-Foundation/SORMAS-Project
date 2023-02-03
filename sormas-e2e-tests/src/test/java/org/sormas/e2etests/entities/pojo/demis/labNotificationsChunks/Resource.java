package org.sormas.e2etests.entities.pojo.demis.labNotificationsChunks;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.apache.poi.hpsf.Section;

import java.util.ArrayList;
import java.util.Date;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Resource {
    public String resourceType;
    public Meta meta;
    public Identifier identifier;
    public String type;
    public Date timestamp;
    public ArrayList<Entry> entry;
    public String id;
    public String status;
    public ArrayList<Category> category;
    public Subject subject;
    public Date date;
    public ArrayList<Author> author;
    public String title;
    public ArrayList<Section> section;
    public Object name;
    public String gender;
    public String birthDate;
    public ArrayList<Address> address;
    public Organization organization;
    public ArrayList<Telecom> telecom;
    public ArrayList<Contact> contact;
    public Code code;
    public String valueString;
    public ArrayList<Interpretation> interpretation;
    public ArrayList<Note> note;
    public Method method;
    public Specimen specimen;
    public Date receivedTime;
    public Collection collection;
}
