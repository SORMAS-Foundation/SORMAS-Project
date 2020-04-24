package com.hzi

import java.text.SimpleDateFormat

import org.apache.commons.lang.RandomStringUtils
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords

public class Helper {

	static String generateString(String prefix, int length) {
		String charset = (('a'..'z') + ('A'..'Z') + ('0'..'9')).join()
		String randomString = RandomStringUtils.random(length, charset.toCharArray())

		return prefix + '-' + randomString
	}

	static TestObject createTestObjectWithXPath(String selector) {
		return new TestObject().addProperty('xpath', ConditionType.EQUALS, selector)
	}

	static WebElement findChildElement(TestObject object, String selector) {
		WebElement webElement = WebUiBuiltInKeywords.findWebElement(object)
		WebElement childElement = webElement.findElement(By.xpath(selector))

		return childElement
	}

	static Date generateRandomDateFromPastToNow(long past) {
		long now = new Date().getTime()
		long difference = now-past

		int size = difference.toString().size()

		double random = Math.random()
		long offset = (random * difference * Math.pow(1, 10)).trunc().toLong()

		Date d = new Date(past + offset)

		return d
	}

	static String formatDateToString(String format, Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat(format)

		return formatter.format(date)
	}
}
