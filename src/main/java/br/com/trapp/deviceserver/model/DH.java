package br.com.trapp.deviceserver.model;

import java.math.BigInteger;
import java.util.Random;

public class DH {

    private BigInteger y;
    private BigInteger x;
    private BigInteger p;
    private BigInteger k;
    private BigInteger g;

    public DH(BigInteger p, BigInteger g) {
	this.g = g;
	this.p = p;
	Random random = new Random();
	do {
	    x = new BigInteger(p.bitCount(), random);
	} while (x.compareTo(p) >= 0);
	this.y = g.modPow(x, p);
    }

    public BigInteger getY() {
	return this.y;
    }

    public void setOtherY(BigInteger y2) {
	this.k = y2.modPow(x, p);
    }

    public BigInteger getSharedSecret() {
	return this.k;
    }

    public BigInteger getP() {
	return this.p;
    }

    public BigInteger getG() {
	return this.g;
    }
}
