@Grapes([
    @Grab('commons-codec:commons-codec:1.5'),
    @GrabConfig(systemClassLoader=true, initContextClassLoader=true)
])

import java.security.GeneralSecurityException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.net.ssl.*
import java.security.cert.*
import java.nio.charset.Charset
import org.apache.commons.codec.binary.Base64
 
final class SSLUtilities {
   
   private static HostnameVerifier _hostnameVerifier
   private static TrustManager[] _trustManagers
 
   /**
    * Set the default Hostname Verifier to an instance of a fake class that trust all hostnames.
    */
   private static void _trustAllHostnames() {
       // Create a trust manager that does not validate certificate chains
        _hostnameVerifier =  _hostnameVerifier ?: new FakeHostnameVerifier()
        // Install the all-trusting host name verifier:
       HttpsURLConnection.defaultHostnameVerifier = _hostnameVerifier
   }
   
   /**
    * Set the default X509 Trust Manager to an instance of a fake class that 
    * trust all certificates, even the self-signed ones.
    */
   private static void _trustAllHttpsCertificates() {
       SSLContext context
       // Create a trust manager that does not validate certificate chains
       _trustManagers = _trustManagers ?:  [ new FakeX509TrustManager() ]
       // Install the all-trusting trust manager:
       try {
           context = SSLContext.getInstance("SSL")
           context.init(null, _trustManagers, new SecureRandom())
       } catch(GeneralSecurityException gse) {
           throw new IllegalStateException("Unable to create the SSL Context", gse)
       } 
       HttpsURLConnection.setDefaultSSLSocketFactory(context.socketFactory)
   } 
 
   /**
    * Set the default Hostname Verifier to an instance of a fake class that trust all hostnames.
    */
   static void trustAllHostnames() { _trustAllHostnames() } 
   
   /**
    * Set the default X509 Trust Manager to an instance of a fake class that 
    * trust all certificates, even the self-signed ones.
    */
   static void trustAllHttpsCertificates() { _trustAllHttpsCertificates() } 
   
   static def readSSLCertFromUrl(String urlString) {
      trustAllHttpsCertificates() 
      trustAllHostnames() 
      URL hostURL = urlString.toURL()
      int port = hostURL.getPort() != -1 ?: 443       
      SSLSocketFactory factory = HttpsURLConnection.defaultSSLSocketFactory  
      
      SSLSocket socket = factory.createSocket(hostURL.host, port)        
        if ( socket ){
          try{
            socket.startHandshake()                
            Certificate[] serverCerts = socket.session.peerCertificates          
            File storeDir = new File("${hostURL.host}${hostURL.port > 0 ? "_${hostURL.port}": ''}")
            storeDir.mkdir()
            // The local certificate first followed by any certificate authorities.  
            serverCerts?.eachWithIndex { item, index ->
                println item 
                println "Public Key:${item.publicKey}"
                File certFile = new File(storeDir, "${index}.crt")    
                certFile.withWriter("UTF-8"){ wr ->
                    Base64 encoder = new Base64(64, '\n'.bytes)
                    String encodedCert = encoder.encodeAsString(item.encoded).trim()

                    wr.writeLine "-----BEGIN CERTIFICATE-----"
                    wr.writeLine encodedCert
                    wr.writeLine "-----END CERTIFICATE-----"
                }
            }            
          } finally {  
            socket.close()
          }  
       }        
   }
   
   /**
    * This class implements a fake hostname verificator, trusting any host name.
    */
   static class FakeHostnameVerifier implements HostnameVerifier {
       
       /**
        * Always return true, indicating that the host name is an acceptable match with the server's authentication scheme.
        *
        * @param hostname        the host name.
        * @param session         the SSL session used on the connection to host.
        * @return                the true boolean value indicating the host name is trusted.
        */
       boolean verify(String hostname, javax.net.ssl.SSLSession session) { true } 
   } 
 
 
   /**
    * This class allow any X509 certificates to be used to authenticate the remote side of a secure socket, including self-signed certificates.
    */
   static class FakeX509TrustManager implements X509TrustManager {
 
       /** Empty array of certificate authority certificates.  */
       private static final X509Certificate[] _acceptedIssuers = []
 
       /**
        * Always trust for client SSL chain peer certificate chain with any authType authentication types.
        *
        * @param chain           the peer certificate chain.
        * @param authType        the authentication type based on the client certificate.
        */
       void checkClientTrusted(X509Certificate[] chain, String authType) { } 
       
       /**
        * Always trust for server SSL chain peer certificate chain with any authType exchange algorithm types.
        *
        * @param chain           the peer certificate chain.
        * @param authType        the key exchange algorithm used.
        */
       void checkServerTrusted(X509Certificate[] chain, String authType) { } 
       
       /**
        * Return an empty array of certificate authority certificates which are trusted for authenticating peers.
        *
        * @return a empty array of issuer certificates.
        */
       X509Certificate[] getAcceptedIssuers() { _acceptedIssuers } 
   } 
 } 

SSLUtilities.readSSLCertFromUrl("https://sso-internal.itg.mu.gazint")
SSLUtilities.readSSLCertFromUrl("https://sso.itg.mu.gazint")
