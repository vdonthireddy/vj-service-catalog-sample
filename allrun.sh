mvn clean install

docker rmi -f k8-svcat-azure-client
docker build -t k8-svcat-azure-client .
docker tag k8-svcat-azure-client vdonthireddy/k8-svcat-azure-client:2.0
docker push vdonthireddy/k8-svcat-azure-client:2.0

kubectl delete deploy k8-svcat-azure-client-deployment
kubectl delete service k8-svcat-azure-client-service
kubectl apply -f aks.yml

kubectl get po,deploy,svc
kubectl get svc -w
