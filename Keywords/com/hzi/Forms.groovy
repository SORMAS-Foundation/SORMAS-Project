package com.hzi

import org.apache.commons.lang.RandomStringUtils
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

public class Forms {

	@Keyword
	def String generateString(String prefix, int length) {
		String charset = (('a'..'z') + ('A'..'Z') + ('0'..'9')).join()
		String randomString = RandomStringUtils.random(length, charset.toCharArray())

		return prefix + '-' + randomString
	}
}
