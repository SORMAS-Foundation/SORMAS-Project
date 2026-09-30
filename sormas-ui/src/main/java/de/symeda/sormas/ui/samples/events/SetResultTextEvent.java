/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 *******************************************************************************/

package de.symeda.sormas.ui.samples.events;

/**
 * Command event: requests that the test result free-text field be set to a specific value.
 * A {@code null} text means "clear the field". Mirrors {@link SetTestResultEvent}.
 */
public class SetResultTextEvent {

    private final String resultText;
    private final String expectedCurrentText;

    public SetResultTextEvent(String resultText) {
        this(resultText, null);
    }

    /**
     * Conditional variant: the text is only replaced if the field is currently blank or still holds
     * {@code expectedCurrentText}, so user-entered text is never overwritten.
     */
    public SetResultTextEvent(String resultText, String expectedCurrentText) {
        this.resultText = resultText;
        this.expectedCurrentText = expectedCurrentText;
    }

    public String getResultText() {
        return resultText;
    }

    public String getExpectedCurrentText() {
        return expectedCurrentText;
    }
}
