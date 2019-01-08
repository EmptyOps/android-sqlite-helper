package com.hsquaretech.common.helpers.android_sqlite_helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.hsquaretech.common.helpers.android_sqlite_helper.sqLiteService.imbc_sqLiteService;

public class sqLiteHelper extends SQLiteOpenHelper
{
	private static Context ctx = null;
	private static sqLiteHelper sqLiteHelper;
	private SQLiteDatabase db = null;
	
	public final String create_tbl_config = "";

	public final String create_tbl_log = "";
	

	private final String create_tbl_chatlog = "";
	
	private final String create_tbl_imdc_rc_user = "";

	private final static String DATABASE_NAME = "he_sqlite.db";
	
	/**
	 * upgrade DATABASE_VERSION by 1 whenever new App or update is released <br>
	 * Incremented DATABASE_VERSION will make sure that onUpgrade is called after upgrade of APK and App is launched <br>
	 * Also make sure that new release of DB updates is applied to onCreate so the device that install new version directly will have that updates. 
	 */
	private final static int DATABASE_VERSION = 10;
	
	/**
	 * 1 if on create is called, 2 if on upgrade is called, 0 if nothing is called. 
	 */
	private int is_db_creat_or_upgrade = 0; 
	
	public static sqLiteHelper singleton( Context activityObj )
	{
		if( sqLiteHelper == null )
		{
			ctx = activityObj;
			sqLiteHelper = new sqLiteHelper( activityObj );
		}
		return sqLiteHelper;
	}

	public SQLiteDatabase dbSingleton()
	{
		if( db == null )
		{
			db = getWritableDatabase();
		}
		
		return db;
	}
	
	public sqLiteHelper(Context context)
	{
		super( context, DATABASE_NAME, null, DATABASE_VERSION );
		
		//singleton
		sqLiteHelper = this;
	}

	@Override
	public void onCreate( SQLiteDatabase database )
	{
		is_db_creat_or_upgrade = 1;
		initCreateUpgrade(database, 0, 0); 
	}

	/**
	 * needs to be implemented when a new/update version is released <br>
	 * do handle any upgrade of configuration key, other parameters and newly added tables etc
	 * 
	 */
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
	{
		// log.singleton().debug("onUpgrade called");
		is_db_creat_or_upgrade = 2;
		initCreateUpgrade(database, oldVersion, newVersion);
	}
	
	@Override
	public synchronized void close()
	{
		super.close();
		
		//close db
		dbSingleton().close();
		db = null; 
	}
	
	/*********************** Transaction support functions *************************/
	/**
	 * Transactions
	 * @since 26-05-2015
	 */
	public void startTransaction()
	{
		dbSingleton().beginTransaction();
	}
	
	
	
	/**
	 * Transactions
	 * @since 26-05-2015
	 */
	public void commitTransaction()
	{
		dbSingleton().setTransactionSuccessful();
		dbSingleton().endTransaction();
	}
	/*********************** Transaction support functions end *********************/
	
	
	/**
	 * 
	 * @param sql
	 * @param params
	 */
	public Cursor getRow(String sql, String[] params )
	{
		return dbSingleton().rawQuery( sql, params ); 
	}
	
	/**
	 * 
	 * @param sql
	 * @param params
	 */
	public void query(String sql, String[] params )
	{
		//dbSingleton().execSQL( sql, params );
		Cursor cu = dbSingleton().rawQuery(sql, params);

		cu.moveToFirst();
	    cu.close();  
	}

	
	/**
	 * 
	 */
	public Cursor executeQuery(String table, String[] columns,
                               String selection, String[] selectionArgs, String groupBy,
                               String having, String orderBy, String limit )
	{
		Cursor cursor = db.query(table, null, selection, selectionArgs, groupBy, having, orderBy, limit);
		if ( cursor.getCount() <= 0 )
		{
			cursor.close();
			return null;
		}
		return cursor;
	}
	
	/**
	 *
	 * @param sql
	 * @param params
	 */
	public Cursor executeQuery(String sql, String[] params )
	{
		// log.singleton().debug(" executeQuery  " + sql);
		Cursor cursor = getRow(sql, params);
		if ( cursor.getCount() <= 0 )
		{
			cursor.close();
			return null;
		}
		return cursor;
	}

	/**
	 *
	 * @param sql
	 * @param params
	 */
	public boolean isRowExist(String sql, String[] params )
	{
		Cursor cursor = getRow( sql, params );
		if ( cursor.getCount() <= 0 )
		{
			cursor.close();
			return false;
		}
		else
		{
			cursor.moveToFirst();
			cursor.close();
			return true;
		}
	}
	
	/**
	 *
	 * @param sql
	 * @param params
	 */
	public Cursor checkIfRowExist(String sql, String[] params )
	{
		Cursor cursor = getRow( sql, params );
		if ( cursor.getCount() <= 0 )
		{
			cursor.close();
			return null;
		}
		return cursor;
	}
	
