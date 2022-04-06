package org.sormas.e2etests.pages.application.contacts;

import org.openqa.selenium.By;

public class ContactImportExportPage {

  public static final By CUSTOM_CONTACT_EXPORT = By.id("exportCustom");
  public static final By EXPORT_CONFIGURATION_DATA_ID_CHECKBOX_CONTACT =
      By.xpath("//label[text()='Contact ID']");
  public static final By EXPORT_CONFIGURATION_DATA_FIRST_NAME_CHECKBOX_CONTACT =
      By.xpath("//label[text()='First name of contact person']");
  public static final By EXPORT_CONFIGURATION_DATA_LAST_NAME_CHECKBOX_CONTACT =
      By.xpath("//label[text()='Last name of contact person']");
}
