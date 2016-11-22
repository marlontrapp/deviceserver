package br.com.trapp.deviceserver.model;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class TrustedCertificates {

    public static final String CA_CERT = "-----BEGIN CERTIFICATE-----\n"+
	    "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\n"+
	    "-----END CERTIFICATE-----";

    public static final String DEV_CERT = "-----BEGIN CERTIFICATE-----\n"+
	    "MIIHCjCCAvKgAwIBAgIBATANBgkqhkiG9w0BAQsFADB/MQswCQYDVQQGEwJCUjEX\n"+
	    "MBUGA1UECAwOU2FudGEgQ2F0YXJpbmExFjAUBgNVBAcMDUZsb3JpYW5vcG9saXMx\n"+
	    "DjAMBgNVBAoMBVRyYXBwMRQwEgYDVQQLDAtEZXZlbG9wbWVudDEZMBcGA1UEAwwQ\n"+
	    "RGV2aWNlIFNlcnZlciBDQTAeFw0xNjAzMDEyMjI2NTVaFw0xNzAzMDEyMjI2NTVa\n"+
	    "MH8xCzAJBgNVBAYTAkJSMRcwFQYDVQQIDA5TYW50YSBDYXRhcmluYTEWMBQGA1UE\n"+
	    "BwwNRmxvcmlhbm9wb2xpczEOMAwGA1UECgwFVHJhcHAxFDASBgNVBAsMC0RldmVs\n"+
	    "b3BtZW50MRkwFwYDVQQDDBBNb2R1bGUgRGV2ZWxvcGVyMIIBIjANBgkqhkiG9w0B\n"+
	    "AQEFAAOCAQ8AMIIBCgKCAQEAxVT3HkeCJmmb+Wd4njG7iHTY0IX8A/sTq4crE3E0\n"+
	    "936TnXsxi07L2nPsNZqx+rF8K5bHM4G8SsQGF4mzIh1U8Kzs7qBniDJTXI6s0vsa\n"+
	    "EF14sg2kI/RG7RC9wPjMfe4yupbUT6q79YNZ7RZSJU3LNXDb68P4E/I4oR5o6Atm\n"+
	    "MCSzThHknn+io2PsXntala/39rQzXygibbBWrF1XsH205MPxhaR0fUP9cq00nKrT\n"+
	    "8Mq+tbaM195I54HPAntMwqOVjH5wQYiAo7HSMJnePARO97Qtd/uTWoRIlwd84W/D\n"+
	    "D2h85F8FEH8KdZAPVLsiqWYyry1xOpjmnwHP0v4J13/2swIDAQABo4GQMIGNMAkG\n"+
	    "A1UdEwQCMAAwHQYDVR0OBBYEFDTFnKH7gUauBvIOIFniKPjsmoW1MB8GA1UdIwQY\n"+
	    "MBaAFKnucAgamf7uE8Gl9E1zH07SucIVMEAGA1UdHwQ5MDcwNaAzoDGGL2h0dHA6\n"+
	    "Ly93d3cuaW5mLnVmc2MuYnIvfm1hcmxvbi50cmFwcC9jYS1jcmwucGVtMA0GCSqG\n"+
	    "SIb3DQEBCwUAA4IEAQCxLwWRgLy0eqRD22FwadTmfOWf8RUGHhXCoqDrnbo5MmYm\n"+
	    "jwe91LznTmOEFZw53dQP9boCVfmf/WC7kCBd2eIAFs7H56ld0CK/bHzfgiTlA03D\n"+
	    "2S1bTEjbmqzFSnTVSFeXAXawrPwbnb31zwSSi8uwHucGpfw5xMOInBPoz6BUznJo\n"+
	    "jJiFLwVm0pQrXXIUa9fD9ikSDpqX4v3MFf9vDzatUUb3UzW9aVSb2dlt52kUzAPn\n"+
	    "PFGSvWDJkMjbcEHvhfdHM0gXCG1liV1qNZdWKpNC0CDpZN//jSgTquD2irDIPYYr\n"+
	    "m0fdTDIXiuBEXeyox49gCT+qiTzjaaUtTgmhJPykAJ+rokjMeXUPxELCMD8mnxOr\n"+
	    "mWyDcnm3jqLY3VLOVE8wNUqZf9x9QuTk0ZqGRZehaQLXDhjxMsoe8TaxpTc7jnBm\n"+
	    "ScqiWoxWsXLwVZ4AlhPLbVyEqUSE2ZQ7dBaIOCA3pBE0SueXbODXgRlMbP+FfmV3\n"+
	    "86Z6k43ZkN4aDV3B2g8gHhD90uq12R0ttuYzF2xdgpssN+l6CYVWjcLc7u4t2sJQ\n"+
	    "Eo63pUv2tSYoOZshwYJa/cXgcrZv4Dyq+V/Z0nIXy7K9P0fqjxpvYcBRjOyHa8HO\n"+
	    "rto/C7JrkYJdF199wK82xWmc/RS+URkua9R7q7yjV9qprOxNf+oftDxBPLFo/elL\n"+
	    "6cYCmUnFvoO/8LZaOCOFb/A8u+FNJEbIiDWYDFAL74Nv4l1BhB9aO9O/f1mJ6EKR\n"+
	    "F1soyO+Oc/l3hGBlkj7ELWi3uais+k9B9hPMwUFJKz6waiFRhUbcrehLjmnMm2P7\n"+
	    "F+Wlu2wwyrFKuF+sMnkeP7igVy/EeK9GlWfHHTMxFclx2NPnZKirnT2bx/CPoGJm\n"+
	    "YXp4sWEiTf6YyENTqJMXYxoMXT53ZwXqygscOTougtgqFCE/t93Z2nhrJDdSaBRk\n"+
	    "an8Pz01yGtn9VyoTYM4bJC+nPqrr/de41kkiE37ahKf868lHQG7LsJgtn4CCRYHS\n"+
	    "KvPKqHh69cz2igRFU9qZ77GhFBIk08iHnBTwloxHVBpzVT1Dz3TpMOiaz9WzkYq0\n"+
	    "dB24R0NrcJ5k0gOrptBnziS4v5FzIL2Oh/inL7LH3nGEX5/EiG12kgv2fC/s9H40\n"+
	    "pErotw6DZjJ1/lkn7qCLbRhHgWEFirZr0wRZBukjYmqad+H3R8f0/aodV1vAG8An\n"+
	    "LtXmyLJ5svvj/bMFjQCDnscWroLosegXH03DOaw2PCFKGvm+eEQeCek8PfRXls6X\n"+
	    "HZ8RcPhl0YkbTBpTLu3EYcgRDXLbXRhBYH+3kYKJBhMKyGGfV8VQSv4AEV32kyWM\n"+
	    "osGe3D9dc7YXiDYNG2j+OlAgTNOen4M2NCg3HmrX\n"+
	    "-----END CERTIFICATE-----";

    public static X509Certificate getCaCert() throws CertificateException {
	ByteArrayInputStream byteArrIStream = new ByteArrayInputStream(CA_CERT.getBytes());
	CertificateFactory cf = CertificateFactory.getInstance("X.509");
	return (X509Certificate) cf.generateCertificate(byteArrIStream);
    }

    public static X509Certificate getDevCert() throws CertificateException {
	ByteArrayInputStream byteArrIStream = new ByteArrayInputStream(DEV_CERT.getBytes());
	CertificateFactory cf = CertificateFactory.getInstance("X.509");
	return (X509Certificate) cf.generateCertificate(byteArrIStream);
    }

}
