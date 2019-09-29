/*
 * Copyright (c) 2019 SIMBA Chain Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.simbachain.simba;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *  Metadata about a file in a bundle. At a minimum this contains the name, mime type
 *  and size of a file. It may additionally contain the hash of the file, the hash algorithm
 *  and a uid.
 */
public class ManifestFile {

    
    @JsonProperty
    private String name;
    @JsonProperty
    private String mimetype;
    @JsonProperty
    private long size;
    @JsonProperty
    private String hash;
    @JsonProperty
    private String uid;
    @JsonProperty
    private String alg;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAlg() {
        return alg;
    }

    public void setAlg(String alg) {
        this.alg = alg;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ManifestFile{");
        sb.append("name='")
          .append(name)
          .append('\'');
        sb.append(", mimetype='")
          .append(mimetype)
          .append('\'');
        sb.append(", size=")
          .append(size);
        sb.append(", hash='")
          .append(hash)
          .append('\'');
        sb.append(", uid='")
          .append(uid)
          .append('\'');
        sb.append(", alg='")
          .append(alg)
          .append('\'');
        sb.append('}');
        return sb.toString();
    }
}
