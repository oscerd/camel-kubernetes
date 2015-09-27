/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.kubernetes;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;

import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriParam;

public class KubernetesConfiguration {

    @UriParam
    @Metadata(required = "true")
    private String masterUrl;

    @UriParam(label = "producer")
    @Metadata(required = "true")
    private String category;

    @UriParam
    private DefaultKubernetesClient kubernetesClient;

    @UriParam
    private String username;

    @UriParam
    private String password;

    @UriParam(label = "producer")
    private String operation;

    @UriParam
    private String apiVersion;

    @UriParam
    private String caCertData;

    @UriParam
    private String caCertFile;

    @UriParam
    private String clientCertData;

    @UriParam
    private String clientCertFile;

    @UriParam
    private String clientKeyAlgo;

    @UriParam
    private String clientKeyData;

    @UriParam
    private String clientKeyFile;

    @UriParam
    private String clientKeyPassphrase;

    @UriParam
    private String oauthToken;

    @UriParam
    private Boolean trustCerts;
    
    @UriParam(label = "consumer")
    private String namespaceName;

    public String getMasterUrl() {
        return masterUrl;
    }

    public void setMasterUrl(String masterUrl) {
        this.masterUrl = masterUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public DefaultKubernetesClient getKubernetesClient() {
        return kubernetesClient;
    }

    public void setKubernetesClient(DefaultKubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getCaCertData() {
        return caCertData;
    }

    public void setCaCertData(String caCertData) {
        this.caCertData = caCertData;
    }

    public String getCaCertFile() {
        return caCertFile;
    }

    public void setCaCertFile(String caCertFile) {
        this.caCertFile = caCertFile;
    }

    public String getClientCertData() {
        return clientCertData;
    }

    public void setClientCertData(String clientCertData) {
        this.clientCertData = clientCertData;
    }

    public String getClientCertFile() {
        return clientCertFile;
    }

    public void setClientCertFile(String clientCertFile) {
        this.clientCertFile = clientCertFile;
    }

    public String getClientKeyAlgo() {
        return clientKeyAlgo;
    }

    public void setClientKeyAlgo(String clientKeyAlgo) {
        this.clientKeyAlgo = clientKeyAlgo;
    }

    public String getClientKeyData() {
        return clientKeyData;
    }

    public void setClientKeyData(String clientKeyData) {
        this.clientKeyData = clientKeyData;
    }

    public String getClientKeyFile() {
        return clientKeyFile;
    }

    public void setClientKeyFile(String clientKeyFile) {
        this.clientKeyFile = clientKeyFile;
    }

    public String getClientKeyPassphrase() {
        return clientKeyPassphrase;
    }

    public void setClientKeyPassphrase(String clientKeyPassphrase) {
        this.clientKeyPassphrase = clientKeyPassphrase;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public Boolean getTrustCerts() {
        return trustCerts;
    }

    public void setTrustCerts(Boolean trustCerts) {
        this.trustCerts = trustCerts;
    }

	public String getNamespaceName() {
		return namespaceName;
	}

	public void setNamespaceName(String namespaceName) {
		this.namespaceName = namespaceName;
	}

	@Override
	public String toString() {
		return "KubernetesConfiguration [masterUrl=" + masterUrl
				+ ", category=" + category + ", kubernetesClient="
				+ kubernetesClient + ", username=" + username + ", password="
				+ password + ", operation=" + operation + ", apiVersion="
				+ apiVersion + ", caCertData=" + caCertData + ", caCertFile="
				+ caCertFile + ", clientCertData=" + clientCertData
				+ ", clientCertFile=" + clientCertFile + ", clientKeyAlgo="
				+ clientKeyAlgo + ", clientKeyData=" + clientKeyData
				+ ", clientKeyFile=" + clientKeyFile + ", clientKeyPassphrase="
				+ clientKeyPassphrase + ", oauthToken=" + oauthToken
				+ ", trustCerts=" + trustCerts + ", namespaceName="
				+ namespaceName + "]";
	}
}
