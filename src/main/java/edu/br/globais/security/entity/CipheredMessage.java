package edu.br.globais.security.entity;

import java.io.Serializable;

public class CipheredMessage implements Serializable {
    private String name;
    private byte[] cipheredText;
    private byte[] iv;
    private byte[] tag;

    public CipheredMessage(byte[] cipheredText, byte[] iv, byte[] tag) {
        this.cipheredText = cipheredText;
        this.iv = iv;
        this.tag = tag;
    }

    public CipheredMessage(String name, byte[] cipheredText, byte[] iv, byte[] tag) {
        this.name = name;
        this.cipheredText = cipheredText;
        this.iv = iv;
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getCipheredText() {
        return cipheredText;
    }

    public void setCipheredText(byte[] cipheredText) {
        this.cipheredText = cipheredText;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public byte[] getTag() {
        return tag;
    }

    public void setTag(byte[] tag) {
        this.tag = tag;
    }
}
