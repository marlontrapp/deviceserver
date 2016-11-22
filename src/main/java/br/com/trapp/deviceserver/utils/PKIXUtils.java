package br.com.trapp.deviceserver.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CRLParser;
import org.bouncycastle.x509.extension.X509ExtensionUtil;

import br.com.trapp.deviceserver.model.TrustedCertificates;

public class PKIXUtils {

    /**
     * Verify if the certificate is valid
     * 
     * @param pemEncodedCert
     *            The PEM encoded certificate
     * 
     * @throws CertificateException
     *             if the certificate is not valid or no encoded correctly
     */
    public void verifyCertificate(String pemEncodedCert) throws CertificateException {
	try {
	    ByteArrayInputStream byteArrIStream = new ByteArrayInputStream(pemEncodedCert.getBytes());
	    CertificateFactory cf = CertificateFactory.getInstance("X.509");
	    this.verifyCertificate((X509Certificate) cf.generateCertificate(byteArrIStream));
	} catch (CertificateException e) {
	    throw new CertificateException("The certificate is not correctly encoded", e);
	}
    }

    public void verifyCertificate(X509Certificate cert) throws CertificateException {
	try {

	} catch (Exception e) {
	    throw new CertificateException("The certificate is correctly encoded", e);
	}
    }

    /**
     * Verifies the validity of the certificate
     * 
     * @param certificate
     * @throws CertificateException
     *             if some error occurred during the validations, meaning that
     *             the certificate is invalid
     */
    @SuppressWarnings("unchecked")
    public void verifyCertificateValidity(InputStream certificatesInputStream) throws CertificateException {
	CertificateFactory cf = CertificateFactory.getInstance("X.509");
	Collection<? extends Certificate> certs = cf.generateCertificates(certificatesInputStream);
	X509Certificate trust = TrustedCertificates.getCaCert();
	List<URL> urls = this.getCRLURLs(trust);
	if (urls.isEmpty()) {
	    throw new CertificateException("None CRL was found");
	}
	Collection<X509CRL> crls = new ArrayList<X509CRL>();
	CertPath cp = cf.generateCertPath(new ArrayList<>(certs));
	X509CRLParser parser = new X509CRLParser();
	int index = 0;
	int max = urls.size() - 1;
	try {
	    parser.engineInit(urls.get(index).openStream());
	    crls.addAll(parser.engineReadAll());
	} catch (Exception e) {
	    if (index == max)
		throw new CertificateException("All CRLs are invalids", e);
	}
	this.verifyCertificate(trust, cp, crls);
    }

    /**
     * Method to get the URL of the CRL from the certificate
     * 
     * @param cert
     *            the certificate where the CRLs are
     * @return an list o URL of CRLs
     * @throws CertificateException
     *             if is not possible to get the cRLDistributionPoints extension
     */
    private List<URL> getCRLURLs(X509Certificate cert) throws CertificateException {
	List<URL> crlStreames = new ArrayList<URL>();
	byte[] crlDistPointBytes = cert.getExtensionValue(Extension.cRLDistributionPoints.getId());
	try {
	    ASN1Primitive ext = X509ExtensionUtil.fromExtensionValue(crlDistPointBytes);
	    CRLDistPoint crlsDistPoint = CRLDistPoint.getInstance(ext);
	    DistributionPoint[] distPoints = crlsDistPoint.getDistributionPoints();
	    for (DistributionPoint distributionPoint : distPoints) {
		DistributionPointName distPointName = distributionPoint.getDistributionPoint();
		GeneralNames generalNames = GeneralNames.getInstance(distPointName.getName());
		GeneralName[] names = generalNames.getNames();
		for (GeneralName generalName : names) {
		    if (generalName.getTagNo() == GeneralName.uniformResourceIdentifier) {
			URI uri = new URI(((DERIA5String) generalName.getName()).getString());
			URL urlFromURI = uri.toURL();
			crlStreames.add(urlFromURI);
		    }
		}
	    }
	} catch (IOException e) {
	    throw new CertificateException("The CRLDistributionPoint extension is invalid", e);
	} catch (URISyntaxException e) {
	    throw new CertificateException("The URI in CRLDistributionPoint extension is invalid", e);
	}
	return crlStreames;
    }

    /**
     * Verify the cert path
     * 
     * @param trust
     *            the trusted anchor
     * @param cp
     *            the cert path to validate
     * @param crls
     *            the list of crls
     * 
     * @throws CertificateException
     *             if the certificate path is not valid
     */
    public void verifyCertificate(X509Certificate trust, CertPath cp, Collection<X509CRL> crls)
	    throws CertificateException {
	try {
	    TrustAnchor anchor = new TrustAnchor(trust, null);
	    PKIXParameters params = new PKIXParameters(Collections.singleton(anchor));
	    params.setRevocationEnabled(true);
	    CertStoreParameters revoked = new CollectionCertStoreParameters(crls);
	    params.addCertStore(CertStore.getInstance("Collection", revoked));
	    CertPathValidator cpv = CertPathValidator.getInstance("PKIX", BouncyCastleProvider.PROVIDER_NAME);
	    cpv.validate(cp, params);
	} catch (Exception e) {
	    throw new CertificateException("The certificate is not valid", e);
	}
    }

}
