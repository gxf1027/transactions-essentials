/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.datasource.xa;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import javax.transaction.xa.Xid;

/**
 * 
 * 
 * 
 * An abstract superclass for all XidFactory implementations. This class
 * provides the functionality to create really unique XIDs.
 * 
 * 
 * 
 * 
 */
public abstract class AbstractXidFactory implements XidFactory
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(AbstractXidFactory.class);

   private static final int MAX_LENGTH_OF_COUNTER = 8;
//default scope for testing issue 10086
   static long counter = 0;

    // to make sure that XIDs for the same
    // combination of TM,TID are still unique

    protected static void incCounter ()
    {
        counter++;
    }

    protected static long getCounter ()
    {
        return counter;
    }

    public AbstractXidFactory ()
    {
        super ();

    }
    
    /**
     * @see com.atomikos.datasource.xa.XidFactory
     */

    public Xid createXid ( String tid , String resourcename )
    {
    	
    	if ( resourcename.getBytes().length + MAX_LENGTH_OF_COUNTER > XID.MAXBQUALSIZE ) {
    		// see case 73086
    		throw new IllegalArgumentException ( "Value too long: " + resourcename );
    	}
    	
        // first increment counter to make sure it is
        // different from the last call that was done
        // by the SAME tid (works because calls within
        // one TID are serial)
        incCounter ();
        return new XID ( tid, resourcename + getCounter () );
    }

}