	public boolean isTableExists(String tableName)
	{
	    Cursor cursor = checkIfRowExist("select DISTINCT tbl_name from sqlite_master where tbl_name = ?", new String[]{tableName});
	    if( cursor != null )
	    {
	    	return true; 
	    }
	    else 
	    {
	    	return false; 
	    }
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void updInsConfigKey(String key, String value )
	{
		query("INSERT OR REPLACE INTO config (c_key, c_value) " + 
			  "VALUES (?, ?)", new String[] { key, value });
	}

	/**
	 * 
	 * @param key
	 */
	public String getConfigKey(String key )
	{
		String c_value = "";
		
		Cursor cursor = checkIfRowExist( " SELECT c_value FROM config WHERE c_key=? " , new String[] { key });
		if( cursor != null && cursor.moveToNext() )
		{
			c_value = cursor.getString( cursor.getColumnIndex( "c_value" ) );
			cursor.close();
			return c_value;
		}
		
		return "";
	}

	/**
	 * 
	 * @param key
	 */
	public void deleteConfigKey( String key )
	{
		query( " DELETE FROM config WHERE c_key=? " , new String[] { key } );
	}

	/**
	 *
	 * @param key
	 */
	public void deleteConfigKeyWildCard( String key )
	{
		query( " DELETE FROM config WHERE c_key LIKE ? " , new String[] { key } );
	}

	/**
	 * handles initialization or upgrade of database
	 */
	public void initCreateUpgrade(SQLiteDatabase database, int oldVersion, int newVersion )
	{
		/**
		 * Lang
		 */
		if( is_db_creat_or_upgrade == 1 )
		{
			/**
			 * config
			 */
			database.execSQL( "CREATE TABLE IF NOT EXISTS config ("
								+ " c_key VARCHAR not null UNIQUE, " 						//		- need indexing
								+ " c_value VARCHAR not null );" );

			
			
			/**
			 * log
			 */
			database.execSQL( "CREATE TABLE IF NOT EXISTS log ("
												 + " run_id integer, " 
												 + " l_group_key VARCHAR null, "
												 + " l_description VARCHAR null, "
												 + " l_created_date VARCHAR null );" );

			/**
			 * cur_ada: list view cursor adapter table
			 */
			database.execSQL( "CREATE TABLE IF NOT EXISTS cur_ada ("
								+ " _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " //		- need indexing
								+ " label VARCHAR null, "								
								+ " desc TEXT null, "								
								+ " image TEXT null, "
								+ " href VARCHAR null, "
								+ " param VARCHAR null, "
								+ " mode TEXT null, "
								+ " id TEXT null "
							    + " ); " );

			/**
			 * recent filters data table
			 */
			database.execSQL( "CREATE TABLE IF NOT EXISTS recent_filter ("
								+ " _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " //		- need indexing
								+ " keyword VARCHAR null, "
								+ " city VARCHAR null, "
								+ " search_date datetime default current_timestamp, "
								+ " filter_type VARCHAR null, "
								+ " id TEXT null "
								+ " ); " );

			/**
			 * common data list table
			 */
			database.execSQL( "CREATE TABLE IF NOT EXISTS data_list ("
					+ " _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " //		- need indexing
					+ " data_mode VARCHAR null, "
					+ " id TEXT null, "
					+ " field_1 VARCHAR null, "
					+ " field_2 VARCHAR null, "
					+ " field_3 VARCHAR null, "
					+ " field_4 VARCHAR null, "
					+ " field_5 VARCHAR null, "
					+ " created_date datetime default current_timestamp, "
					+ " modified_date datetime null "
					+ " ); " );

			/**
			 * it is actually setting language in session so below is not standard way to do this 
			 */
			database.execSQL( "INSERT INTO config(c_key, c_value) VALUES ('lang', 'EN_US');" ); 
		}
		else if( is_db_creat_or_upgrade == 2 )
		{
			/**
			 * since config table structure is changed after version release of 11 ==> 1.1, 
			 * change the structure if it's older version is 11 
			 */
			if( oldVersion <= 11908 )
			{
				/**
				 * cur_ada: add mode field
				 */
				// log.singleton().debug(" oldVersion  ");
				database.execSQL( "ALTER TABLE cur_ada ADD COLUMN mode TEXT;" );
			}
			
		}
		
        final Intent intent = new Intent( ctx, imbc_sqLiteService.class);
        intent.setAction( imbc_sqLiteService.getInitAction() );
        intent.putExtra("is_db_creat_or_upgrade", is_db_creat_or_upgrade);
        intent.putExtra("oldVersion", oldVersion);
        intent.putExtra("newVersion", newVersion);
        ctx.startService(intent);
	}

	// update discription
	public void updateDesc(String desc, String id , String mode )
	{

			query( " UPDATE cur_ada SET desc=? WHERE id=? AND mode =? " , new String[] { desc, id , mode });

	}


	////////////////////// RECENT FILTER FUNCTIONS ///////////////////////////

	/**
	 *
	 */
	public void insertRecentFilter(String keyword, String city, String filter_type )
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDate = sdf.format(new Date());

		// log.singleton().debug(" insertRecentFilter  " + strDate);

		if(!rowExist(keyword , city))
		{
			query("INSERT OR REPLACE INTO recent_filter (keyword, city, filter_type, search_date) " +
					"VALUES (?, ?, ?, ?)", new String[]{keyword, city, filter_type, strDate});
		}
		else
		{
			query( " UPDATE recent_filter SET search_date=? WHERE keyword=? AND city =? " , new String[] { strDate, keyword , city });
		}
	}

