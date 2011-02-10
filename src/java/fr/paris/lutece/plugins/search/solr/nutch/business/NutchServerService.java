/*
 * Copyright (c) 2002-2009, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.search.solr.nutch.business;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;

import java.net.MalformedURLException;


/**
 * This service provides an instance of SolrServer.
 *
 */
public final class NutchServerService
{
    private static final String PROPERTY_SOLR_SERVER_URL = "solr.nutch.server.address";
    private static final String SOLR_SERVER_URL = AppPropertiesService.getProperty( PROPERTY_SOLR_SERVER_URL );
    private static NutchServerService _instance;
    private SolrServer _solrServer;

    /**
     * Private constructor that creates the SolrServer.
     */
    private NutchServerService(  )
    {
        _solrServer = createSolrServer( SOLR_SERVER_URL );
    }

    /**
     * Return the instance.
     * @return the instance.
     */
    public static NutchServerService getInstance(  )
    {
        if ( _instance == null )
        {
            _instance = new NutchServerService(  );
        }

        return _instance;
    }

    /**
     * Returns the SolrServer.
     * @return the SolrServer
     */
    public SolrServer getSolrServer(  )
    {
        return _solrServer;
    }

    /**
    * Creates the SolrServer.
    * @param strServerUrl the server url
    * @return the SolrServer.
    */
    private SolrServer createSolrServer( String strServerUrl )
    {
        try
        {
            return new CommonsHttpSolrServer( strServerUrl );
        }
        catch ( MalformedURLException e )
        {
            AppLogService.error( e.getMessage(  ), e );

            return null;
        }
    }
}
