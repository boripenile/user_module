package com.jardo.usermodule.hbn;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dbunit.Assertion;
import org.dbunit.DBTestCase;
import org.dbunit.DatabaseUnitException;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Ignore;

@Ignore
public class UMDatabaseTestCase extends DBTestCase {

	private static final String DATA_SET_PREFIX = "src/test/resources/dataSets/";

	private final FlatXmlDataSetBuilder dataSetBuilder;

	private final SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");

	protected void assertTableContent(IDataSet expectedDataSet, String tableName, String[] excludedColumns) throws SQLException, Exception {
		IDataSet actualDataSet = getConnection().createDataSet();

		ITable expectedTable = expectedDataSet.getTable(tableName);
		ITable actualTable = actualDataSet.getTable(tableName);

		if (excludedColumns != null) {
			expectedTable = DefaultColumnFilter.excludedColumnsTable(expectedTable, excludedColumns);
			actualTable = DefaultColumnFilter.excludedColumnsTable(actualTable, excludedColumns);
		}

		Assertion.assertEquals(expectedTable, actualTable);
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return null;
	}

	protected void fillDatabase(String dataSetFileName) throws DatabaseUnitException, SQLException, Exception {
		IDataSet dataSet = loadFlatXmlDataSet(dataSetFileName);
		resetAutoGeneratedColumn("um_user", "id");
		turnForeignKeyCheckingOff();
		DatabaseOperation.CLEAN_INSERT.execute(getConnection(), dataSet);
		turnForeignKeyCheckingOn();
	}

	@Override
	protected DatabaseOperation getSetUpOperation() throws Exception
	{
		return DatabaseOperation.NONE;
	}

	protected ReplacementDataSet loadFlatXmlDataSet(String fileName) throws FileNotFoundException, DataSetException {
		FileInputStream inputStream = new FileInputStream(DATA_SET_PREFIX + fileName);
		IDataSet dataSet = dataSetBuilder.build(inputStream);

		ReplacementDataSet result = new ReplacementDataSet(dataSet);
		return result;
	}

	protected Date parseDate(String dateStr) throws ParseException {
		return dateParser.parse(dateStr);
	}

	protected void resetAutoGeneratedColumn(String tableName, String columnName) throws SQLException, Exception {
		StringBuilder statement = new StringBuilder();
		statement.append("ALTER TABLE ");
		statement.append(tableName);
		statement.append(" ALTER COLUMN ");
		statement.append(columnName);
		statement.append(" RESTART WITH 1");

		getConnection().getConnection().prepareStatement(statement.toString()).execute();
	}

	protected void turnForeignKeyCheckingOff() throws Exception {
		getConnection().getConnection().prepareStatement("SET DATABASE REFERENTIAL INTEGRITY FALSE").execute();
	}

	protected void turnForeignKeyCheckingOn() throws Exception {
		getConnection().getConnection().prepareStatement("SET DATABASE REFERENTIAL INTEGRITY TRUE").execute();
	}

	public UMDatabaseTestCase() {
		super();
		this.dataSetBuilder = new FlatXmlDataSetBuilder();

		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "org.hsqldb.jdbcDriver");
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:hsqldb:hsql://localhost/user_module_test");
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "sa");
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "");
	}
}
