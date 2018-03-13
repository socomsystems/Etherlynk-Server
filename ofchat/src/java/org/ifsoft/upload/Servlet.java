package org.ifsoft.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.*;

import net.lingala.zip4j.core.ZipFile;

import org.jivesoftware.util.*;
import org.jivesoftware.openfire.*;
import org.jivesoftware.openfire.auth.AuthFactory;
import org.jivesoftware.openfire.plugin.rest.*;

import org.jivesoftware.openfire.plugin.spark.Bookmark;
import org.jivesoftware.openfire.plugin.spark.Bookmarks;
import org.jivesoftware.openfire.plugin.spark.BookmarkManager;

public class Servlet extends HttpServlet
{
    private static final Logger Log = LoggerFactory.getLogger( Servlet.class );

    @Override
    protected void doPut( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        String authorization = request.getHeader("authorization");

        if (authorization == null)
        {
            response.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
            return;
        }

        String[] usernameAndPassword = BasicAuth.decode(authorization);

        if (usernameAndPassword == null || usernameAndPassword.length != 2) {
            response.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
            return;
        }

        try {
            AuthFactory.authenticate(usernameAndPassword[0], usernameAndPassword[1]);
        } catch (Exception e) {
            response.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
            return;
        }

        String name = request.getParameter("name");
        String username = request.getParameter("username");

        if (name.endsWith(".zip"))
        {
            Log.info( "Processing PUT request... ({} submitting to {})", request.getRemoteAddr(), request.getRequestURI() );
            response.setHeader( "Cache-Control", "max-age=31536000" );

            final Path path = Paths.get( ".", "upload-" + System.currentTimeMillis() + name);
            final OutputStream outStream = new BufferedOutputStream(Files.newOutputStream(path, java.nio.file.StandardOpenOption.CREATE ));
            final InputStream in = request.getInputStream();

            try {
                final byte[] buffer = new byte[ 1024 * 4 ];
                int bytesRead;

                while ( ( bytesRead = in.read( buffer ) ) != -1 )
                {
                    outStream.write( buffer, 0, bytesRead );
                }

                outStream.close();

                String source = path.toString();
                String folder = name = name.substring(0, name.indexOf(".zip"));
                String destination = JiveGlobals.getHomeDirectory() + File.separator + "resources" + File.separator + "spank" + File.separator + username + File.separator + folder;

                Log.info( "Extracting application..." + source + " " + destination);

                ZipFile zipFile = new ZipFile(path.toFile());
                zipFile.extractAll(destination);

                Files.deleteIfExists(path);

                String bookmarkValue = JiveGlobals.getProperty("ofmeet.root.url.secure", "https://" + XMPPServer.getInstance().getServerInfo().getHostname() + ":" + JiveGlobals.getProperty("httpbind.port.secure", "7443")) + "/" + username + "/" + folder;
                long id = -1;

                for (Bookmark bookmark : BookmarkManager.getBookmarks())
                {
                    if (bookmark.getValue().equals(bookmarkValue)) id = bookmark.getBookmarkID();
                }

                if (id == -1)
                {
                    new Bookmark(Bookmark.Type.url, folder, bookmarkValue, new ArrayList<String>(Arrays.asList(new String[] {username})), null);
                }

                response.setHeader( "Location", request.getRequestURL().toString() );
                response.setStatus( HttpServletResponse.SC_CREATED );

            } catch (Exception e) {
               Log.error("upload servlet", e);
               response.setStatus( HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            Log.warn("Application upload. " + name + " is not a zip file from " + username);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}