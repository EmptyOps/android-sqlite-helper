package com.hsquaretech.common.helpers.db.sqLiteService;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

/**
 * Service to upload files as a multi-part form data in background using HTTP POST with notification center progress
 * display.
 * 
 * @author alexbbb (Alex Gotev)
 * @author eliasnaur
 */
public class imbc_sqLiteService extends IntentService
{

    private static final String SERVICE_NAME = imbc_sqLiteService.class.getName();
    private static final String TAG = "AndroidUploadService";
    public static String NAMESPACE = "gam.startupjobs.lib.backgroundservice.intentservice.sqLiteService";

    /**
     * actions
     */
    private static final String initCreateUpgrade = ".imbc_sqLiteService.initCreateUpgrade";
   
    /**
     * 
     * @return
     */
    public static String getInitAction()
    {
        return NAMESPACE + initCreateUpgrade;
    }

    
    /**
     * 
     */
    public imbc_sqLiteService()
    {
        super(SERVICE_NAME);
    }

    /**
     * 
     */
    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    /**
     * 
     */
    @Override
    protected void onHandleIntent(Intent intent)
    {
    	// log.singleton().debug(" imbc_sqLiteService::onHandleIntent ");

        if (intent != null)
        {
            final String action = intent.getAction();

            if ( action.equals( getInitAction() ) )
            {
            	Bundle extras = intent.getExtras();
            	initCreateUpgrade( extras.getInt("is_db_creat_or_upgrade"), extras.getInt("oldVersion"), extras.getInt("newVersion")); 
            }
        }
    }

    /**
     * 
     */
    private void initCreateUpgrade(int is_db_creat_or_upgrade, int oldVersion, int newVersion) 
    {

		
		/**
		 * Lang
		 */
		if( is_db_creat_or_upgrade == 1 )
		{

			
		}
		else if( is_db_creat_or_upgrade == 2 ) 
		{
			/**
			 * create them 
			 */
			if( oldVersion <= 11 )
			{
				
			}

		} 
    }

    @Override
    public void onDestroy()
    {
    	super.onDestroy();
    }

}
