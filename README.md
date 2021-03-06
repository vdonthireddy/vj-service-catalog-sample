#### Key Commands [Click here for details](#full-details-with-explanation):
```sh
helm init --upgrade
kubectl create clusterrolebinding tiller-cluster-admin --clusterrole=cluster-admin --serviceaccount=kube-system:default

helm repo add svc-cat https://svc-catalog-charts.storage.googleapis.com
helm install svc-cat/catalog --name catalog --namespace catalog

helm repo add azure https://kubernetescharts.blob.core.windows.net/azure
helm install azure/open-service-broker-azure --name osba --namespace osba --set azure.subscriptionId=**_AZURE SUBSCRIPTION ID_** --set azure.tenantId=**_AZURE TENANT ID_** --set azure.clientId=**_AZURE CLIENT ID_** --set azure.clientSecret=**_AZURE CLIENT SECRET_** --set modules.minStability=experimental

kubectl create -f azure-postgres-instance.yaml
kubectl create -f azure-postgres-binding.yaml
kubectl create -f aks.yaml
```

#### Full details with explanation:
Create an account in [Microsoft Azure](https://azure.microsoft.com)

[Install the Azure CLI](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli?view=azure-cli-latest)
```sh
az login
```
```sh
az account show
```
```sh
az aks get-credentials --resource-group <resource group name> --name <AKS cluster name>
```
First, check your version of kubectl:
```sh
kubectl version
```
Ensure that the server version and client versions are both 1.9 or above.
If you don’t have Helm installed already, download the [helm CLI](https://github.com/kubernetes/helminstall) and then run the following command (this installs Tiller, the server-side component of Helm, into your Kubernetes cluster).
```sh
helm init --upgrade
```
Tiller will need to be configured with cluster-admin access to properly install Service Catalog:
```sh
kubectl create clusterrolebinding tiller-cluster-admin --clusterrole=cluster-admin --serviceaccount=kube-system:default
```
Helm Repository Setup:
```sh
helm repo add svc-cat https://svc-catalog-charts.storage.googleapis.com
```
```sh
helm search service-catalog
```
Now that your cluster and Helm are configured properly, installing Service Catalog is simple:
```sh
helm install svc-cat/catalog --name catalog --namespace catalog
```
```sh
kubectl get apiservice
```

Installing the Service Catalog CLI
```sh
brew update
```
```sh
brew install kubernetes-service-catalog-client
```

To use svcat as a plugin, run the following command after downloading:
```sh
svcat install plugin
```

Start by adding the Open Service Broker for Azure Helm repository:
```sh
helm repo add azure https://kubernetescharts.blob.core.windows.net/azure
```

Create a Service Principal with the following Azure CLI command:
```sh
az ad sp create-for-rbac
```

Output should be similar to the following. Take note of the appId, password, and tenant values, which you use in the next step.
```sh
{
  "appId": "2s87as18-1234-4s69-b3s4-82qwer7d76d5",
  "displayName": "azure-cli-2018-09-19-03-16-46",
  "name": "http://azure-cli-2018-09-19-03-16-46",
  "password": "1s944s1d-1235-1234-9s8d-52528ss7s98s",
  "tenant": "15sss6s1-d335-1234-b6f9-7s1234s09876"
}
```

Set the following environment variables with the preceding values:
```sh
AZURE_CLIENT_ID=<appId>
AZURE_CLIENT_SECRET=<password>
AZURE_TENANT_ID=<tenant>
```

Now, get your Azure subscription ID:
```sh
az account show --query id --output tsv
```

Again, set the following environment variable with the preceding value:
```sh
AZURE_SUBSCRIPTION_ID=[your Azure subscription ID from above]
```

Now that you've populated these environment variables, execute the following command to install the Open Service Broker for Azure using the Helm chart (Note: use --set modules.minStability=experimental for experimental services):
```sh
helm install azure/open-service-broker-azure --name osba --namespace osba --set azure.subscriptionId=$AZURE_SUBSCRIPTION_ID --set azure.tenantId=$AZURE_TENANT_ID --set azure.clientId=$AZURE_CLIENT_ID --set azure.clientSecret=$AZURE_CLIENT_SECRET
```

If you want to get all the experimental classes as well, use the following command
```sh
helm install azure/open-service-broker-azure --name osba --namespace osba --set azure.subscriptionId=$AZURE_SUBSCRIPTION_ID --set azure.tenantId=$AZURE_TENANT_ID --set azure.clientId=$AZURE_CLIENT_ID --set azure.clientSecret=$AZURE_CLIENT_SECRET --set modules.minStability=experimental
```

If you haven't already installed the ingress controller in the kube-system namespace, execute the following command to install it:
```sh
helm install stable/nginx-ingress --namespace kube-system
```

Now, list installed service brokers:
```sh
svcat get brokers
```

You should see output similar to the following:
```sh
NAME                               URL                                STATUS
+------+--------------------------------------------------------------+--------+
  osba   http://osba-open-service-broker-azure.osba.svc.cluster.local   Ready
```

List the available service classes.
```sh
svcat get classes
```

List all available service plans
```sh
svcat get plans
```

```sh
kubectl create -f azure-postgres-instance.yaml
```

```sh
svcat get instances
```
Please wait until you see the STATUS "Ready"

```sh
kubectl create -f azure-postgres-binding.yaml
```

```sh
svcat get bindings
```
Please wait until you see the STATUS "Ready"

```sh
svcat describe instance azure-postgresql-instance
```

```sh
svcat describe binding app-azure-postgresql-binding
```

```sh
kubectl get secrets
```

```sh
kubectl describe secret azure-postgresql-secret
```

```sh
kubectl get secret azure-postgresql-secret -o yaml
```

```sh
echo 'NTQzMg==' | base64 -D
```
Once the setup of service catalog is complete, and postgres instance and binding is created, run the following to deploy and test your application connecting to postgres instance:

Note: Before you run the following commands, please take a look at aks.yaml and allrun.sh files to make sure you have all the secrets created and comment/uncomment the sections based on your requirement.

```sh
mvn clean install
```

```sh
docker rmi -f k8-svcat-azure-client
```
```sh
docker build -t k8-svcat-azure-client .
```
```sh
docker tag k8-svcat-azure-client vdonthireddy/k8-svcat-azure-client:2.0
```
```sh
docker push vdonthireddy/k8-svcat-azure-client:2.0
```

```sh
kubectl delete deploy k8-svcat-azure-client-deployment & kubectl delete service k8-svcat-azure-client-service
```
```sh
kubectl apply -f aks.yaml
```

```sh
kubectl get po,deploy,svc
```
```sh
kubectl get svc -w
```

References:

[Service Catalog](https://kubernetes.io/docs/concepts/extend-kubernetes/service-catalog/)

[Integrate with Azure-managed services using Open Service Broker for Azure](https://docs.microsoft.com/en-us/azure/aks/integrate-azure)

[Kubernetes ConfigMaps and Secrets](https://medium.com/google-cloud/kubernetes-configmaps-and-secrets-68d061f7ab5b)

[Azure Database for PostgreSQL](https://github.com/Azure/open-service-broker-azure/blob/master/docs/modules/postgresql.md)

[Azure Redis Cache](https://github.com/Azure/open-service-broker-azure/blob/master/docs/modules/rediscache.md)
