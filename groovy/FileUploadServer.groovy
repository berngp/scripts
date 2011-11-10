#!/usr/local/share/groovy/bin/groovy

@GrabResolver(name='codehaus-release-repo', root='http://repository.codehaus.org')
@Grab(group='org.mortbay.jetty', module='jetty-embedded', version='6.1.11')

import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.mortbay.jetty.*
import org.mortbay.jetty.servlet.*

// some very poor cli setup
def port =  ( this.args.length > 0 ? this.args[0] : 1234 ) as Integer
// setup the server and context...
def server = new Server( port )
def root = new Context( server,"/",Context.SESSIONS)
root.setResourceBase( '.' )
//root.addServlet( new ServletHolder( new TemplateServlet()), "*.html")
//we add the File Upload Servlet
root.addServlet( new ServletHolder( 
         new HttpServlet () {

            protected void doGet( HttpServletRequest req, HttpServletResponse resp ) //throws ServletException, IOException
            {
                String result = "not expected" 
                File file = req.getAttribute( "afile" ) as File
                if( !file || !file.exists() ) {
                    result = "File does not exist"
                }
                else if( file.isDirectory()) {
                    result = "File is a directory"
                }
                else {
                    File outputFile =  req.getParameter( "afile" ) as File
                    file.renameTo( outputFile )
                    result = "File successfully uploaded." 
                }

                resp.contentType = "application/json"
                resp.writer << """{ "result":"$result" }"""
            }

            protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
                doGet( req, resp )
            }
        }),
        '/fileupload'
)

//We add the filter
def multipartFilterHolder = new FilterHolder( new org.mortbay.servlet.MultiPartFilter() )
multipartFilterHolder.setInitParameters( [ 'deleteFiles': 'true' ] )
root.addFilter(multipartFilterHolder, '/fileupload', 0) 

//and last but not least ... start the server
println "Strarting File Upload Server on port $port"
server.start()
