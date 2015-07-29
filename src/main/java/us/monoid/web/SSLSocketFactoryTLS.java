package us.monoid.web;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * Created by drakegebhart on 7/29/15. From http://stackoverflow.com/questions/1037590/which-cipher-suites-to-enable-for-ssl-socket/23365536#23365536
 */
class SSLSocketFactoryTLS extends SSLSocketFactory
{
    public SSLSocketFactoryTLS() throws NoSuchAlgorithmException, KeyManagementException
    {
        initSSLSocketFactoryEx(null,null,null);
    }

    public SSLSocketFactoryTLS(KeyManager[] km, TrustManager[] tm, SecureRandom random) throws NoSuchAlgorithmException, KeyManagementException
    {
        initSSLSocketFactoryEx(km, tm, random);
    }

    public SSLSocketFactoryTLS(SSLContext ctx) throws NoSuchAlgorithmException, KeyManagementException
    {
        initSSLSocketFactoryEx(ctx);
    }

    public String[] getDefaultCipherSuites()
    {
        return m_ciphers;
    }

    public String[] getSupportedCipherSuites()
    {
        return m_ciphers;
    }

    public String[] getDefaultProtocols()
    {
        return m_protocols;
    }

    public String[] getSupportedProtocols()
    {
        return m_protocols;
    }

    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException
    {
        SSLSocketFactory factory = m_ctx.getSocketFactory();
        SSLSocket ss = (SSLSocket)factory.createSocket(s, host, port, autoClose);

        ss.setEnabledProtocols(m_protocols);
        ss.setEnabledCipherSuites(m_ciphers);

        return ss;
    }

    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException
    {
        SSLSocketFactory factory = m_ctx.getSocketFactory();
        SSLSocket ss = (SSLSocket)factory.createSocket(address, port, localAddress, localPort);

        ss.setEnabledProtocols(m_protocols);
        ss.setEnabledCipherSuites(m_ciphers);

        return ss;
    }

    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException
    {
        SSLSocketFactory factory = m_ctx.getSocketFactory();
        SSLSocket ss = (SSLSocket)factory.createSocket(host, port, localHost, localPort);

        ss.setEnabledProtocols(m_protocols);
        ss.setEnabledCipherSuites(m_ciphers);

        return ss;
    }

    public Socket createSocket(InetAddress host, int port) throws IOException
    {
        SSLSocketFactory factory = m_ctx.getSocketFactory();
        SSLSocket ss = (SSLSocket)factory.createSocket(host, port);

        ss.setEnabledProtocols(m_protocols);
        ss.setEnabledCipherSuites(m_ciphers);

        return ss;
    }

    public Socket createSocket(String host, int port) throws IOException
    {
        SSLSocketFactory factory = m_ctx.getSocketFactory();
        SSLSocket ss = (SSLSocket)factory.createSocket(host, port);

        ss.setEnabledProtocols(m_protocols);
        ss.setEnabledCipherSuites(m_ciphers);

        return ss;
    }

    private void initSSLSocketFactoryEx(KeyManager[] km, TrustManager[] tm, SecureRandom random)
            throws NoSuchAlgorithmException, KeyManagementException
    {
        m_ctx = SSLContext.getInstance("TLSv1.2");
        m_ctx.init(km, tm, random);

        m_protocols = GetProtocolList();
        m_ciphers = GetCipherList();
    }

    private void initSSLSocketFactoryEx(SSLContext ctx)
            throws NoSuchAlgorithmException, KeyManagementException
    {
        m_ctx = ctx;

        m_protocols = GetProtocolList();
        m_ciphers = GetCipherList();
    }

    protected String[] GetProtocolList()
    {
        String[] preferredProtocols = {"TLSv1.1", "TLSv1.2", "TLSv1.3"};
        String[] availableProtocols = null;

        SSLSocket socket = null;

        try
        {
            SSLSocketFactory factory = m_ctx.getSocketFactory();
            socket = (SSLSocket)factory.createSocket();

            availableProtocols = socket.getSupportedProtocols();
            Arrays.sort(availableProtocols);
        }
        catch(Exception e)
        {
            return new String[]{ "TLSv1.1" };
        }
        finally
        {
            if(socket != null)
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        List<String> aa = new ArrayList<String>();
        for(int i = 0; i < preferredProtocols.length; i++)
        {
            int idx = Arrays.binarySearch(availableProtocols, preferredProtocols[i]);
            if(idx >= 0)
                aa.add(preferredProtocols[i]);
        }

        return aa.toArray(new String[0]);
    }

    protected String[] GetCipherList()
    {
        String[] preferredCiphers = {

                // *_CHACHA20_POLY1305 are 3x to 4x faster than existing cipher suites.
                //   http://googleonlinesecurity.blogspot.com/2014/04/speeding-up-and-strengthening-https.html
                // Use them if available. Normative names can be found at (TLS spec depends on IPSec spec):
                //   http://tools.ietf.org/html/draft-nir-ipsecme-chacha20-poly1305-01
                //   http://tools.ietf.org/html/draft-mavrogiannopoulos-chacha-tls-02
//                "TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305",
//                "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305",
//                "TLS_ECDHE_ECDSA_WITH_CHACHA20_SHA",
//                "TLS_ECDHE_RSA_WITH_CHACHA20_SHA",

//                "TLS_DHE_RSA_WITH_CHACHA20_POLY1305",
//                "TLS_RSA_WITH_CHACHA20_POLY1305",
//                "TLS_DHE_RSA_WITH_CHACHA20_SHA",
//                "TLS_RSA_WITH_CHACHA20_SHA",

                // Done with bleeding edge, back to TLS v1.2 and below
                "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA",
                "TLS_DHE_DSS_WITH_AES_128_GCM_SHA",
                "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA",
                "TLS_DHE_DSS_WITH_AES_256_GCM_SHA",

                "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
                "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",
                "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA"



        };

        String[] availableCiphers = null;

        try
        {
            SSLSocketFactory factory = m_ctx.getSocketFactory();
            availableCiphers = factory.getSupportedCipherSuites();
            Arrays.sort(availableCiphers);
        }
        catch(Exception e)
        {
            return new String[] {
                    "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
                    "TLS_DHE_DSS_WITH_AES_256_CBC_SHA",
                    "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
                    "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
                    "TLS_RSA_WITH_AES_256_CBC_SHA256",
                    "TLS_RSA_WITH_AES_256_CBC_SHA",
                    "TLS_RSA_WITH_AES_128_CBC_SHA256",
                    "TLS_RSA_WITH_AES_128_CBC_SHA",
                    "TLS_EMPTY_RENEGOTIATION_INFO_SCSV"
            };
        }

        List<String> aa = new ArrayList<String>();
        for(int i = 0; i < preferredCiphers.length; i++)
        {
            int idx = Arrays.binarySearch(availableCiphers, preferredCiphers[i]);
            if(idx >= 0)
                aa.add(preferredCiphers[i]);
        }

        aa.add("TLS_EMPTY_RENEGOTIATION_INFO_SCSV");

        return aa.toArray(new String[0]);
    }

    private SSLContext m_ctx;

    private String[] m_ciphers;
    private String[] m_protocols;
}