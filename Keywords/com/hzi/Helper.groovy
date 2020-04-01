package com.hzi

import org.apache.commons.lang.RandomStringUtils

import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject

public class Helper {

	static String generateString(String prefix, int length) {
		String charset = (('a'..'z') + ('A'..'Z') + ('0'..'9')).join()
		String randomString = RandomStringUtils.random(length, charset.toCharArray())

		return prefix + '-' + randomString
	}

	static TestObject createTestObjectWithXPath(String selector) {
		return new TestObject().addProperty('xpath', ConditionType.EQUALS, selector)
	}
}
