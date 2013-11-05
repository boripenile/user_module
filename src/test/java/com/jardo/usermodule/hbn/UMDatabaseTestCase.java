package com.jardo.usermodule.hbn;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;

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

public abstract class UMDatabaseTestCase extends DBTestCase {

	private IDataSet initialDataSet;

	private final FlatXmlDataSetBuilder dataSetBuilder;

	protected abstract IDataSet createInitialDataSet() throws Exception;

	protected void assertTableContent(IDataSet expectedDataSet, String tableName, String[] excludedColumns) throws SQLException, Exception {
		IDataSet actualDataSet = getConnection().createDataSet();

		ITable expectedTable = expectedDataSet.getTable(tableName);

		ITable actualTable = actualDataSet.getTable(tableName);
		if (excludedColumns != null) {
			actualTable = DefaultColumnFilter.excludedColumnsTable(actualTable, excludedColumns);
		}

		Assertion.assertEquals(expectedTable, actualTable);
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		if (initialDataSet == null) {
			initialDataSet = createInitialDataSet();
		}
		return initialDataSet;
	}

	protected void fillDatabase(String dataSetFileName) throws DatabaseUnitException, SQLException, Exception {
		IDataSet dataSet = loadFlatXmlDataSet(dataSetFileName);
		resetAutoGeneratedColumn("um_user", "id");
		turnForeignKeyCheckingOff();
		DatabaseOperation.CLEAN_INSERT.execute(getConnection(), dataSet);
		turnForeignKeyCheckingOn();
	}

	protected ReplacementDataSet loadFlatXmlDataSet(String fileName) throws FileNotFoundException, DataSetException {
		FileInputStream inputStream = new FileInputStream(fileName);
		IDataSet dataSet = dataSetBuilder.build(inputStream);

		ReplacementDataSet result = new ReplacementDataSet(dataSet);
		return result;
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
		this.initialDataSet = null;
		this.dataSetBuilder = new FlatXmlDataSetBuilder();

		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "org.hsqldb.jdbcDriver");
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:hsqldb:hsql://localhost/user_module_test");
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "sa");
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "");
	}
}
