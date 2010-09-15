/*
 * Copyright (C) 2010 Francisco José Morero Peyrona. All Rights Reserved.
 *
 * This file is part of Tapas project: http://code.google.com/p/tapas-tpv/
 *
 * GNU Classpath is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the free
 * Software Foundation; either version 3, or (at your option) any later version.
 *
 * Tapas is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Tapas; see the file COPYING.  If not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.peyrona.tapas.utils;

import com.peyrona.tapas.mainFrame.MainFrame;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Francisco Morero Peyrona
 */
public class Utils
{
    private static NumberFormat nfCurrency = NumberFormat.getCurrencyInstance();
    private static NumberFormat nfNoSymbol = NumberFormat.getInstance();

    public static final int nEXIT_NO_EXIT   = -1;
    public static final int nEXIT_NO_ERROR  =  0;
    public static final int nEXIT_LAF_ERROR =  1;
    public static final int nEXIT_DB_ERROR  =  2;

    public static final char   cDecimalSep     = (new DecimalFormatSymbols()).getMonetaryDecimalSeparator();
    public static final String sCurrencySymbol = (new DecimalFormatSymbols()).getCurrencySymbol();

    public static final boolean bDEBUGGING = true;   // ¿Estamos en modo develop o debug?

    //------------------------------------------------------------------------//

    static
    {
        int nDigits = Currency.getInstance( Locale.getDefault() ).getDefaultFractionDigits();

        Utils.nfNoSymbol.setMinimumFractionDigits( nDigits );
        Utils.nfNoSymbol.setMaximumFractionDigits( nDigits );
    }

    //------------------------------------------------------------------------//

    /**
     * Formatea cantidades como moneda utilizando el default locale.
     * 
     * @param nAmount
     * @return
     */
    public static String formatAsCurrency( BigDecimal nAmount )
    {
        return nfCurrency.format( nAmount );
    }

    /**
     * Funciona como formatAsCurrency(...) pero no incluye el símbolo de la moneda.
     *
     * @param nAmount
     * @return
     */
    public static String formatLikeCurrency( BigDecimal nAmount )
    {
        return nfNoSymbol.format( nAmount );
    }

    public static BufferedImage ImageChooser()
    {
        BufferedImage     bufImage = null;
        PreviewImagePanel preview  = new PreviewImagePanel();
        JFileChooser      chooser  = new JFileChooser();
                          chooser.setAccessory( preview );
                          chooser.addPropertyChangeListener( preview );
                          chooser.addChoosableFileFilter( new ImageFilter() );
        int nAction =     chooser.showOpenDialog( MainFrame.getInstance() );

        if( nAction == JFileChooser.APPROVE_OPTION )
            bufImage = preview.getImage();
        
        return bufImage;
    }

    public static Image scaleImage( BufferedImage buffImage, int nCompWidth, int nCompHeight )
    {// FIXME: No va bien
        if( buffImage == null )
            return null;

        float nImgWidth  = buffImage.getWidth();
        float nImgHeight = buffImage.getHeight();
        float nScale     = nImgHeight / nImgWidth;

        if( nImgWidth > nCompWidth )
        {
            nImgWidth   = nCompWidth;
            nImgHeight *= nScale;
        }

        if( nImgHeight > nCompHeight )
        {
            nImgHeight = nCompHeight;
            nImgWidth *= nScale;
        }
                   
        return buffImage.getScaledInstance( Math.max( 16, (int)nImgWidth ),
                                            Math.max( 16, (int)nImgHeight ),
                                            Image.SCALE_SMOOTH );
    }

    public static void printError( Throwable th, Level level, String sMessage, int nExitCode )
    {
        if( sMessage != null )
            System.out.println( sMessage );

        if( th != null )
        {
            th.printStackTrace( System.err );
            // FIXME: hacer que el logger escriba a un fichero en disco
            Logger.getLogger( "Tapas.Logger" ).log( Level.SEVERE, sMessage, th );
        }

        if( nExitCode != nEXIT_NO_EXIT )
            System.exit( nExitCode );
    }

    public static void showError( final Throwable th, final Level level, final String sMessage, final int nExitCode )
    {
        printError( th, level, sMessage, nExitCode );

        EventQueue.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                JOptionPane.showMessageDialog( null, sMessage );

                if( nExitCode != nEXIT_NO_EXIT )
                    System.exit( nExitCode );
            }
        } );
    }
}