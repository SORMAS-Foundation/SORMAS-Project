/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.sormas.e2etests.pages.application.tasks;

import org.openqa.selenium.By;

public class CreateNewTaskPage {
  public static final By TASK_POPUP = By.xpath("(//div[@class='popupContent'])[2]");
  public static final By TASK_TYPE_COMBOBOX = By.cssSelector(".v-window #taskType input+div");
  public static final By TASK_TYPE_INPUT = By.cssSelector(".v-window #taskType input");
  public static final By SUGGESTED_START_DATE_INPUT =
      By.cssSelector(".v-window #suggestedStart_date input");
  public static final By DUE_DATE_DATE_INPUT = By.cssSelector(".v-window #dueDate_date input");
  public static final By SUGGESTED_START_TIME_INPUT =
      By.cssSelector(".v-window #suggestedStart_time input");
  public static final By SUGGESTED_START_TIME_COMBOBOX =
      By.cssSelector(".v-window #suggestedStart_time input+div");
  public static final By DUE_DATE_TIME_INPUT = By.cssSelector(".v-window #dueDate_time input");
  public static final By DUE_DATE_TIME_COMBOBOX =
      By.cssSelector(".v-window #dueDate_time input+div");
  public static final By ASSIGNED_TO_COMBOBOX = By.cssSelector(".v-window #assigneeUser input+div");
  public static final By ASSIGNED_TO_INPUT = By.cssSelector(".v-window #assigneeUser input");
  public static final By PRIORITY_COMBOBOX = By.cssSelector(".v-window #priority input+div");
  public static final By PRIORITY_INPUT = By.cssSelector(".v-window #priority input");
  public static final By COMMENTS_ON_TASK_TEXTAREA = By.cssSelector(".v-window #creatorComment");
  public static final By COMMENTS_ON_EXECUTION_TEXTAREA =
      By.cssSelector(".v-window #assigneeReply");
  public static final By TASK_STATUS_OPTIONS =
      By.cssSelector(".v-window #taskStatus .v-radiobutton label");
  public static final By SAVE_BUTTON = By.cssSelector(".v-window #commit");
  public static final By SELECTED_TASK_CONTEXT = By.cssSelector(".v-window [id='taskContext']");
}
