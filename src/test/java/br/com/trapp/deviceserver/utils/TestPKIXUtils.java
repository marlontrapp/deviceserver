package br.com.trapp.deviceserver.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import br.com.trapp.deviceserver.model.TrustedCertificates;

public class TestPKIXUtils {

    @Test
    public void testValidCertificate() throws CertificateException, CertPathValidatorException,
	    InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {

	InputStream certStream = new ByteArrayInputStream(TrustedCertificates.DEV_CERT.getBytes());
	Security.addProvider(new BouncyCastleProvider());
	PKIXUtils pkix = new PKIXUtils();
	pkix.verifyCertificateValidity(certStream);
    }

    @Test(expected = CertificateException.class)
    public void testRevokedCertificate() throws CertificateException, CertPathValidatorException,
	    InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {

	String cert = "-----BEGIN CERTIFICATE-----\n"
		+ "MIIHATCCAumgAwIBAgIBAjANBgkqhkiG9w0BAQsFADB/MQswCQYDVQQGEwJCUjEX\n"
		+ "MBUGA1UECAwOU2FudGEgQ2F0YXJpbmExFjAUBgNVBAcMDUZsb3JpYW5vcG9saXMx\n"
		+ "DjAMBgNVBAoMBVRyYXBwMRQwEgYDVQQLDAtEZXZlbG9wbWVudDEZMBcGA1UEAwwQ\n"
		+ "RGV2aWNlIFNlcnZlciBDQTAeFw0xNjAzMDMxOTExNTVaFw0xNzAzMDMxOTExNTVa\n"
		+ "MHYxCzAJBgNVBAYTAkJSMRcwFQYDVQQIDA5TYW50YSBDYXRhcmluYTEWMBQGA1UE\n"
		+ "BwwNRmxvcmlhbm9wb2xpczEOMAwGA1UECgwFVHJhcHAxFDASBgNVBAsMC0RldmVs\n"
		+ "b3BtZW50MRAwDgYDVQQDDAdSZXZva2VkMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A\n"
		+ "MIIBCgKCAQEA798aYFJD9o7tENK4SfJjc+NJMMFFUDzfEICeQHE9FA5xo0nODDnq\n"
		+ "2rzYTPBtLnU+n4WurP4wYpudZWvbVRn6uNZiKQ0B0UTGZZbr18ZlGXyKX7wQtQ3X\n"
		+ "xq3VFnjlCBelUA9UppfLeMoGvlgZm/35ZrivSc8IRsiXblV2TEtd0zPPvSx3I8LN\n"
		+ "vIXljlLH/0hCFMA0HHLRSS8hzgJYezxnLUzSiA4ufAG26czcSYRKudnj5J88U6Kt\n"
		+ "EzICOLviz7y2mkhkIoPfZuzpWVf2Mut+isce5fBKM51cy5sXaiscUnC4rI3bFOiw\n"
		+ "JO36+7PUSw0q77gGzjiK5JsSzWv6bUbu+wIDAQABo4GQMIGNMAkGA1UdEwQCMAAw\n"
		+ "HQYDVR0OBBYEFNz3/OkNcdd/725kbWNeAMFz0LQcMB8GA1UdIwQYMBaAFKnucAga\n"
		+ "mf7uE8Gl9E1zH07SucIVMEAGA1UdHwQ5MDcwNaAzoDGGL2h0dHA6Ly93d3cuaW5m\n"
		+ "LnVmc2MuYnIvfm1hcmxvbi50cmFwcC9jYS1jcmwucGVtMA0GCSqGSIb3DQEBCwUA\n"
		+ "A4IEAQA2o96DNzO+sImSfMMUYp1WqwR/0sXqFEcmbeWNcuSCbWMBmi9Zb9StZdzw\n"
		+ "dmY77vIokamdbzY2mgOveOOFVyZD1VjqBO5QUwiQtGgAKOEqKl8HGpfK+99yjjy4\n"
		+ "u7ZJTdNGj4CF1+haRpRo3AEQyY797821n1W59cy1bTDhCyFK9bBo9HAFSet6ccBY\n"
		+ "nPXOmZZorH67G5lZvj5OXGmMjODTDT/SbiFV524AxzTKVfGjruHf093Pk73AlrCC\n"
		+ "+WIG1Ke+dD1rKXAnXI3p7N6m/maJBS1RG5QP+bXgTfc2YlKEjOtknqjEaVarcZLU\n"
		+ "vodejzH/7xvJ2qRnxfBe/rJJWb0rsXM0cXnAm3fROtpG673zLvYComL0PustAXdQ\n"
		+ "Rz95ZSFtRdMrxAXNEG+1wb1UD/v7mwUfZ4nDVod5d5FvvYhb4HRF4WvobOouyUFK\n"
		+ "5cAT5HO6HeP/TUMaHMxynsOfmlYIyMY3uhUTDNDf+vSjdDn2RhFMIZUKH47mFA9c\n"
		+ "lnZdyUsgFTqjQD+hTBEW+n0GnQ0vIwQbvOK1srTQmAZnMPXsmUscgwoSxt/HfdPN\n"
		+ "U1//YSz8T/mQLk8WkmRT68RJ+7ckp0YpZfAsquydjXJ404ictrlo2C+mYjH2HM3z\n"
		+ "BKtzFBLJ2dB/QY//OGnzw0kOgZv1OVWBcTAXMl90TE3mCZYzWDA0e9ZoGV4zXg1h\n"
		+ "7MwG2Uqffv47sztnaIXhJIxWpANNg9Am66tFy8JtHbtduzsWkfCQV2HX8F9cZe/r\n"
		+ "4PXTvgR1DspdfLgxLq87Z7HIopqVIDBNazqEyIJ+uVdpIajGZQ5UkRi8UcvXI5Yk\n"
		+ "pFtxtBqBJm69GRLDFLRXPkN35vfqMiUgiuCU1S0FbyDqoOSjN0JhOdW+8zz6xwZ+\n"
		+ "7qlbNj3/VFjh4OLmLIFI5DqUSLs1NCH3QvrbUOJIaQ6rj24gOa4hEC89sE9OVES/\n"
		+ "XOR+TqUw1cJpses9SZyU8nIBP1KE8WjS4tyRq/vrgR9lF0a2JDGl8sOwox3LyGP7\n"
		+ "uos9i5ZIlj6qOjbtKd8FrXf3j0dzqBP7hX3WC4mfLs5zI/2PqM/ZbTzngSazh+OZ\n"
		+ "xj8PL6Cm3sKu9xpyfccATm/7/wCUODZK+RnknpHq+DCf+ghV3PxoxMz56Xj9Udpo\n"
		+ "ZdA7RMqEn+ClEeLKHOXGcN7aqiRmZSncSvtl58p6qu9bCBQIqibZBueLWe83m0GY\n"
		+ "u/wH7jW8LCO+et1+FU4dF2C1sC6+HPZUsXeeSJ3rbdUIDB3rTzPutdmBWRSxzOVc\n"
		+ "7JKy5fLBXVqn+1fzLseUVuyfjbmog5jDIaUlUrAzasj6b0AKj/JgAXB/ZZibXWhE\n"
		+ "EHO98n0r3C22eXXczevGQrTdG+ZP\n" + "-----END CERTIFICATE-----";
	InputStream certStream = new ByteArrayInputStream(cert.getBytes());
	Security.addProvider(new BouncyCastleProvider());
	PKIXUtils pkix = new PKIXUtils();
	pkix.verifyCertificateValidity(certStream);
    }

    @Test(expected = CertificateException.class)
    public void testCertificateNotSignedByTrusted() throws CertificateException, CertPathValidatorException,
	    InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {

	String cert = "-----BEGIN CERTIFICATE-----\n"
		+ "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\n"
		+ "l+xdPoETxppZwlIRbio2QY4AFw==\n" + "-----END CERTIFICATE-----";
	InputStream certStream = new ByteArrayInputStream(cert.getBytes());
	Security.addProvider(new BouncyCastleProvider());
	PKIXUtils pkix = new PKIXUtils();
	pkix.verifyCertificateValidity(certStream);
    }
}
