package com.hzi

import static com.kms.katalon.core.testdata.TestDataFactory.findTestData

import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI


public class TestDataConnector {

	/**
	 * Returns the value of the corresponding key from the given internal database. The columns needs to be named "key" and "value" for column 1 and 2.	
	 * @param dbName
	 * @param key
	 * @return
	 */
	static String getValueByKey(String dbName, String key) {
		TestData data = findTestData(dbName)

		String value = TestDataConnector.findDataByKey(data, key)
		println('found value: ' + value + ' for key:' + key)
		return value
	}

	static String findDataByKey(TestData data, String key) {
		int keyIndex, valueIndex
		(keyIndex, valueIndex) = TestDataConnector.determineColumnIndeces(data)

		List allData = data.getAllData()

		String value = 'nothing found'
		allData.each{row->
			String rowKey = row.get(keyIndex)

			if (rowKey == key) {
				value = row.get(valueIndex)
			}
		}

		return value
	}

	static determineColumnIndeces(TestData data) {
		String[] columns = data.getColumnNames()
		int keyColumnIndex = 0
		int valueColumnIndex = 0

		columns.eachWithIndex{ col, index ->
			if (col.equalsIgnoreCase('key')) {
				keyColumnIndex = index.value
			}

			if (col.equalsIgnoreCase('value')) {
				valueColumnIndex = index.value
			}
		}

		return [keyColumnIndex, valueColumnIndex]
	}
}
