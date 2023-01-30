package org.sormas.e2etests.steps.api.demisLabNotification;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.Date;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

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
  public ArrayList<Coding> type1;
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