	public boolean rowExist(String keyword , String city)
	{
		Cursor cursor = checkIfRowExist("SELECT keyword , city FROM recent_filter WHERE keyword= ? AND city= ? " , new String[] { keyword, city });
		if( cursor != null )
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	/**
	 *
	 */
	public Cursor getRecentSearches(int limit )
	{
		return executeQuery("SELECT DISTINCT rf.keyword, rf.city, rf.search_date  " +
							" FROM recent_filter AS rf " +
							" ORDER BY search_date DESC " +
							" LIMIT " + limit, null);
	}

	/**
	 *
	 */
	public Cursor getKeywordSuggestion(String query, int limit )
	{
		return executeQuery(" SELECT DISTINCT rf.keyword  " +
							" FROM recent_filter AS rf " +
							" WHERE rf.keyword LIKE ? AND rf.keyword<>'' " +
							" LIMIT " + limit, new String[] { query });
	}
////////////////////// RECENT FILTER FUNCTIONS END ///////////////////////////

    ////////////////////// DATA LIST FUNCTIONS ///////////////////////////
	/**
	 *
	 */
	public void insertLinkedinDataList(String data_mode, String field_1 )
	{
		// log.singleton().debug(" insertLinkedinDataList  " + field_1 + " , " + data_mode );
		query("INSERT INTO data_list (data_mode, field_1) " +
				"VALUES (?, ?)", new String[]{ data_mode, field_1 });
	}

	/**
	 *
	 */
	public void insertDataList(String data_mode, String field_1 )
	{
		query("INSERT OR REPLACE INTO data_list (data_mode, field_1) " +
				"VALUES (?, ?)", new String[] { data_mode, field_1 });
	}

	/**
	 *
	 */
	public void insertDataList(String data_mode, String field_1, String field_2 )
	{
		query("INSERT OR REPLACE INTO data_list (data_mode, field_1 , field_2) " +
				"VALUES (?, ? , ?)", new String[]{data_mode, field_1, field_2 });
	}
	/**
	 *
	 */
	public Cursor getDataList(String data_mode )
	{
		return executeQuery("SELECT DISTINCT field_1  " +
							"FROM data_list " +
							"WHERE data_mode = ? " +
							"ORDER BY _id DESC " , new String[] { data_mode } );
	}

	/**
	 *
	 */
	public Cursor getDataListAll(String data_mode )
	{
		return executeQuery("SELECT field_1, field_2   " +
				"FROM data_list " +
				"WHERE data_mode = ? " +
				"ORDER BY _id DESC " , new String[] { data_mode } );
	}

	/**
	 *
	 */
	public void deleteDataList(String data_mode, String field_1 )
	{
		query("DELETE FROM data_list " +
				"WHERE data_mode = ? AND field_1 = ? ", new String[] { data_mode, field_1 });
	}

	/**
	 *
	 */
	public void deleteDataListAll( String data_mode )
	{
		query("DELETE FROM data_list " +
			  "WHERE data_mode = ? ", new String[] { data_mode });
	}

	/**
	 *
	 */
	public void deleteTableDataListAll( String data_mode )
	{
		// log.singleton().debug(" deleteTableDataListAll  " + data_mode);
		query("DELETE FROM cur_ada " +
				"WHERE mode = ? ", new String[] { data_mode });
	}

	/**
	 *
	 */
	public void deleteTableRowDataAll(String data_mode , String id )
	{
		// log.singleton().debug(" deleteTableDataListAll  " + data_mode + " ,  " + id);
		query("DELETE FROM cur_ada " +
				"WHERE mode = ? AND id = ? ", new String[] { data_mode , id });
	}

	/**
	 *
	 */
	public int courserAdapterCount(Activity activityObj, String mode)
	{
		Cursor cursor = sqLiteHelper.singleton(activityObj).getRow("SELECT COUNT(1) as 'Cnt' FROM cur_ada where mode =  '" + mode + "' ", null);
		if (cursor != null)
		{
			cursor.moveToFirst();

			return cursor.getInt( cursor.getColumnIndex("Cnt") );
		}

		return 0;
	}

	/**
	 *
	 */
	public int courserAdapterCount1(Activity activityObj, String mode)
	{
		Cursor cursor = sqLiteHelper.singleton(activityObj).getRow("SELECT COUNT(1) as 'Cnt' FROM recent_filter ", null);
		if (cursor != null)
		{
			cursor.moveToFirst();

			return cursor.getInt( cursor.getColumnIndex("Cnt") );
		}

		return 0;
	}
////////////////////// DATA LIST FUNCTIONS END ///////////////////////////

}