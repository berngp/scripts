#!/usr/local/bin/groovyclient
//proudly brewed with vim
import java.security.MessageDigest
def md5Strategy = { file ->
    MessageDigest md = MessageDigest.getInstance('MD5')
    BigInteger number = new BigInteger(1, md.digest( file.newInputStream().bytes ))
    number.toString(16).padLeft( 32, '0' )
}

def afile = args[0] as File
assert afile.exists()

println "Using md5 over file $afile.path:${md5Strategy(afile)}"

